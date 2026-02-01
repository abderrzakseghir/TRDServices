package m2.microservices.AccountService.adapter.in.web.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BetHistoryResponse(
        String betId,
        String gameName,
        String marketName,
        String selection,
        BigDecimal odds,
        BigDecimal amountWagered,
        BigDecimal payoutAmount,
        String status,
        LocalDateTime dateTime
) {}
