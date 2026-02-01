package m2.microservices.AccountService.domain.event;

import m2.microservices.AccountService.domain.event.keys.AccountEventKeys;
import m2.microservices.AccountService.domain.model.vo.AccountId;
import m2.microservices.AccountService.domain.model.vo.Money;

import java.time.LocalDateTime;

public record BetAddedToHistory(
        AccountId accountId,
        String betId,
        String gameName ,
        Money amountWagered,
        LocalDateTime occurredAt
) implements  DomainEvent {
    public BetAddedToHistory(AccountId accountId,
                             String betId,
                             String gameName,
                             Money amountWagered) {
        this(accountId, betId, gameName, amountWagered, LocalDateTime.now());
    }

    @Override
    public String key() {
        return AccountEventKeys.HISTORY_PLACED;
    }
}
