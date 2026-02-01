package m2.microservices.bet_lifecycle_service.adapter.in.web.dto;

import java.math.BigDecimal;

public record BetResponse(
        String betId,
        String status,
        BigDecimal potentialPayout
) {}