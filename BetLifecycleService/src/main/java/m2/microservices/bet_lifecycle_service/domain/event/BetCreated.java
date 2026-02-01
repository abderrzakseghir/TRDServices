package m2.microservices.bet_lifecycle_service.domain.event;

import m2.microservices.bet_lifecycle_service.domain.event.keys.BetEventKeys;
import m2.microservices.bet_lifecycle_service.domain.model.vo.BetId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BetCreated(
        String betId,
        String accountId, 
        BigDecimal amountWagered, 
        BigDecimal potentialPayout,
        LocalDateTime occurredAt
) implements  DomainEvent{

    public BetCreated(String betId, String  accountId, BigDecimal amountWagered, BigDecimal potentialPayout) {
        this(betId, accountId, amountWagered, potentialPayout, LocalDateTime.now());
    }


    @Override
    public String key() {
        return BetEventKeys.BET_PLACED;
    }
}
