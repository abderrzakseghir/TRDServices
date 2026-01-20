using System.Collections.Generic;
using Newtonsoft.Json;

namespace BetResultService.Dto;

public class BetDto
{
	[JsonProperty("betId")]
	public long BetId { get; set; }

	[JsonProperty("accountId")]
	public int AccountId { get; set; }

	[JsonProperty("amount")]
	public decimal Amount { get; set; }

	[JsonProperty("selections")]
	public List<SelectionDto> Selections { get; set; } = new List<SelectionDto>();
}
