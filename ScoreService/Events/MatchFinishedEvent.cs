using System;
using Newtonsoft.Json;

namespace ScoreService.Events;

public class MatchFinishedEvent
{
	[JsonProperty("matchId")]
	public int MatchId { get; set; }

	[JsonProperty("homeTeam")]
	public string HomeTeam { get; set; } = string.Empty;

	[JsonProperty("awayTeam")]
	public string AwayTeam { get; set; } = string.Empty;

	[JsonProperty("homeScore")]
	public int HomeScore { get; set; }

	[JsonProperty("awayScore")]
	public int AwayScore { get; set; }

	[JsonProperty("status")]
	public string Status { get; set; } = string.Empty;

	[JsonProperty("occurredAt")]
	public DateTime OccurredAt { get; set; }
}
