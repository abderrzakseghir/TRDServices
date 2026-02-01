using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Amazon.SimpleEmail;
using Amazon.SimpleEmail.Model;
using BetResultService.Entities;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging;

namespace BetResultService.Services;

/// <summary>
/// Service d'envoi d'emails via AWS SES avec injection de dépendances
/// </summary>
public class SesEmailService : IEmailService
{
    private readonly IAmazonSimpleEmailService _sesClient;
    private readonly IConfiguration _config;
    private readonly ILogger<SesEmailService> _logger;
    private readonly string _fromEmail;

    public SesEmailService(
        IAmazonSimpleEmailService sesClient,
        IConfiguration config, 
        ILogger<SesEmailService> logger)
    {
        _sesClient = sesClient;
        _config = config;
        _logger = logger;
        _fromEmail = _config["AwsSes:FromEmail"] ?? "noreply@example.com";
    }

    public async Task<bool> SendBetResultNotificationAsync(BetEntity bet, string eventName)
    {
        try
        {
            // Pour le moment, on utilise une adresse email de test
            // Dans un vrai système, on récupérerait l'email de l'utilisateur via AccountId
            var toEmail = _config["AwsSes:TestRecipientEmail"] ?? "test@example.com";
            
            var subject = "Resultat de votre pari"; // Sans accent pour compatibilité email
            var isWon = bet.Status == "WON";
            
            var htmlContent = GenerateHtmlEmail(bet, eventName, isWon);
            var textContent = GenerateTextEmail(bet, eventName, isWon);

            var request = new SendEmailRequest
            {
                Source = _fromEmail,
                Destination = new Destination
                {
                    ToAddresses = new List<string> { toEmail }
                },
                Message = new Message
                {
                    Subject = new Content(subject),
                    Body = new Body
                    {
                        Html = new Content { Charset = "UTF-8", Data = htmlContent },
                        Text = new Content { Charset = "UTF-8", Data = textContent }
                    }
                }
            };

            var response = await _sesClient.SendEmailAsync(request);
            _logger.LogInformation($"[EMAIL] Notification envoyée pour le pari {bet.ExternalBetId}. MessageId: {response.MessageId}");
            return true;
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, $"[EMAIL] Erreur lors de l'envoi de la notification pour le pari {bet.ExternalBetId}");
            return false;
        }
    }

    private string GenerateHtmlEmail(BetEntity bet, string eventName, bool isWon)
    {
        var statusColor = isWon ? "#28a745" : "#dc3545";
        var statusText = isWon ? "GAGN&Eacute;" : "PERDU";
        var statusIcon = isWon ? "&#10004;" : "&#10008;";
        
        // Calcul des détails
        var totalOdds = bet.Selections.Aggregate(1m, (acc, s) => acc * s.Odd);
        var selectionsHtml = "";
        foreach (var selection in bet.Selections)
        {
            var selectionIcon = selection.Status == "WON" ? "&#10004;" : "&#10008;";
            var selectionColor = selection.Status == "WON" ? "#28a745" : "#dc3545";
            selectionsHtml += $@"
                <tr>
                    <td style='padding: 12px; border-bottom: 1px solid #eee;'>{selection.SelectionName}</td>
                    <td style='padding: 12px; border-bottom: 1px solid #eee; text-align: center;'>{selection.Odd:F2}</td>
                    <td style='padding: 12px; border-bottom: 1px solid #eee; text-align: center; color: {selectionColor}; font-weight: bold;'>{selectionIcon}</td>
                </tr>";
        }

        var gainSection = isWon 
            ? $@"<div style='background: linear-gradient(135deg, #28a745, #20c997); color: white; padding: 20px; border-radius: 10px; text-align: center; margin: 20px 0;'>
                    <p style='margin: 0; font-size: 14px; opacity: 0.9;'>Votre gain</p>
                    <p style='margin: 10px 0 0 0; font-size: 32px; font-weight: bold;'>{bet.Payout:F2} &euro;</p>
                </div>"
            : "";

        return $@"
<!DOCTYPE html>
<html lang='fr'>
<head>
    <meta charset='UTF-8'>
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
    <meta name='viewport' content='width=device-width, initial-scale=1.0'>
</head>
<body style='margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, Segoe UI, Roboto, Helvetica Neue, Arial, sans-serif; background-color: #f4f4f4;'>
    <table role='presentation' style='width: 100%; border-collapse: collapse;'>
        <tr>
            <td style='padding: 40px 20px;'>
                <table role='presentation' style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);'>
                    <!-- Header -->
                    <tr>
                        <td style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 30px; text-align: center;'>
                            <h1 style='color: white; margin: 0; font-size: 28px;'>&#127920; R&eacute;sultat de votre pari</h1>
                        </td>
                    </tr>
                    
                    <!-- Status Banner -->
                    <tr>
                        <td style='padding: 0;'>
                            <div style='background-color: {statusColor}; color: white; text-align: center; padding: 20px;'>
                                <span style='font-size: 24px; font-weight: bold;'>{statusIcon} PARI {statusText} !</span>
                            </div>
                        </td>
                    </tr>
                    
                    <!-- Content -->
                    <tr>
                        <td style='padding: 30px;'>
                            <!-- Event Info -->
                            <div style='background-color: #f8f9fa; border-radius: 10px; padding: 20px; margin-bottom: 20px;'>
                                <p style='margin: 0 0 5px 0; color: #6c757d; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;'>&Eacute;v&eacute;nement</p>
                                <p style='margin: 0; font-size: 18px; font-weight: 600; color: #333;'>&#9917; {eventName}</p>
                            </div>
                            
                            <!-- Bet Details -->
                            <div style='margin-bottom: 20px;'>
                                <h3 style='color: #333; margin: 0 0 15px 0; font-size: 16px;'>&#128203; D&eacute;tails du pari</h3>
                                <table style='width: 100%; border-collapse: collapse;'>
                                    <tr style='background-color: #f8f9fa;'>
                                        <td style='padding: 12px; font-weight: 600;'>Num&eacute;ro de pari</td>
                                        <td style='padding: 12px; text-align: right;'>#{bet.ExternalBetId}</td>
                                    </tr>
                                    <tr>
                                        <td style='padding: 12px; font-weight: 600;'>Mise</td>
                                        <td style='padding: 12px; text-align: right; font-weight: bold; color: #667eea;'>{bet.Amount:F2} &euro;</td>
                                    </tr>
                                    <tr style='background-color: #f8f9fa;'>
                                        <td style='padding: 12px; font-weight: 600;'>Cote totale</td>
                                        <td style='padding: 12px; text-align: right;'>{totalOdds:F2}</td>
                                    </tr>
                                </table>
                            </div>
                            
                            <!-- Selections -->
                            <div style='margin-bottom: 20px;'>
                                <h3 style='color: #333; margin: 0 0 15px 0; font-size: 16px;'>&#127919; Vos s&eacute;lections</h3>
                                <table style='width: 100%; border-collapse: collapse; background-color: #fff; border: 1px solid #eee; border-radius: 8px;'>
                                    <thead>
                                        <tr style='background-color: #667eea; color: white;'>
                                            <th style='padding: 12px; text-align: left;'>S&eacute;lection</th>
                                            <th style='padding: 12px; text-align: center;'>Cote</th>
                                            <th style='padding: 12px; text-align: center;'>R&eacute;sultat</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {selectionsHtml}
                                    </tbody>
                                </table>
                            </div>
                            
                            <!-- Gain Section (only if won) -->
                            {gainSection}
                        </td>
                    </tr>
                    
                    <!-- Footer -->
                    <tr>
                        <td style='background-color: #f8f9fa; padding: 20px; text-align: center; border-top: 1px solid #eee;'>
                            <p style='margin: 0 0 10px 0; color: #6c757d; font-size: 14px;'>Merci de votre confiance !</p>
                            <p style='margin: 0; color: #adb5bd; font-size: 12px;'>&copy; {DateTime.Now.Year} TRDServices - Tous droits r&eacute;serv&eacute;s</p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>";
    }

    private string GenerateTextEmail(BetEntity bet, string eventName, bool isWon)
    {
        var statusText = isWon ? "GAGNÉ" : "PERDU";
        var totalOdds = bet.Selections.Aggregate(1m, (acc, s) => acc * s.Odd);
        var selectionsText = "";
        
        foreach (var selection in bet.Selections)
        {
            var selectionStatus = selection.Status == "WON" ? "[GAGNÉ]" : "[PERDU]";
            selectionsText += $"  - {selection.SelectionName} (Cote: {selection.Odd:F2}) {selectionStatus}\n";
        }

        var gainText = isWon ? $"\nVOTRE GAIN: {bet.Payout:F2} EUR\n" : "";

        return $@"
========================================
        RÉSULTAT DE VOTRE PARI
========================================

Statut: PARI {statusText} !

Événement: {eventName}

DÉTAILS DU PARI
---------------
Numéro de pari: #{bet.ExternalBetId}
Mise: {bet.Amount:F2} EUR
Cote totale: {totalOdds:F2}

VOS SÉLECTIONS
--------------
{selectionsText}
{gainText}
----------------------------------------
Merci de votre confiance !
(c) {DateTime.Now.Year} TRDServices
";
    }
}
