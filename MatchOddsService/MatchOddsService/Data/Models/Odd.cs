using System.Text.RegularExpressions;

namespace MatchOddsService.Data.Models;

public class Odd
{
    public int Id { get; set; }
    public decimal HomeWin { get; set; } // Victoire Équipe 1
    public decimal AwayWin { get; set; } // Victoire Équipe 2
    public decimal Draw { get; set; }    // Match Nul

    // Clé étrangère vers le Match
    public int MatchId { get; set; }
    public Match? Match { get; set; }
}