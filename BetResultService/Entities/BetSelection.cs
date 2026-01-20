namespace BetResultService.Entities;

public class BetSelection
{
	public int Id { get; set; }

	public int MatchId { get; set; }

	public string SelectionName { get; set; }

	public decimal Odd { get; set; }

	public string Status { get; set; } = "PENDING";
}
