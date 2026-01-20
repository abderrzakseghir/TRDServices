using MatchOddsService.Data.Configuration;
using MatchOddsService.Data.DTOs;
using MatchOddsService.Data.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace MatchOddsService.Controllers;

[Route("api/[controller]")]
[ApiController]
public class MatchesController : ControllerBase
{
    private readonly MatchContext _context;

    public MatchesController(MatchContext context)
    {
        _context = context;
    }

    // GET: api/matches
    [HttpGet]
    public async Task<ActionResult<IEnumerable<MatchResponseDto>>> GetMatches()
    {
        var matches = await _context.Matches
            .Include(m => m.HomeTeam)
            .Include(m => m.AwayTeam)
            .Include(m => m.Odds)
            .ToListAsync();

        return Ok(matches.Select(MapToDto));
    }

    // GET: api/matches/5
    [HttpGet("{id}")]
    public async Task<ActionResult<MatchResponseDto>> GetMatch(int id)
    {
        var match = await _context.Matches
            .Include(m => m.HomeTeam)
            .Include(m => m.AwayTeam)
            .Include(m => m.Odds)
            .FirstOrDefaultAsync(m => m.Id == id);

        if (match == null) return NotFound();
        return MapToDto(match);
    }

    // POST: api/matches
    [HttpPost]
    public async Task<ActionResult<MatchResponseDto>> CreateMatch(CreateMatchDto dto)
    {
        var match = new Match
        {
            HomeTeamId = dto.HomeTeamId,
            AwayTeamId = dto.AwayTeamId,
            MatchDate = dto.MatchDate,
            Status = "Scheduled"
        };

        _context.Matches.Add(match);
        await _context.SaveChangesAsync();

        // Recharger pour avoir les infos des équipes incluses
        return await GetMatch(match.Id);
    }

    // PATCH: api/matches/5
    // Permet de modifier partiellement un match (ex: changer juste le statut en "Live")
    [HttpPatch("{id}")]
    public async Task<IActionResult> PatchMatch(int id, PatchMatchDto dto)
    {
        var match = await _context.Matches.FindAsync(id);
        if (match == null) return NotFound();

        // On ne met à jour que si la valeur n'est pas null
        if (dto.MatchDate.HasValue) match.MatchDate = dto.MatchDate.Value;
        if (!string.IsNullOrEmpty(dto.Status)) match.Status = dto.Status;
        if (dto.HomeTeamId.HasValue) match.HomeTeamId = dto.HomeTeamId.Value;
        if (dto.AwayTeamId.HasValue) match.AwayTeamId = dto.AwayTeamId.Value;

        await _context.SaveChangesAsync();
        return NoContent();
    }

    // DELETE: api/matches/5
    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteMatch(int id)
    {
        var match = await _context.Matches.FindAsync(id);
        if (match == null) return NotFound();

        _context.Matches.Remove(match);
        await _context.SaveChangesAsync();
        return NoContent();
    }   

    // Helper pour convertir en DTO proprement
    private static MatchResponseDto MapToDto(Match m)
    {
        return new MatchResponseDto(
            m.Id,
            m.HomeTeam != null ? new TeamResponseDto(m.HomeTeam.Id, m.HomeTeam.Name, m.HomeTeam.FlagUrl) : null,
            m.AwayTeam != null ? new TeamResponseDto(m.AwayTeam.Id, m.AwayTeam.Name, m.AwayTeam.FlagUrl) : null,
            m.MatchDate,
            m.Status,
            m.Odds != null ? new OddResponseDto(m.Odds.Id, m.Odds.MatchId, m.Odds.HomeWin, m.Odds.AwayWin, m.Odds.Draw) : null
        );
    }
}