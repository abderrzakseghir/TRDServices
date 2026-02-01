package m2.microservices.WalletService.adapter.in.listener.dto;

import java.math.BigDecimal;

public record BetSettledIntegrationEvent(
        String betId,
        String accountId,
        String outcome,
        BigDecimal amountWon
) {
}
