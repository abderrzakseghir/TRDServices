using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace MockScoreService.API.Migrations
{
    /// <inheritdoc />
    public partial class InitialCreate : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Competitions",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    Name = table.Column<string>(type: "TEXT", nullable: false),
                    Code = table.Column<string>(type: "TEXT", nullable: false),
                    Type = table.Column<string>(type: "TEXT", nullable: false),
                    Emblem = table.Column<string>(type: "TEXT", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Competitions", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Seasons",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    StartDate = table.Column<DateTime>(type: "TEXT", nullable: false),
                    EndDate = table.Column<DateTime>(type: "TEXT", nullable: false),
                    CurrentMatchday = table.Column<int>(type: "INTEGER", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Seasons", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Teams",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    Name = table.Column<string>(type: "TEXT", nullable: false),
                    ShortName = table.Column<string>(type: "TEXT", nullable: false),
                    Tla = table.Column<string>(type: "TEXT", nullable: false),
                    Crest = table.Column<string>(type: "TEXT", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Teams", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Matches",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    UtcDate = table.Column<DateTime>(type: "TEXT", nullable: false),
                    Status = table.Column<string>(type: "TEXT", nullable: false),
                    Matchday = table.Column<int>(type: "INTEGER", nullable: false),
                    Stage = table.Column<string>(type: "TEXT", nullable: false),
                    Group = table.Column<string>(type: "TEXT", nullable: true),
                    LastUpdated = table.Column<DateTime>(type: "TEXT", nullable: false),
                    CompetitionId = table.Column<int>(type: "INTEGER", nullable: false),
                    SeasonId = table.Column<int>(type: "INTEGER", nullable: false),
                    HomeTeamId = table.Column<int>(type: "INTEGER", nullable: false),
                    AwayTeamId = table.Column<int>(type: "INTEGER", nullable: false),
                    HomeScore = table.Column<int>(type: "INTEGER", nullable: true),
                    AwayScore = table.Column<int>(type: "INTEGER", nullable: true),
                    HalfTimeHomeScore = table.Column<int>(type: "INTEGER", nullable: true),
                    HalfTimeAwayScore = table.Column<int>(type: "INTEGER", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Matches", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Matches_Competitions_CompetitionId",
                        column: x => x.CompetitionId,
                        principalTable: "Competitions",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Matches_Seasons_SeasonId",
                        column: x => x.SeasonId,
                        principalTable: "Seasons",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Matches_Teams_AwayTeamId",
                        column: x => x.AwayTeamId,
                        principalTable: "Teams",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                    table.ForeignKey(
                        name: "FK_Matches_Teams_HomeTeamId",
                        column: x => x.HomeTeamId,
                        principalTable: "Teams",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Restrict);
                });

            migrationBuilder.InsertData(
                table: "Competitions",
                columns: new[] { "Id", "Code", "Emblem", "Name", "Type" },
                values: new object[] { 1, "FL1", "https://crests.football-data.org/FL1.png", "Ligue 1", "LEAGUE" });

            migrationBuilder.InsertData(
                table: "Seasons",
                columns: new[] { "Id", "CurrentMatchday", "EndDate", "StartDate" },
                values: new object[] { 1, 20, new DateTime(2025, 5, 31, 0, 0, 0, 0, DateTimeKind.Unspecified), new DateTime(2024, 8, 1, 0, 0, 0, 0, DateTimeKind.Unspecified) });

            migrationBuilder.InsertData(
                table: "Teams",
                columns: new[] { "Id", "Crest", "Name", "ShortName", "Tla" },
                values: new object[,]
                {
                    { 1, "https://crests.football-data.org/524.png", "Paris Saint-Germain FC", "Paris SG", "PSG" },
                    { 2, "https://crests.football-data.org/516.png", "Olympique de Marseille", "Marseille", "MAR" },
                    { 3, "https://crests.football-data.org/523.png", "Olympique Lyonnais", "Lyon", "LYO" },
                    { 4, "https://crests.football-data.org/548.png", "AS Monaco FC", "Monaco", "MON" },
                    { 5, "https://crests.football-data.org/521.png", "LOSC Lille", "Lille", "LIL" },
                    { 6, "https://crests.football-data.org/522.png", "OGC Nice", "Nice", "NIC" },
                    { 7, "https://crests.football-data.org/529.png", "Stade Rennais FC", "Rennes", "REN" },
                    { 8, "https://crests.football-data.org/546.png", "RC Lens", "Lens", "LEN" }
                });

            migrationBuilder.InsertData(
                table: "Matches",
                columns: new[] { "Id", "AwayScore", "AwayTeamId", "CompetitionId", "Group", "HalfTimeAwayScore", "HalfTimeHomeScore", "HomeScore", "HomeTeamId", "LastUpdated", "Matchday", "SeasonId", "Stage", "Status", "UtcDate" },
                values: new object[,]
                {
                    { 1, null, 2, 1, null, null, null, null, 1, new DateTime(2026, 1, 20, 17, 6, 55, 878, DateTimeKind.Utc).AddTicks(3919), 20, 1, "REGULAR_SEASON", "SCHEDULED", new DateTime(2026, 1, 21, 17, 6, 55, 878, DateTimeKind.Utc).AddTicks(3903) },
                    { 2, null, 4, 1, null, null, null, null, 3, new DateTime(2026, 1, 20, 17, 6, 55, 878, DateTimeKind.Utc).AddTicks(3923), 20, 1, "REGULAR_SEASON", "SCHEDULED", new DateTime(2026, 1, 21, 20, 6, 55, 878, DateTimeKind.Utc).AddTicks(3921) },
                    { 3, null, 6, 1, null, null, null, null, 5, new DateTime(2026, 1, 20, 17, 6, 55, 878, DateTimeKind.Utc).AddTicks(3926), 20, 1, "REGULAR_SEASON", "SCHEDULED", new DateTime(2026, 1, 22, 17, 6, 55, 878, DateTimeKind.Utc).AddTicks(3924) }
                });

            migrationBuilder.CreateIndex(
                name: "IX_Matches_AwayTeamId",
                table: "Matches",
                column: "AwayTeamId");

            migrationBuilder.CreateIndex(
                name: "IX_Matches_CompetitionId",
                table: "Matches",
                column: "CompetitionId");

            migrationBuilder.CreateIndex(
                name: "IX_Matches_HomeTeamId",
                table: "Matches",
                column: "HomeTeamId");

            migrationBuilder.CreateIndex(
                name: "IX_Matches_SeasonId",
                table: "Matches",
                column: "SeasonId");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "Matches");

            migrationBuilder.DropTable(
                name: "Competitions");

            migrationBuilder.DropTable(
                name: "Seasons");

            migrationBuilder.DropTable(
                name: "Teams");
        }
    }
}
