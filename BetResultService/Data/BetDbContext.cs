using BetResultService.Entities;
using Microsoft.EntityFrameworkCore;

namespace BetResultService.Data;

public class BetDbContext : DbContext
{
	public DbSet<BetEntity> Bets { get; set; }

	public DbSet<BetSelection> BetSelections { get; set; }

	public BetDbContext(DbContextOptions<BetDbContext> options)
		: base(options)
	{
	}

	protected override void OnModelCreating(ModelBuilder modelBuilder)
	{
		modelBuilder.Entity<BetEntity>().HasMany((BetEntity b) => b.Selections).WithOne()
			.HasForeignKey("BetEntityId")
			.OnDelete(DeleteBehavior.Cascade);
		modelBuilder.Entity<BetEntity>().Property((BetEntity b) => b.Amount).HasPrecision(18, 2);
		modelBuilder.Entity<BetEntity>().Property((BetEntity b) => b.Payout).HasPrecision(18, 2);
		modelBuilder.Entity<BetSelection>().Property((BetSelection s) => s.Odd).HasPrecision(18, 2);
		base.OnModelCreating(modelBuilder);
	}
}
