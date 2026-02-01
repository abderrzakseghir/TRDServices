package m2.microservices.WalletService.adapter.in.listener.dto;

public record AccountRegisteredIntegrationEvent(
        String accountId,
        String email
) {}
