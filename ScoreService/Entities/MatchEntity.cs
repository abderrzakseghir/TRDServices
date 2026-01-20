using System;

namespace ScoreService.Entities;

public class MatchEntity
{
	public int Id { get; set; }

	public string HomeTeam { get; set; }

	public string AwayTeam { get; set; }

	public int? HomeScore { get; set; }

	public int? AwayScore { get; set; }

	public string Status { get; set; }

	public DateTime MatchDate { get; set; }

	public DateTime LastUpdated { get; set; }
}
