package m2.microservices.bet_lifecycle_service.domain.event;

import m2.microservices.bet_lifecycle_service.domain.event.keys.BetEventKeys;
import m2.microservices.bet_lifecycle_service.domain.model.Selection;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record BetConfirmed (
        String value,
        String  accountId,
        List<Selection> selections,
        BigDecimal potentialPayout,
        LocalDateTime occurredAt
) implements DomainEvent {

    public BetConfirmed(String value, String accountId, List<Selection> selections, BigDecimal potentialPayout) {
        this(value, accountId, selections, potentialPayout, LocalDateTime.now());
    }

    @Override
    public LocalDateTime occurredAt() {
        return null;
    }

    @Override
    public String key() {
        return BetEventKeys.BET_CONFIRMED;
    }
}
