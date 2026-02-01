package m2.microservices.AccountService.domain.event;

import m2.microservices.AccountService.domain.event.keys.AccountEventKeys;
import m2.microservices.AccountService.domain.model.vo.AccountId;

import java.time.LocalDateTime;

public record AccountProfileUpdated(
        AccountId AccountId,
        String newEmail,
        String newFirstName,
        String newLastName,
        String newPhoneNumber,
        LocalDateTime occurredAt
) implements  DomainEvent {

    public AccountProfileUpdated(AccountId accountId,
                                 String newEmail,
                                 String newFirstName,
                                 String newLastName,
                                 String newPhoneNumber) {
        this(accountId, newEmail, newFirstName, newLastName, newPhoneNumber, LocalDateTime.now());
    }

    @Override
    public String key() {
        return AccountEventKeys.PROFILE_UPDATED;
    }
}
