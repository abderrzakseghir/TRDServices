using System;
using System.Net.Http;
using System.Threading.Tasks;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;
using Newtonsoft.Json;
using ScoreService.Data;
using ScoreService.Dtos;
using ScoreService.Entities;
using ScoreService.Events;

namespace ScoreService.Services;

public class FootballDataService
{
	private readonly IHttpClientFactory _httpClientFactory;

	private readonly IServiceProvider _serviceProvider;

	private readonly IConfiguration _config;

	private readonly ILogger<FootballDataService> _logger;

	private readonly RabbitMqProducer _rabbitMqProducer;

	public FootballDataService(IHttpClientFactory httpClientFactory, IServiceProvider serviceProvider, IConfiguration config, ILogger<FootballDataService> logger, RabbitMqProducer rabbitMqProducer)
	{
		_httpClientFactory = httpClientFactory;
		_serviceProvider = serviceProvider;
		_config = config;
		_logger = logger;
		_rabbitMqProducer = rabbitMqProducer;
	}

	public async Task FetchAndSaveLiveMatchesAsync()
	{
		HttpClient client = _httpClientFactory.CreateClient();
		client.DefaultRequestHeaders.Add("X-Auth-Token", _config["ApiSettings:Token"]);
		string url = _config["ApiSettings:BaseUrl"] + "matches";
		try
		{
			HttpResponseMessage response = await client.GetAsync(url);
			if (!response.IsSuccessStatusCode)
			{
				_logger.LogError($"Erreur API: {response.StatusCode}");
				return;
			}
			ApiResponse data = JsonConvert.DeserializeObject<ApiResponse>(await response.Content.ReadAsStringAsync());
			using IServiceScope scope = _serviceProvider.CreateScope();
			AppDbContext context = scope.ServiceProvider.GetRequiredService<AppDbContext>();
			foreach (MatchDto matchDto in data.Matches)
			{
				MatchEntity existingMatch = await context.Matches.FindAsync(matchDto.Id);
				bool matchJustFinished = false;
				if (existingMatch == null)
				{
					MatchEntity newMatch = new MatchEntity
					{
						Id = matchDto.Id,
						HomeTeam = matchDto.HomeTeam.Name,
						AwayTeam = matchDto.AwayTeam.Name,
						Status = matchDto.Status,
						MatchDate = DateTime.Parse(matchDto.UtcDate).ToUniversalTime(),
						HomeScore = matchDto.Score.FullTime.Home,
						AwayScore = matchDto.Score.FullTime.Away,
						LastUpdated = DateTime.UtcNow
					};
					context.Matches.Add(newMatch);
				}
				else
				{
					if (existingMatch.Status != "FINISHED" && matchDto.Status == "FINISHED")
					{
						matchJustFinished = true;
					}
					if (existingMatch.Status != matchDto.Status || existingMatch.HomeScore != matchDto.Score.FullTime.Home || existingMatch.AwayScore != matchDto.Score.FullTime.Away)
					{
						existingMatch.Status = matchDto.Status;
						existingMatch.HomeScore = matchDto.Score.FullTime.Home;
						existingMatch.AwayScore = matchDto.Score.FullTime.Away;
						existingMatch.LastUpdated = DateTime.UtcNow;
						_logger.LogInformation($"Mise à jour match {matchDto.HomeTeam.Name}-{matchDto.AwayTeam.Name}: {matchDto.Score.FullTime.Home}-{matchDto.Score.FullTime.Away}");
					}
				}
				await context.SaveChangesAsync();
				if (matchJustFinished)
				{
					MatchFinishedEvent evt = new MatchFinishedEvent
					{
						MatchId = matchDto.Id,
						HomeTeam = matchDto.HomeTeam.Name,
						AwayTeam = matchDto.AwayTeam.Name,
						HomeScore = matchDto.Score.FullTime.Home.GetValueOrDefault(),
						AwayScore = matchDto.Score.FullTime.Away.GetValueOrDefault(),
						Status = "FINISHED",
						OccurredAt = DateTime.UtcNow
					};
					await _rabbitMqProducer.PublishMatchFinishedAsync(evt);
				}
			}
		}
		catch (Exception exception)
		{
			_logger.LogError(exception, "Exception lors de la récupération des scores");
		}
	}
}
