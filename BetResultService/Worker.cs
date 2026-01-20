using System;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using BetResultService.Dto;
using BetResultService.Entities;
using BetResultService.Services;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;

namespace BetResultService;

public class Worker : BackgroundService
{
	private readonly IServiceProvider _serviceProvider;

	private readonly ILogger<Worker> _logger;

	private IConnection _connection;

	private IChannel _channel;

	private readonly IConfiguration _config;

	public Worker(IServiceProvider serviceProvider, ILogger<Worker> logger, IConfiguration config)
	{
		_serviceProvider = serviceProvider;
		_logger = logger;
		_config = config;
	}

	protected override async Task ExecuteAsync(CancellationToken stoppingToken)
	{
		ConnectionFactory factory = new ConnectionFactory
		{
			HostName = _config["RabbitMqSettings:HostName"],
			UserName = "user",
			Password = "password"
		};
		while (_channel == null)
		{
			try
			{
				if (_connection == null || !_connection.IsOpen)
				{
					_connection = await factory.CreateConnectionAsync();
				}

				if (_channel == null)
				{
					_channel = await _connection.CreateChannelAsync();
				}
			}
			catch (Exception ex)
			{
				_logger.LogWarning("RabbitMQ pas encore prêt (" + ex.Message + "), nouvelle tentative dans 5s...");
				await Task.Delay(5000, stoppingToken);
			}

			if (stoppingToken.IsCancellationRequested)
			{
				return;
			}
		}
		await _channel.ExchangeDeclareAsync("sportsbook.topic", "topic", durable: true);
		string queueBets = "q.bet-result.new-bets";
		await _channel.QueueDeclareAsync(queueBets, durable: true, exclusive: false, autoDelete: false);
		await _channel.QueueBindAsync(queueBets, "sportsbook.topic", "bet.placed");
		string queueScores = "q.bet-result.match-scores";
		await _channel.QueueDeclareAsync(queueScores, durable: true, exclusive: false, autoDelete: false);
		await _channel.QueueBindAsync(queueScores, "sportsbook.topic", "match.finished");
		AsyncEventingBasicConsumer consumer = new AsyncEventingBasicConsumer(_channel);
		consumer.ReceivedAsync += async delegate(object model, BasicDeliverEventArgs ea)
		{
			byte[] body = ea.Body.ToArray();
			string message = Encoding.UTF8.GetString(body);
			string routingKey = ea.RoutingKey;
			_logger.LogInformation("Message re\ufffdu [" + routingKey + "]");
			using IServiceScope scope = _serviceProvider.CreateScope();
			BetProcessor processor = scope.ServiceProvider.GetRequiredService<BetProcessor>();
			if (routingKey == "bet.placed")
			{
				BetDto betData = JsonConvert.DeserializeObject<BetDto>(message);
				BetEntity entity = new BetEntity
				{
					ExternalBetId = betData.BetId,
					AccountId = betData.AccountId,
					Amount = betData.Amount,
					Selections = betData.Selections.Select((SelectionDto s) => new BetSelection
					{
						MatchId = int.Parse(s.MatchId),
						SelectionName = s.SelectionName,
						Odd = s.Odd
					}).ToList()
				};
				await processor.ProcessNewBetAsync(entity);
			}
			else if (routingKey == "match.finished")
			{
				dynamic matchData = JsonConvert.DeserializeObject(message);
				await processor.ProcessMatchResultAsync((int)matchData.matchId, (string)matchData.homeTeam, (string)matchData.awayTeam, (int)matchData.homeScore, (int)matchData.awayScore);
			}
		};
		await _channel.BasicConsumeAsync(queueBets, autoAck: true, consumer);
		await _channel.BasicConsumeAsync(queueScores, autoAck: true, consumer);
		await Task.Delay(-1, stoppingToken);
	}
}
