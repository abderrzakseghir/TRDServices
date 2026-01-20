namespace MockScoreService.API.DTOs;

public class MatchesResponse
{
    public MatchFilters? Filters { get; set; }
    public ResultSet? ResultSet { get; set; }
    public Competition Competition { get; set; } = null!;
    public List<MatchDto> Matches { get; set; } = new();
}

public class MatchFilters
{
    public string? Season { get; set; }
    public string? Status { get; set; }
}

public class ResultSet
{
    public int Count { get; set; }
    public string First { get; set; } = "";
    public string Last { get; set; } = "";
    public int Played { get; set; }
}

public class Competition
{
    public int Id { get; set; }
    public string Name { get; set; } = "";
    public string Code { get; set; } = "";
    public string Type { get; set; } = "";
    public string Emblem { get; set; } = "";
}

public class Area
{
    public int Id { get; set; }
    public string Name { get; set; } = "";
    public string Code { get; set; } = "";
    public string Flag { get; set; } = "";
}

public class Season
{
    public int Id { get; set; }
    public string StartDate { get; set; } = "";
    public string EndDate { get; set; } = "";
    public int CurrentMatchday { get; set; }
    public string? Winner { get; set; }
}

public class TeamDto
{
    public int Id { get; set; }
    public string Name { get; set; } = "";
    public string ShortName { get; set; } = "";
    public string Tla { get; set; } = "";
    public string? Crest { get; set; }
}

public class ScoreDto
{
    public string? Winner { get; set; }
    public string Duration { get; set; } = "REGULAR";
    public ScoreDetailDto? FullTime { get; set; }
    public ScoreDetailDto? HalfTime { get; set; }
}

public class ScoreDetailDto
{
    public int? Home { get; set; }
    public int? Away { get; set; }
}

public class MatchDto
{
    public Area Area { get; set; } = new();
    public Competition Competition { get; set; } = new();
    public Season Season { get; set; } = new();
    public int Id { get; set; }
    public string UtcDate { get; set; } = "";
    public string Status { get; set; } = "";
    public int Matchday { get; set; }
    public string Stage { get; set; } = "";
    public string? Group { get; set; }
    public string LastUpdated { get; set; } = "";
    public TeamDto HomeTeam { get; set; } = new();
    public TeamDto AwayTeam { get; set; } = new();
    public ScoreDto Score { get; set; } = new();
    public List<object> Odds { get; set; } = new();
    public List<object> Referees { get; set; } = new();
}
