using System.Threading;
using System.Threading.Tasks;
using Microsoft.Extensions.Hosting;
using ScoreService.Services;

namespace ScoreService;

public class Worker : BackgroundService
{
	private readonly FootballDataService _footballService;

	public Worker(FootballDataService footballService)
	{
		_footballService = footballService;
	}

	protected override async Task ExecuteAsync(CancellationToken stoppingToken)
	{
		while (!stoppingToken.IsCancellationRequested)
		{
			await _footballService.FetchAndSaveLiveMatchesAsync();
			await Task.Delay(6000, stoppingToken);
		}
	}
}
