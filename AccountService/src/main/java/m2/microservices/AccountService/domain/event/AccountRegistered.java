package m2.microservices.AccountService.domain.event;

import m2.microservices.AccountService.domain.event.keys.AccountEventKeys;
import m2.microservices.AccountService.domain.model.vo.AccountId;
import m2.microservices.AccountService.domain.model.vo.KeycloakId;

import java.time.LocalDateTime;

public record AccountRegistered(
        String accountId,
        String email,
        LocalDateTime occurredAt
) implements DomainEvent {

    public AccountRegistered(String accountId,
                             String email) {
        this(accountId, email , LocalDateTime.now());
    }

    @Override
    public String key() {
        return AccountEventKeys.ACCOUNT_REGISTERED;
    }
}
