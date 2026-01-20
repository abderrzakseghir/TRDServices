using System;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using RabbitMQ.Client;
using ScoreService.Events;

namespace ScoreService.Services;

public class RabbitMqProducer
{
	private readonly IConfiguration _config;

	private readonly ILogger<RabbitMqProducer> _logger;

	public RabbitMqProducer(IConfiguration config, ILogger<RabbitMqProducer> logger)
	{
		_config = config;
		_logger = logger;
	}

	public Task PublishMatchFinishedAsync(MatchFinishedEvent matchEvent)
	{
		try
		{
			var factory = new ConnectionFactory
			{
				HostName = _config["RabbitMqSettings:HostName"],
				UserName = "user",
				Password = "password"
			};
			
			// Simple retry policy
			using (var connection = CreateConnectionWithRetry(factory))
            {
                if (connection == null) return Task.CompletedTask;

                using (var channel = connection.CreateModel())
                {
                    string exchangeName = "sportsbook.topic";
                    channel.ExchangeDeclare(exchangeName, "topic", durable: true);
                    string json = JsonConvert.SerializeObject(matchEvent);
                    byte[] body = Encoding.UTF8.GetBytes(json);
                    string routingKey = "match.finished";
                    var props = channel.CreateBasicProperties();
                    props.ContentType = "application/json";
                    channel.BasicPublish(exchangeName, routingKey, props, body);
                    _logger.LogInformation($"[RabbitMQ] Événement publié : Match {matchEvent.MatchId} terminé.");
                }
            }
		}
		catch (Exception exception)
		{
			_logger.LogError(exception, "[RabbitMQ] Erreur lors de la publication du message.");
		}
		return Task.CompletedTask;
	}

    private IConnection CreateConnectionWithRetry(ConnectionFactory factory)
    {
        int retries = 0;
        while (retries < 5)
        {
            try
            {
                return factory.CreateConnection();
            }
            catch (Exception)
            {
                retries++;
                Thread.Sleep(2000); // Wait 2s
            }
        }
        _logger.LogError("[RabbitMQ] Impossible de se connecter après plusieurs tentatives.");
        return null; // Let caller handle null
    }
}
