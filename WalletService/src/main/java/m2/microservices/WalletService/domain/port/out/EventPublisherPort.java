package m2.microservices.WalletService.domain.port.out;

import m2.microservices.WalletService.domain.event.DomainEvent;

public interface EventPublisherPort {
    void publish(DomainEvent event);
}
