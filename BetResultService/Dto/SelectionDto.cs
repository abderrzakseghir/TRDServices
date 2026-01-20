using Newtonsoft.Json;

namespace BetResultService.Dto;

public class SelectionDto
{
	[JsonProperty("matchId")]
	public string MatchId { get; set; }

	[JsonProperty("marketName")]
	public string MarketName { get; set; }

	[JsonProperty("selectionName")]
	public string SelectionName { get; set; }

	[JsonProperty("odd")]
	public decimal Odd { get; set; }
}
