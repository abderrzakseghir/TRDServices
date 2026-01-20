namespace MatchOddsService.Data.DTOs;

// --- TEAMS ---
public record CreateTeamDto(string Name, string? FlagUrl);
public record UpdateTeamDto(string Name, string? FlagUrl);

// --- ODDS (Cotes) ---
public record CreateOddDto(int MatchId, decimal HomeWin, decimal AwayWin, decimal Draw);
// Pour le Patch/Update, tout est nullable pour permettre la modif partielle
public record UpdateOddDto(decimal? HomeWin, decimal? AwayWin, decimal? Draw);

// --- MATCHES ---
public record CreateMatchDto(int HomeTeamId, int AwayTeamId, DateTime MatchDate);

// DTO spécifique pour le PATCH (mise à jour partielle)
// Si tu envoies null, le champ n'est pas modifié.
public record PatchMatchDto(
    DateTime? MatchDate,
    string? Status,
    int? HomeTeamId,
    int? AwayTeamId
);

// --- RESPONSES (Ce que l'API renvoie) ---
public record TeamResponseDto(int Id, string Name, string? FlagUrl);

public record OddResponseDto(int Id, int MatchId, decimal HomeWin, decimal AwayWin, decimal Draw);

public record MatchResponseDto(
    int Id,
    TeamResponseDto? HomeTeam,
    TeamResponseDto? AwayTeam,
    DateTime MatchDate,
    string Status,
    OddResponseDto? Odds
);