package m2.microservices.AccountService.domain.port.out;

import m2.microservices.AccountService.domain.event.DomainEvent;

public interface EventPublisherPort {
    void publish(DomainEvent event);
}
