package m2.microservices.bet_lifecycle_service.domain.event;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime occurredAt();

    String key();
}

