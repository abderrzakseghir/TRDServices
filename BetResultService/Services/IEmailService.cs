using System.Threading.Tasks;
using BetResultService.Entities;

namespace BetResultService.Services;

/// <summary>
/// Interface pour le service d'envoi d'emails
/// </summary>
public interface IEmailService
{
    /// <summary>
    /// Envoie un email de notification de résultat de pari
    /// </summary>
    /// <param name="bet">L'entité du pari</param>
    /// <param name="eventName">Le nom de l'événement (match)</param>
    /// <returns>True si l'email a été envoyé avec succès</returns>
    Task<bool> SendBetResultNotificationAsync(BetEntity bet, string eventName);
}
