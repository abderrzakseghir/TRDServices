using System.Collections.Generic;

namespace ScoreService.Dtos;

public class ApiResponse
{
	public List<MatchDto> Matches { get; set; }
}
