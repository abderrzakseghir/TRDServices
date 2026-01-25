using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using BetResultService.Data;
using BetResultService.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;

namespace BetResultService.Services;

public class BetProcessor
{
	private readonly BetDbContext _context;

	private readonly ILogger<BetProcessor> _logger;

	public BetProcessor(BetDbContext context, ILogger<BetProcessor> logger)
	{
		_context = context;
		_logger = logger;
	}

	public async Task ProcessNewBetAsync(BetEntity newBet)
	{
		if (!(await _context.Bets.AnyAsync((BetEntity b) => b.ExternalBetId == newBet.ExternalBetId)))
		{
			_context.Bets.Add(newBet);
			await _context.SaveChangesAsync();
			_logger.LogInformation($"Nouveau pari reçu: ID {newBet.ExternalBetId} pour {newBet.Amount}€");
		}
	}

	public async Task ProcessMatchResultAsync(int matchId, string homeTeam, string awayTeam, int homeScore, int awayScore)
	{
		_logger.LogInformation($"[DEBUG] Traitement résultat match {matchId}: {homeTeam} {homeScore} - {awayScore} {awayTeam}");
		
		// Log tous les paris en base
		var allBets = await _context.Bets.Include(b => b.Selections).ToListAsync();
		_logger.LogInformation($"[DEBUG] Total paris en base: {allBets.Count}");
		foreach (var b in allBets)
		{
			_logger.LogInformation($"[DEBUG] Pari {b.ExternalBetId}: Status={b.Status}, Selections: {string.Join(", ", b.Selections.Select(s => $"MatchId={s.MatchId}"))}");
		}
		
		List<BetEntity> pendingBets = await (from b in _context.Bets.Include((BetEntity b) => b.Selections)
			where b.Status == "PENDING" && b.Selections.Any((BetSelection s) => s.MatchId == matchId)
			select b).ToListAsync();
		
		_logger.LogInformation($"[DEBUG] Paris PENDING trouvés pour match {matchId}: {pendingBets.Count}");
		
		if (!pendingBets.Any())
		{
			_logger.LogWarning($"Aucun pari PENDING trouvé pour le match {matchId}");
			return;
		}
		
		foreach (BetEntity bet in pendingBets)
		{
			BetSelection selection = bet.Selections.First((BetSelection s) => s.MatchId == matchId);
			_logger.LogInformation($"[DEBUG] Vérification pari {bet.ExternalBetId}: Selection={selection.SelectionName}, Winner attendu={homeTeam} (si {homeScore}>{awayScore})");
			bool isWin = CheckIfWin(selection, homeTeam, awayTeam, homeScore, awayScore);
			_logger.LogInformation($"[DEBUG] Résultat isWin={isWin}");
			selection.Status = (isWin ? "WON" : "LOST");
			if (selection.Status == "LOST")
			{
				bet.Status = "LOST";
				bet.Payout = default(decimal);
				_logger.LogInformation($"Pari {bet.ExternalBetId} PERDU (Match {matchId})");
			}
			else if (bet.Selections.All((BetSelection s) => s.Status == "WON"))
			{
				bet.Status = "WON";
				decimal totalOdds = bet.Selections.Aggregate(1m, (decimal acc, BetSelection s) => acc * s.Odd);
				bet.Payout = bet.Amount * totalOdds;
				_logger.LogInformation($"Pari {bet.ExternalBetId} GAGNÉ ! Gain: {bet.Payout}€");
			}
		}
		await _context.SaveChangesAsync();
	}

	private bool CheckIfWin(BetSelection sel, string home, string away, int hScore, int aScore)
	{
		string winner = ((hScore > aScore) ? home : ((aScore > hScore) ? away : "DRAW"));
		return sel.SelectionName == winner;
	}
}
