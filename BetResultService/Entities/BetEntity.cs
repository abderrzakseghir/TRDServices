using System;
using System.Collections.Generic;

namespace BetResultService.Entities;

public class BetEntity
{
	public int Id { get; set; }

	public long ExternalBetId { get; set; }

	public int AccountId { get; set; }

	public decimal Amount { get; set; }

	public string Status { get; set; } = "PENDING";

	public decimal? Payout { get; set; }

	public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

	public List<BetSelection> Selections { get; set; } = new List<BetSelection>();
}
