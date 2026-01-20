using Microsoft.EntityFrameworkCore;
using MockScoreService.Web.Models;

namespace MockScoreService.Web.Data;

public class FootballDbContext : DbContext
{
    public FootballDbContext(DbContextOptions<FootballDbContext> options) : base(options)
    {
    }

    public DbSet<Team> Teams { get; set; }
    public DbSet<Competition> Competitions { get; set; }
    public DbSet<Season> Seasons { get; set; }
    public DbSet<Match> Matches { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        // Configuration des relations
        modelBuilder.Entity<Match>()
            .HasOne(m => m.HomeTeam)
            .WithMany()
            .HasForeignKey(m => m.HomeTeamId)
            .OnDelete(DeleteBehavior.Restrict);

        modelBuilder.Entity<Match>()
            .HasOne(m => m.AwayTeam)
            .WithMany()
            .HasForeignKey(m => m.AwayTeamId)
            .OnDelete(DeleteBehavior.Restrict);

        // Seed data
        SeedData(modelBuilder);
    }

    private void SeedData(ModelBuilder modelBuilder)
    {
        // Competition
        modelBuilder.Entity<Competition>().HasData(
            new Competition { Id = 1, Name = "Ligue 1", Code = "FL1", Type = "LEAGUE", Emblem = "https://crests.football-data.org/FL1.png" }
        );

        // Season
        modelBuilder.Entity<Season>().HasData(
            new Season
            {
                Id = 1,
                StartDate = new DateTime(2024, 8, 1),
                EndDate = new DateTime(2025, 5, 31),
                CurrentMatchday = 20
            }
        );

        // Teams
        var teams = new[]
        {
            new Team { Id = 1, Name = "Paris Saint-Germain FC", ShortName = "Paris SG", Tla = "PSG", Crest = "https://crests.football-data.org/524.png" },
            new Team { Id = 2, Name = "Olympique de Marseille", ShortName = "Marseille", Tla = "MAR", Crest = "https://crests.football-data.org/516.png" },
            new Team { Id = 3, Name = "Olympique Lyonnais", ShortName = "Lyon", Tla = "LYO", Crest = "https://crests.football-data.org/523.png" },
            new Team { Id = 4, Name = "AS Monaco FC", ShortName = "Monaco", Tla = "MON", Crest = "https://crests.football-data.org/548.png" },
            new Team { Id = 5, Name = "LOSC Lille", ShortName = "Lille", Tla = "LIL", Crest = "https://crests.football-data.org/521.png" },
            new Team { Id = 6, Name = "OGC Nice", ShortName = "Nice", Tla = "NIC", Crest = "https://crests.football-data.org/522.png" },
            new Team { Id = 7, Name = "Stade Rennais FC", ShortName = "Rennes", Tla = "REN", Crest = "https://crests.football-data.org/529.png" },
            new Team { Id = 8, Name = "RC Lens", ShortName = "Lens", Tla = "LEN", Crest = "https://crests.football-data.org/546.png" }
        };
        modelBuilder.Entity<Team>().HasData(teams);

        // Matches
        var matches = new[]
        {
            new Match
            {
                Id = 1,
                CompetitionId = 1,
                SeasonId = 1,
                UtcDate = DateTime.UtcNow.AddDays(1),
                Status = "SCHEDULED",
                Matchday = 20,
                Stage = "REGULAR_SEASON",
                HomeTeamId = 1,
                AwayTeamId = 2,
                LastUpdated = DateTime.UtcNow
            },
            new Match
            {
                Id = 2,
                CompetitionId = 1,
                SeasonId = 1,
                UtcDate = DateTime.UtcNow.AddDays(1).AddHours(3),
                Status = "SCHEDULED",
                Matchday = 20,
                Stage = "REGULAR_SEASON",
                HomeTeamId = 3,
                AwayTeamId = 4,
                LastUpdated = DateTime.UtcNow
            },
            new Match
            {
                Id = 3,
                CompetitionId = 1,
                SeasonId = 1,
                UtcDate = DateTime.UtcNow.AddDays(2),
                Status = "SCHEDULED",
                Matchday = 20,
                Stage = "REGULAR_SEASON",
                HomeTeamId = 5,
                AwayTeamId = 6,
                LastUpdated = DateTime.UtcNow
            }
        };
        modelBuilder.Entity<Match>().HasData(matches);
    }
}
