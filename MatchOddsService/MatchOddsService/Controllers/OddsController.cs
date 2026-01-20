using MatchOddsService.Data.Configuration;
using MatchOddsService.Data.DTOs;
using MatchOddsService.Data.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace MatchOddsService.Controllers;

[Route("api/[controller]")]
[ApiController]
public class OddsController : ControllerBase
{
    private readonly MatchContext _context;

    public OddsController(MatchContext context)
    {
        _context = context;
    }

    // POST: api/odds
    // Créer une cote pour un match qui n'en a pas
    [HttpPost]
    public async Task<ActionResult<OddResponseDto>> CreateOdd(CreateOddDto dto)
    {
        // Vérifier si le match existe
        var match = await _context.Matches.Include(m => m.Odds).FirstOrDefaultAsync(m => m.Id == dto.MatchId);
        if (match == null) return NotFound("Match introuvable.");
        if (match.Odds != null) return BadRequest("Ce match a déjà des cotes.");

        var odd = new Odd
        {
            MatchId = dto.MatchId,
            HomeWin = dto.HomeWin,
            AwayWin = dto.AwayWin,
            Draw = dto.Draw
        };

        _context.Odds.Add(odd);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetOdd), new { id = odd.Id },
            new OddResponseDto(odd.Id, odd.MatchId, odd.HomeWin, odd.AwayWin, odd.Draw));
    }

    // GET: api/odds/5 (Récupérer une cote par son ID)
    [HttpGet("{id}")]
    public async Task<ActionResult<OddResponseDto>> GetOdd(int id)
    {
        var odd = await _context.Odds.FindAsync(id);
        if (odd == null) return NotFound();

        return new OddResponseDto(odd.Id, odd.MatchId, odd.HomeWin, odd.AwayWin, odd.Draw);
    }

    // PATCH: api/odds/5
    // Modifier les cotes existantes
    [HttpPatch("{id}")]
    public async Task<IActionResult> PatchOdd(int id, UpdateOddDto dto)
    {
        var odd = await _context.Odds.FindAsync(id);
        if (odd == null) return NotFound();

        if (dto.HomeWin.HasValue) odd.HomeWin = dto.HomeWin.Value;
        if (dto.AwayWin.HasValue) odd.AwayWin = dto.AwayWin.Value;
        if (dto.Draw.HasValue) odd.Draw = dto.Draw.Value;

        await _context.SaveChangesAsync();
        return NoContent();
    }

    // DELETE: api/odds/5
    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteOdd(int id)
    {
        var odd = await _context.Odds.FindAsync(id);
        if (odd == null) return NotFound();

        _context.Odds.Remove(odd);
        await _context.SaveChangesAsync();
        return NoContent();
    }
}