using MatchOddsService.Data.Configuration;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// Controllers
builder.Services.AddControllers();

// Swagger / OpenAPI
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// DbContext PostgreSQL
builder.Services.AddDbContext<MatchContext>(options =>
    options.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

// CORS
var frontendAppPolicy = "AllowFrontendApp";
builder.Services.AddCors(options =>
{
    options.AddPolicy(frontendAppPolicy, policy =>
    {
        policy.WithOrigins(
            "http://localhost:8080",
            "http://localhost:3000",
            "http://localhost:32780"
        )
        .AllowAnyHeader()
        .AllowAnyMethod();
    });
});

var app = builder.Build();

// Enable Swagger middleware
app.UseSwagger();

// Swagger UI configuration
app.UseSwaggerUI(options =>
{
    options.SwaggerEndpoint("/swagger/v1/swagger.json", "MatchOddsService API v1");
    options.RoutePrefix = string.Empty; // Swagger UI at the app's root (http://localhost:8080)
});

app.UseCors(frontendAppPolicy);



app.UseAuthorization();

app.MapControllers();

using (var scope = app.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<MatchContext>();
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

app.Run();

