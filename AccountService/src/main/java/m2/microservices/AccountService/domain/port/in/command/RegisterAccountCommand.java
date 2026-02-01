package m2.microservices.AccountService.domain.port.in.command;

import m2.microservices.AccountService.domain.model.vo.KeycloakId;

public record RegisterAccountCommand(
        KeycloakId keycloakId,
        String email,
        String firstName,
        String lastName,
        String phoneNumber

) {
    public RegisterAccountCommand {
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Invalid Email");
    }
}
