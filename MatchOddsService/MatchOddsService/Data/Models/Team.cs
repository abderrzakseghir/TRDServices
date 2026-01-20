namespace MatchOddsService.Data.Models;

public class Team
{
    public int Id { get; set; }
    public required string Name { get; set; }
    public string? FlagUrl { get; set; } // Optionnel pour l'affichage
}