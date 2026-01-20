namespace MockScoreService.Web.Models;

public class Team
{
    public int Id { get; set; }
    public string Name { get; set; } = string.Empty;
    public string ShortName { get; set; } = string.Empty;
    public string Tla { get; set; } = string.Empty;
    public string? Crest { get; set; }
}

public class Competition
{
    public int Id { get; set; }
    public string Name { get; set; } = "Ligue 1";
    public string Code { get; set; } = "FL1";
    public string Type { get; set; } = "LEAGUE";
    public string Emblem { get; set; } = "";
}

public class Season
{
    public int Id { get; set; }
    public DateTime StartDate { get; set; }
    public DateTime EndDate { get; set; }
    public int CurrentMatchday { get; set; }
}

public class Match
{
    public int Id { get; set; }
    public DateTime UtcDate { get; set; }
    public string Status { get; set; } = "SCHEDULED";
    public int Matchday { get; set; }
    public string Stage { get; set; } = "REGULAR_SEASON";
    public string? Group { get; set; }
    public DateTime LastUpdated { get; set; }
    
    public int CompetitionId { get; set; }
    public Competition Competition { get; set; } = null!;
    
    public int SeasonId { get; set; }
    public Season Season { get; set; } = null!;
    
    public int HomeTeamId { get; set; }
    public Team HomeTeam { get; set; } = null!;
    
    public int AwayTeamId { get; set; }
    public Team AwayTeam { get; set; } = null!;
    
    public int? HomeScore { get; set; }
    public int? AwayScore { get; set; }
    public int? HalfTimeHomeScore { get; set; }
    public int? HalfTimeAwayScore { get; set; }
}
