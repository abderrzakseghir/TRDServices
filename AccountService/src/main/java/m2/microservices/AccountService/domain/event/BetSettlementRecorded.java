package m2.microservices.AccountService.domain.event;

import m2.microservices.AccountService.domain.event.keys.AccountEventKeys;
import m2.microservices.AccountService.domain.model.vo.AccountId;
import m2.microservices.AccountService.domain.model.vo.Money;

import java.time.LocalDateTime;

public record BetSettlementRecorded(
        AccountId accountId,
        String betId,
        Boolean isWin,
        Money settledAmount,
        LocalDateTime occurredAt

) implements DomainEvent {
    public BetSettlementRecorded(
            AccountId accountId,
            String betId,
            boolean isWin,
            Money settledAmount
    ) {
        this(accountId, betId, isWin, settledAmount, LocalDateTime.now());
    }

    @Override
    public String key() {
        return AccountEventKeys.HISTORY_SETTLED;
    }
}
