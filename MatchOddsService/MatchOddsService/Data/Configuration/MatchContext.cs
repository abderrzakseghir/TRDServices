using MatchOddsService.Data.Models;
using Microsoft.EntityFrameworkCore;


namespace MatchOddsService.Data.Configuration;

public class MatchContext : DbContext
{
    public MatchContext(DbContextOptions<MatchContext> options) : base(options) { }

    public DbSet<Match> Matches { get; set; }
    public DbSet<Team> Teams { get; set; }
    public DbSet<Odd> Odds { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        // Configuration de la relation 1-1 entre Match et Odd
        modelBuilder.Entity<Match>()
            .HasOne(m => m.Odds)
            .WithOne(o => o.Match)
            .HasForeignKey<Odd>(o => o.MatchId);
    }
}