package m2.microservices.bet_lifecycle_service.domain.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class Selection {
    private final String matchId;
    private final String marketName;
    private final String selectionName;
    private final BigDecimal odd;

    public Selection(String matchId, String marketName, String selectionName, BigDecimal odd) {
        if (odd.compareTo(BigDecimal.ONE) <= 0) {
            throw new IllegalArgumentException("Odd must be greater than 1.00");
        }
        this.matchId = matchId;
        this.marketName = marketName;
        this.selectionName = selectionName;
        this.odd = odd;
    }
}
