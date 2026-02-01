package m2.microservices.AccountService.adapter.in.listener.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BetPlacedIntegrationEvent(
        String betId,
        String accountId,
        String gameName,
        String marketName,
        String selection,
        BigDecimal odds,
        BigDecimal amountWagered,
        LocalDateTime placedAt
) {}
