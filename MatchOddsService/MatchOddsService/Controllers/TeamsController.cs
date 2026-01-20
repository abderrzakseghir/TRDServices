using MatchOddsService.Data.Configuration;
using MatchOddsService.Data.DTOs;
using MatchOddsService.Data.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace MatchOddsService.Controllers;

[Route("api/[controller]")]
[ApiController]
public class TeamsController : ControllerBase
{
    private readonly MatchContext _context;

    public TeamsController(MatchContext context)
    {
        _context = context;
    }

    // GET: api/teams
    [HttpGet]
    public async Task<ActionResult<IEnumerable<TeamResponseDto>>> GetTeams()
    {
        return await _context.Teams
            .Select(t => new TeamResponseDto(t.Id, t.Name, t.FlagUrl))
            .ToListAsync();
    }

    // GET: api/teams/5
    [HttpGet("{id}")]
    public async Task<ActionResult<TeamResponseDto>> GetTeam(int id)
    {
        var team = await _context.Teams.FindAsync(id);
        if (team == null) return NotFound();
        return new TeamResponseDto(team.Id, team.Name, team.FlagUrl);
    }

    // POST: api/teams
    [HttpPost]
    public async Task<ActionResult<TeamResponseDto>> CreateTeam(CreateTeamDto dto)
    {
        var team = new Team { Name = dto.Name, FlagUrl = dto.FlagUrl };
        _context.Teams.Add(team);
        await _context.SaveChangesAsync();

        return CreatedAtAction(nameof(GetTeam), new { id = team.Id },
            new TeamResponseDto(team.Id, team.Name, team.FlagUrl));
    }

    // PUT: api/teams/5
    [HttpPut("{id}")]
    public async Task<IActionResult> UpdateTeam(int id, UpdateTeamDto dto)
    {
        var team = await _context.Teams.FindAsync(id);
        if (team == null) return NotFound();

        team.Name = dto.Name;
        team.FlagUrl = dto.FlagUrl; // Peut être null

        await _context.SaveChangesAsync();
        return NoContent();
    }

    // DELETE: api/teams/5
    [HttpDelete("{id}")]
    public async Task<IActionResult> DeleteTeam(int id)
    {
        var team = await _context.Teams.FindAsync(id);
        if (team == null) return NotFound();

        _context.Teams.Remove(team);
        await _context.SaveChangesAsync();
        return NoContent();
    }
}