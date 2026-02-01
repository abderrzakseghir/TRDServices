package m2.microservices.WalletService.adapter.in.listener.dto;

import java.math.BigDecimal;

public record BetPlacedIntegrationEvent(
        String betId,
        String accountId,
        BigDecimal amountWagered
) {}