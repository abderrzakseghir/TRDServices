package m2.microservices.bet_lifecycle_service.adapter.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2.microservices.bet_lifecycle_service.domain.event.DomainEvent;
import m2.microservices.bet_lifecycle_service.domain.port.out.EventPublisherPort;
import m2.microservices.bet_lifecycle_service.infrastructure.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitEventPublished implements EventPublisherPort {
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(DomainEvent event) {
        String routingKey = event.key();

        log.info("Publishing Event: [Key: {}] [ID: {}]", routingKey, event);


        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                routingKey,
                event
        );
    }
}
