using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MockScoreService.API.Data;
using MockScoreService.API.DTOs;

namespace MockScoreService.API.Controllers;

[ApiController]
[Route("v4")]
public class MatchesController : ControllerBase
{
    private readonly FootballDbContext _context;
    private readonly ILogger<MatchesController> _logger;

    public MatchesController(FootballDbContext context, ILogger<MatchesController> logger)
    {
        _context = context;
        _logger = logger;
    }

    [HttpGet("matches")]
    public async Task<ActionResult<MatchesResponse>> GetMatches(
        [FromQuery] string? status = null,
        [FromQuery] DateTime? dateFrom = null,
        [FromQuery] DateTime? dateTo = null)
    {
        _logger.LogInformation("GET /v4/matches called with status={Status}, dateFrom={DateFrom}, dateTo={DateTo}", 
            status, dateFrom, dateTo);

        var query = _context.Matches
            .Include(m => m.HomeTeam)
            .Include(m => m.AwayTeam)
            .Include(m => m.Competition)
            .Include(m => m.Season)
            .AsQueryable();

        if (!string.IsNullOrEmpty(status))
        {
            query = query.Where(m => m.Status == status.ToUpper());
        }

        if (dateFrom.HasValue)
        {
            query = query.Where(m => m.UtcDate >= dateFrom.Value);
        }

        if (dateTo.HasValue)
        {
            query = query.Where(m => m.UtcDate <= dateTo.Value);
        }

        var matches = await query.OrderBy(m => m.UtcDate).ToListAsync();

        var response = new MatchesResponse
        {
            Filters = new MatchFilters
            {
                Status = status,
                Season = "2024"
            },
            ResultSet = new ResultSet
            {
                Count = matches.Count,
                First = matches.FirstOrDefault()?.UtcDate.ToString("yyyy-MM-dd") ?? "",
                Last = matches.LastOrDefault()?.UtcDate.ToString("yyyy-MM-dd") ?? "",
                Played = matches.Count(m => m.Status == "FINISHED")
            },
            Competition = matches.FirstOrDefault()?.Competition != null ? new Competition
            {
                Id = matches.First().Competition.Id,
                Name = matches.First().Competition.Name,
                Code = matches.First().Competition.Code,
                Type = matches.First().Competition.Type,
                Emblem = matches.First().Competition.Emblem
            } : new Competition(),
            Matches = matches.Select(m => new MatchDto
            {
                Area = new Area
                {
                    Id = 2088,
                    Name = "France",
                    Code = "FRA",
                    Flag = "https://crests.football-data.org/FRA.svg"
                },
                Competition = new Competition
                {
                    Id = m.Competition.Id,
                    Name = m.Competition.Name,
                    Code = m.Competition.Code,
                    Type = m.Competition.Type,
                    Emblem = m.Competition.Emblem
                },
                Season = new Season
                {
                    Id = m.Season.Id,
                    StartDate = m.Season.StartDate.ToString("yyyy-MM-dd"),
                    EndDate = m.Season.EndDate.ToString("yyyy-MM-dd"),
                    CurrentMatchday = m.Season.CurrentMatchday
                },
                Id = m.Id,
                UtcDate = m.UtcDate.ToString("yyyy-MM-ddTHH:mm:ssZ"),
                Status = m.Status,
                Matchday = m.Matchday,
                Stage = m.Stage,
                Group = m.Group,
                LastUpdated = m.LastUpdated.ToString("yyyy-MM-ddTHH:mm:ssZ"),
                HomeTeam = new TeamDto
                {
                    Id = m.HomeTeam.Id,
                    Name = m.HomeTeam.Name,
                    ShortName = m.HomeTeam.ShortName,
                    Tla = m.HomeTeam.Tla,
                    Crest = m.HomeTeam.Crest
                },
                AwayTeam = new TeamDto
                {
                    Id = m.AwayTeam.Id,
                    Name = m.AwayTeam.Name,
                    ShortName = m.AwayTeam.ShortName,
                    Tla = m.AwayTeam.Tla,
                    Crest = m.AwayTeam.Crest
                },
                Score = new ScoreDto
                {
                    Winner = m.HomeScore > m.AwayScore ? "HOME_TEAM" :
                             m.AwayScore > m.HomeScore ? "AWAY_TEAM" : null,
                    Duration = "REGULAR",
                    FullTime = new ScoreDetailDto
                    {
                        Home = m.HomeScore,
                        Away = m.AwayScore
                    },
                    HalfTime = new ScoreDetailDto
                    {
                        Home = m.HalfTimeHomeScore,
                        Away = m.HalfTimeAwayScore
                    }
                }
            }).ToList()
        };

        return Ok(response);
    }

    [HttpGet("matches/{id}")]
    public async Task<ActionResult<MatchDto>> GetMatch(int id)
    {
        var match = await _context.Matches
            .Include(m => m.HomeTeam)
            .Include(m => m.AwayTeam)
            .Include(m => m.Competition)
            .Include(m => m.Season)
            .FirstOrDefaultAsync(m => m.Id == id);

        if (match == null)
        {
            return NotFound();
        }

        var matchDto = new MatchDto
        {
            Area = new Area
            {
                Id = 2088,
                Name = "France",
                Code = "FRA",
                Flag = "https://crests.football-data.org/FRA.svg"
            },
            Competition = new Competition
            {
                Id = match.Competition.Id,
                Name = match.Competition.Name,
                Code = match.Competition.Code,
                Type = match.Competition.Type,
                Emblem = match.Competition.Emblem
            },
            Season = new Season
            {
                Id = match.Season.Id,
                StartDate = match.Season.StartDate.ToString("yyyy-MM-dd"),
                EndDate = match.Season.EndDate.ToString("yyyy-MM-dd"),
                CurrentMatchday = match.Season.CurrentMatchday
            },
            Id = match.Id,
            UtcDate = match.UtcDate.ToString("yyyy-MM-ddTHH:mm:ssZ"),
            Status = match.Status,
            Matchday = match.Matchday,
            Stage = match.Stage,
            Group = match.Group,
            LastUpdated = match.LastUpdated.ToString("yyyy-MM-ddTHH:mm:ssZ"),
            HomeTeam = new TeamDto
            {
                Id = match.HomeTeam.Id,
                Name = match.HomeTeam.Name,
                ShortName = match.HomeTeam.ShortName,
                Tla = match.HomeTeam.Tla,
                Crest = match.HomeTeam.Crest
            },
            AwayTeam = new TeamDto
            {
                Id = match.AwayTeam.Id,
                Name = match.AwayTeam.Name,
                ShortName = match.AwayTeam.ShortName,
                Tla = match.AwayTeam.Tla,
                Crest = match.AwayTeam.Crest
            },
            Score = new ScoreDto
            {
                Winner = match.HomeScore > match.AwayScore ? "HOME_TEAM" :
                         match.AwayScore > match.HomeScore ? "AWAY_TEAM" : null,
                Duration = "REGULAR",
                FullTime = new ScoreDetailDto
                {
                    Home = match.HomeScore,
                    Away = match.AwayScore
                },
                HalfTime = new ScoreDetailDto
                {
                    Home = match.HalfTimeHomeScore,
                    Away = match.HalfTimeAwayScore
                }
            }
        };

        return Ok(matchDto);
    }
}
