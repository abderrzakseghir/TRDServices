using Microsoft.EntityFrameworkCore;
using ScoreService.Entities;

namespace ScoreService.Data;

public class AppDbContext : DbContext
{
	public DbSet<MatchEntity> Matches { get; set; }

	public AppDbContext(DbContextOptions<AppDbContext> options)
		: base(options)
	{
	}

	protected override void OnModelCreating(ModelBuilder modelBuilder)
	{
		modelBuilder.Entity<MatchEntity>().Property((MatchEntity m) => m.Id).ValueGeneratedNever();
	}
}
	