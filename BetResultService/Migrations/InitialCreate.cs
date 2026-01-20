using System;
using BetResultService.Data;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Infrastructure;
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using Microsoft.EntityFrameworkCore.Migrations;
using Microsoft.EntityFrameworkCore.Migrations.Operations;
using Microsoft.EntityFrameworkCore.Migrations.Operations.Builders;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

namespace BetResultService.Migrations;

[DbContext(typeof(BetDbContext))]
[Migration("20251207013519_InitialCreate")]
public class InitialCreate : Migration
{
	protected override void Up(MigrationBuilder migrationBuilder)
	{
		migrationBuilder.CreateTable("Bets", delegate(ColumnsBuilder table)
		{
			OperationBuilder<AddColumnOperation> id = table.Column<int>("integer").Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn);
			OperationBuilder<AddColumnOperation> externalBetId = table.Column<long>("bigint");
			OperationBuilder<AddColumnOperation> accountId = table.Column<int>("integer");
			int? precision = 18;
			int? scale = 2;
			OperationBuilder<AddColumnOperation> amount = table.Column<decimal>("numeric(18,2)", null, null, rowVersion: false, null, nullable: false, null, null, null, null, null, null, precision, scale);
			OperationBuilder<AddColumnOperation> status = table.Column<string>("text");
			scale = 18;
			precision = 2;
			return new
			{
				Id = id,
				ExternalBetId = externalBetId,
				AccountId = accountId,
				Amount = amount,
				Status = status,
				Payout = table.Column<decimal>("numeric(18,2)", null, null, rowVersion: false, null, nullable: true, null, null, null, null, null, null, scale, precision),
				CreatedAt = table.Column<DateTime>("timestamp with time zone")
			};
		}, null, table =>
		{
			table.PrimaryKey("PK_Bets", x => x.Id);
		});
		migrationBuilder.CreateTable("BetSelections", delegate(ColumnsBuilder table)
		{
			OperationBuilder<AddColumnOperation> id = table.Column<int>("integer").Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn);
			OperationBuilder<AddColumnOperation> matchId = table.Column<int>("integer");
			OperationBuilder<AddColumnOperation> selectionName = table.Column<string>("text");
			int? precision = 18;
			int? scale = 2;
			return new
			{
				Id = id,
				MatchId = matchId,
				SelectionName = selectionName,
				Odd = table.Column<decimal>("numeric(18,2)", null, null, rowVersion: false, null, nullable: false, null, null, null, null, null, null, precision, scale),
				Status = table.Column<string>("text"),
				BetEntityId = table.Column<int>("integer", null, null, rowVersion: false, null, nullable: true)
			};
		}, null, table =>
		{
			table.PrimaryKey("PK_BetSelections", x => x.Id);
			table.ForeignKey("FK_BetSelections_Bets_BetEntityId", x => x.BetEntityId, "Bets", "Id", null, ReferentialAction.NoAction, ReferentialAction.Cascade);
		});
		migrationBuilder.CreateIndex("IX_BetSelections_BetEntityId", "BetSelections", "BetEntityId");
	}

	protected override void Down(MigrationBuilder migrationBuilder)
	{
		migrationBuilder.DropTable("BetSelections");
		migrationBuilder.DropTable("Bets");
	}

	protected override void BuildTargetModel(ModelBuilder modelBuilder)
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
