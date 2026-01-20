namespace MatchOddsService.Data.Models;

public class Match
{
    public int Id { get; set; }
    public DateTime MatchDate { get; set; }
    public string Status { get; set; } = "Scheduled"; // Scheduled, Live, Finished

    // Relations Équipes
    public int HomeTeamId { get; set; }
    public Team? HomeTeam { get; set; }

    public int AwayTeamId { get; set; }
    public Team? AwayTeam { get; set; }

    // Relation Cote (One-to-One)
    public Odd? Odds { get; set; }
}