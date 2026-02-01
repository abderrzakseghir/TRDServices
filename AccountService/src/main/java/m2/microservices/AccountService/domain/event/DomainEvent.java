package m2.microservices.AccountService.domain.event;


import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime occurredAt();

    String key();
}
