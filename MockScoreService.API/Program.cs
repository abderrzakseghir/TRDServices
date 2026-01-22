using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;
using MockScoreService.API.Data;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "Mock Football API",
        Version = "v1",
        Description = "API simulant les données football pour le service de scores. Permet de créer des matchs, équipes et simuler des résultats en temps réel.",
        Contact = new OpenApiContact
        {
            Name = "Football Mock Service"
        }
    });
});

// Configure SQLite
builder.Services.AddDbContext<FootballDbContext>(options =>
    options.UseSqlite(builder.Configuration.GetConnectionString("DefaultConnection")));

// Configure CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", policy =>
    {
        policy.AllowAnyOrigin()
              .AllowAnyMethod()
              .AllowAnyHeader();
    });
});

var app = builder.Build();

// Apply migrations automatically
using (var scope = app.Services.CreateScope())
{
    var dbContext = scope.ServiceProvider.GetRequiredService<FootballDbContext>();
    var logger = scope.ServiceProvider.GetRequiredService<ILogger<Program>>();
    
    try
    {
        logger.LogInformation("Applying database migrations...");
        dbContext.Database.Migrate();
        logger.LogInformation("Database migrations applied successfully!");
    }
    catch (Exception ex)
    {
        logger.LogError(ex, "An error occurred while applying migrations");
        throw;
    }
}

// Configure the HTTP request pipeline - Swagger enabled for all environments
app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "Mock Football API v1");
    c.RoutePrefix = "swagger";
    c.DocumentTitle = "Mock Football API - Swagger";
});

app.UseCors("AllowAll");
app.UseAuthorization();
app.MapControllers();

app.Run();
