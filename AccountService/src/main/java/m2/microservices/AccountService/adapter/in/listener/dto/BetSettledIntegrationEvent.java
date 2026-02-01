package m2.microservices.AccountService.adapter.in.listener.dto;

import java.math.BigDecimal;

public record BetSettledIntegrationEvent(
        String betId,
        String accountId,
        boolean isWin,
        BigDecimal payoutMoney
) { }
