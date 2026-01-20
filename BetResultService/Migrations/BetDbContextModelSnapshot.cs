using System;
using BetResultService.Data;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Metadata.Builders;

namespace BetResultService.Migrations;

[DbContext(typeof(BetDbContext))]
internal class BetDbContextModelSnapshot : ModelSnapshot
{
	protected override void BuildModel(ModelBuilder modelBuilder)
	{
		modelBuilder.HasAnnotation("ProductVersion", "10.0.0").HasAnnotation("Relational:MaxIdentifierLength", 63);
		modelBuilder.UseIdentityByDefaultColumns();
		modelBuilder.Entity("BetResultService.Entities.BetEntity", delegate(EntityTypeBuilder b)
		{
			b.Property<int>("Id").ValueGeneratedOnAdd().HasColumnType("integer");
			b.Property<int>("Id").UseIdentityByDefaultColumn();
			b.Property<int>("AccountId").HasColumnType("integer");
			b.Property<decimal>("Amount").HasPrecision(18, 2).HasColumnType("numeric(18,2)");
			b.Property<DateTime>("CreatedAt").HasColumnType("timestamp with time zone");
			b.Property<long>("ExternalBetId").HasColumnType("bigint");
			b.Property<decimal?>("Payout").HasPrecision(18, 2).HasColumnType("numeric(18,2)");
			b.Property<string>("Status").IsRequired().HasColumnType("text");
			b.HasKey("Id");
			b.ToTable("Bets");
		});
		modelBuilder.Entity("BetResultService.Entities.BetSelection", delegate(EntityTypeBuilder b)
		{
			b.Property<int>("Id").ValueGeneratedOnAdd().HasColumnType("integer");
			b.Property<int>("Id").UseIdentityByDefaultColumn();
			b.Property<int?>("BetEntityId").HasColumnType("integer");
			b.Property<int>("MatchId").HasColumnType("integer");
			b.Property<decimal>("Odd").HasPrecision(18, 2).HasColumnType("numeric(18,2)");
			b.Property<string>("SelectionName").IsRequired().HasColumnType("text");
			b.Property<string>("Status").IsRequired().HasColumnType("text");
			b.HasKey("Id");
			b.HasIndex("BetEntityId");
			b.ToTable("BetSelections");
		});
		modelBuilder.Entity("BetResultService.Entities.BetSelection", delegate(EntityTypeBuilder b)
		{
			b.HasOne("BetResultService.Entities.BetEntity", null).WithMany("Selections").HasForeignKey("BetEntityId")
				.OnDelete(DeleteBehavior.Cascade);
		});
		modelBuilder.Entity("BetResultService.Entities.BetEntity", delegate(EntityTypeBuilder b)
		{
			b.Navigation("Selections");
		});
	}
}
