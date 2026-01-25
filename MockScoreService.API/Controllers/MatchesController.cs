using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MockScoreService.API.Data;
using MockScoreService.API.DTOs;

namespace MockScoreService.API.Controllers;

/// <summary>
/// Contrôleur pour gérer les matchs et équipes de football
/// </summary>
[ApiController]
[Route("v4")]
[Produces("application/json")]
public class MatchesController : ControllerBase
{
    private readonly FootballDbContext _context;
    private readonly ILogger<MatchesController> _logger;

    public MatchesController(FootballDbContext context, ILogger<MatchesController> logger)
    {
        _context = context;
        _logger = logger;
    }

    /// <summary>
    /// Récupère la liste des matchs
    /// </summary>
    /// <param name="status">Filtrer par statut (SCHEDULED, IN_PLAY, FINISHED, etc.)</param>
    /// <param name="dateFrom">Date de début (optionnel)</param>
    /// <param name="dateTo">Date de fin (optionnel)</param>
    /// <returns>Liste des matchs</returns>
    /// <remarks>
    /// Retourne un format compatible avec ScoreService.Dtos.ApiResponse
    /// </remarks>
    [HttpGet("matches")]
    [ProducesResponseType(typeof(SimpleMatchesResponse), StatusCodes.Status200OK)]
    public async Task<ActionResult<SimpleMatchesResponse>> GetMatches(
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

        // Format compatible avec ScoreService.Dtos.ApiResponse
        var response = new SimpleMatchesResponse
        {
            Matches = matches.Select(m => new SimpleMatchDto
            {
                Id = m.Id,
                UtcDate = m.UtcDate.ToString("yyyy-MM-ddTHH:mm:ssZ"),
                Status = m.Status,
                HomeTeam = new SimpleTeamDto
                {
                    Id = m.HomeTeam.Id,
                    Name = m.HomeTeam.Name,
                    ShortName = m.HomeTeam.ShortName
                },
                AwayTeam = new SimpleTeamDto
                {
                    Id = m.AwayTeam.Id,
                    Name = m.AwayTeam.Name,
                    ShortName = m.AwayTeam.ShortName
                },
                Score = new SimpleScoreDto
                {
                    Duration = "REGULAR",
                    FullTime = new SimpleScoreDetailDto
                    {
                        Home = m.HomeScore,
                        Away = m.AwayScore
                    }
                }
            }).ToList()
        };

        return Ok(response);
    }

    /// <summary>
    /// Récupère un match par son ID
    /// </summary>
    /// <param name="id">ID du match</param>
    /// <returns>Détails du match</returns>
    [HttpGet("matches/{id}")]
    [ProducesResponseType(typeof(MatchDto), StatusCodes.Status200OK)]
    [ProducesResponseType(StatusCodes.Status404NotFound)]
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

                /// <summary>
                /// Crée un nouveau match
                /// </summary>
                /// <param name="request">Données du match à créer</param>
                /// <returns>Match créé</returns>
                [HttpPost("matches")]
                [ProducesResponseType(StatusCodes.Status201Created)]
                [ProducesResponseType(StatusCodes.Status400BadRequest)]
                public async Task<ActionResult<MatchDto>> CreateMatch([FromBody] CreateMatchRequest request)
                {
                    _logger.LogInformation("POST /v4/matches - Creating match: HomeTeam={HomeTeamId}, AwayTeam={AwayTeamId}", 
                        request.HomeTeamId, request.AwayTeamId);

                    var homeTeam = await _context.Teams.FindAsync(request.HomeTeamId);
                    var awayTeam = await _context.Teams.FindAsync(request.AwayTeamId);


                    if (homeTeam == null || awayTeam == null)
                    {
                        return BadRequest("Home team or away team not found");
                    }

                    var competition = await _context.Competitions.FirstOrDefaultAsync();
                    var season = await _context.Seasons.FirstOrDefaultAsync();

                    if (competition == null || season == null)
                    {
                        return BadRequest("Competition or season not configured");
                    }

                    var match = new Models.Match
                    {
                        HomeTeamId = request.HomeTeamId,
                        AwayTeamId = request.AwayTeamId,
                        UtcDate = request.UtcDate.ToUniversalTime(),
                        Status = "SCHEDULED",
                        Matchday = request.Matchday,
                        Stage = request.Stage,
                        CompetitionId = competition.Id,
                        SeasonId = season.Id,
                        LastUpdated = DateTime.UtcNow
                    };

                    _context.Matches.Add(match);
                    await _context.SaveChangesAsync();

                    _logger.LogInformation("Match created with ID {MatchId}", match.Id);

                    return CreatedAtAction(nameof(GetMatch), new { id = match.Id }, new { id = match.Id, message = "Match created successfully" });
                }

                /// <summary>
                /// Met à jour le score d'un match
                /// </summary>
                /// <param name="id">ID du match</param>
                /// <param name="request">Nouveau score</param>
                [HttpPatch("matches/{id}/score")]
                [ProducesResponseType(StatusCodes.Status200OK)]
                [ProducesResponseType(StatusCodes.Status404NotFound)]
                public async Task<ActionResult> UpdateScore(int id, [FromBody] UpdateScoreRequest request)
                {
                    _logger.LogInformation("PATCH /v4/matches/{Id}/score - Updating score: Home={HomeScore}, Away={AwayScore}", 
                        id, request.HomeScore, request.AwayScore);

                    var match = await _context.Matches.FindAsync(id);
                    if (match == null)
                    {
                        return NotFound();
                    }

                    match.HomeScore = request.HomeScore;
                    match.AwayScore = request.AwayScore;
                    match.LastUpdated = DateTime.UtcNow;

                    await _context.SaveChangesAsync();

                    _logger.LogInformation("Score updated for match {MatchId}: {HomeScore}-{AwayScore}", 
                        id, request.HomeScore, request.AwayScore);

                    return Ok(new { message = "Score updated successfully" });
                }

                /// <summary>
                /// Met à jour le statut d'un match
                /// </summary>
                /// <param name="id">ID du match</param>
                /// <param name="request">Nouveau statut (SCHEDULED, TIMED, IN_PLAY, PAUSED, FINISHED, POSTPONED, CANCELLED)</param>
                [HttpPatch("matches/{id}/status")]
                [ProducesResponseType(StatusCodes.Status200OK)]
                [ProducesResponseType(StatusCodes.Status400BadRequest)]
                [ProducesResponseType(StatusCodes.Status404NotFound)]
                public async Task<ActionResult> UpdateStatus(int id, [FromBody] UpdateStatusRequest request)
                {
                    _logger.LogInformation("PATCH /v4/matches/{Id}/status - Updating status to: {Status}", id, request.Status);

                    var match = await _context.Matches.FindAsync(id);
                    if (match == null)
                    {
                        return NotFound();
                    }

                    var validStatuses = new[] { "SCHEDULED", "TIMED", "IN_PLAY", "PAUSED", "FINISHED", "POSTPONED", "CANCELLED" };
                    if (!validStatuses.Contains(request.Status.ToUpper()))
                    {
                        return BadRequest($"Invalid status. Valid values: {string.Join(", ", validStatuses)}");
                    }

                    // If starting match, initialize scores to 0
                    if (match.Status == "SCHEDULED" && request.Status.ToUpper() == "IN_PLAY")
                    {
                        match.HomeScore ??= 0;
                        match.AwayScore ??= 0;
                    }

                    match.Status = request.Status.ToUpper();
                    match.LastUpdated = DateTime.UtcNow;

                    await _context.SaveChangesAsync();

                    _logger.LogInformation("Status updated for match {MatchId}: {Status}", id, request.Status);

                    return Ok(new { message = "Status updated successfully" });
                }

                /// <summary>
                /// Supprime un match
                /// </summary>
                /// <param name="id">ID du match à supprimer</param>
                [HttpDelete("matches/{id}")]
                [ProducesResponseType(StatusCodes.Status200OK)]
                [ProducesResponseType(StatusCodes.Status404NotFound)]
                public async Task<ActionResult> DeleteMatch(int id)
                {
                    _logger.LogInformation("DELETE /v4/matches/{Id}", id);

                    var match = await _context.Matches.FindAsync(id);
                    if (match == null)
                    {
                        return NotFound();
                    }

                    _context.Matches.Remove(match);
                    await _context.SaveChangesAsync();

                    _logger.LogInformation("Match {MatchId} deleted", id);

                    return Ok(new { message = "Match deleted successfully" });
                }

                /// <summary>
                /// Récupère la liste des équipes
                /// </summary>
                /// <returns>Liste des équipes</returns>
                [HttpGet("teams")]
                [ProducesResponseType(typeof(TeamsResponse), StatusCodes.Status200OK)]
                public async Task<ActionResult<TeamsResponse>> GetTeams()
                {
                    _logger.LogInformation("GET /v4/teams");

                    var teams = await _context.Teams.ToListAsync();

                    return Ok(new TeamsResponse
                    {
                        Count = teams.Count,
                        Teams = teams.Select(t => new TeamDto
                        {
                            Id = t.Id,
                            Name = t.Name,
                            ShortName = t.ShortName,
                            Tla = t.Tla,
                            Crest = t.Crest
                        }).ToList()
                    });
                }

                /// <summary>
                /// Crée une nouvelle équipe
                /// </summary>
                /// <param name="request">Données de l'équipe</param>
                /// <returns>Équipe créée</returns>
                [HttpPost("teams")]
                [ProducesResponseType(typeof(TeamDto), StatusCodes.Status201Created)]
                public async Task<ActionResult<TeamDto>> CreateTeam([FromBody] CreateTeamRequest request)
                {
                    _logger.LogInformation("POST /v4/teams - Creating team: {Name}", request.Name);

                    var team = new Models.Team
                    {
                        Name = request.Name,
                        ShortName = request.ShortName,
                        Tla = request.Tla.ToUpper(),
                        Crest = request.Crest
                    };

                    _context.Teams.Add(team);
                    await _context.SaveChangesAsync();

                    _logger.LogInformation("Team created with ID {TeamId}", team.Id);

                    return CreatedAtAction(nameof(GetTeams), new { id = team.Id }, new TeamDto
                    {
                        Id = team.Id,
                        Name = team.Name,
                        ShortName = team.ShortName,
                        Tla = team.Tla,
                        Crest = team.Crest
                    });
                }

                /// <summary>
                /// Supprime une équipe
                /// </summary>
                /// <param name="id">ID de l'équipe à supprimer</param>
                [HttpDelete("teams/{id}")]
                [ProducesResponseType(StatusCodes.Status200OK)]
                [ProducesResponseType(StatusCodes.Status404NotFound)]
                public async Task<ActionResult> DeleteTeam(int id)
                {
                    _logger.LogInformation("DELETE /v4/teams/{Id}", id);

                    var team = await _context.Teams.FindAsync(id);
                    if (team == null)
                    {
                        return NotFound();
                    }

                    // Check if team is used in any match
                    var hasMatches = await _context.Matches.AnyAsync(m => m.HomeTeamId == id || m.AwayTeamId == id);
                    if (hasMatches)
                    {
                        return BadRequest("Cannot delete team that has matches. Delete the matches first.");
                    }

                    _context.Teams.Remove(team);
                    await _context.SaveChangesAsync();

                    _logger.LogInformation("Team {TeamId} deleted", id);

                    return Ok(new { message = "Team deleted successfully" });
                }
            }
