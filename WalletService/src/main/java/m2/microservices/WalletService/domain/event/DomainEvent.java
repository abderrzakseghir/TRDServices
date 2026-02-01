package m2.microservices.WalletService.domain.event;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime occurredAt();

    String key();
}

