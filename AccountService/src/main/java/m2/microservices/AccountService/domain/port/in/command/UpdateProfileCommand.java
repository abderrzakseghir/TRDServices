package m2.microservices.AccountService.domain.port.in.command;

import m2.microservices.AccountService.domain.model.vo.AccountId;

public record UpdateProfileCommand(
        AccountId accountId,
        String newFirstName,
        String newLastName,
        String newPhoneNumber
) {}
