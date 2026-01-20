using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using ScoreService;
using ScoreService.Data;
using ScoreService.Services;

var builder = Host.CreateApplicationBuilder(args);

builder.Services.AddDbContext<AppDbContext>(options =>
{
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection"));
});

builder.Services.AddHttpClient();
builder.Services.AddSingleton<FootballDataService>();
builder.Services.AddSingleton<RabbitMqProducer>();
builder.Services.AddHostedService<Worker>();

var host = builder.Build();

using (var scope = host.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    var logger = scope.ServiceProvider.GetRequiredService<ILogger<Program>>();
    
    // Retry logic pour attendre que PostgreSQL soit prêt
    int retries = 10;
    int delay = 3000; // 3 secondes
    
    for (int i = 0; i < retries; i++)
    {
        try
        {
            logger.LogInformation($"Tentative de migration de la base de données ({i + 1}/{retries})...");
            dbContext.Database.Migrate();
            logger.LogInformation("Migration de la base de données réussie !");
            break;
        }
        catch (Exception ex)
        {
            if (i == retries - 1)
            {
                logger.LogError(ex, "Échec de la migration après toutes les tentatives");
                throw;
            }
            
            logger.LogWarning($"Migration échouée: {ex.Message}. Nouvelle tentative dans {delay/1000}s...");
            Thread.Sleep(delay);
        }
    }
}

host.Run();

