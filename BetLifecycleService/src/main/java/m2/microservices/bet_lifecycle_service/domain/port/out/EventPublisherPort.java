package m2.microservices.bet_lifecycle_service.domain.port.out;

import m2.microservices.bet_lifecycle_service.domain.event.DomainEvent;

public interface EventPublisherPort {
    void publish(DomainEvent event);
}
