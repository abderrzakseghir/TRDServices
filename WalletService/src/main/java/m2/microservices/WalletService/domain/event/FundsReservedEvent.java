package m2.microservices.WalletService.domain.event;

import m2.microservices.WalletService.domain.event.keys.WalletEventKeys;

import java.time.LocalDateTime;

public record FundsReservedEvent(
        String betId,
        String status,
        String reason,
        LocalDateTime occurredAt
) implements DomainEvent {

    public FundsReservedEvent(String betId, String status, String reason) {
        this(betId, status, reason, LocalDateTime.now());
    }

    @Override
    public String key() {
        return WalletEventKeys.FUNDS_RESERVED;
    }
}