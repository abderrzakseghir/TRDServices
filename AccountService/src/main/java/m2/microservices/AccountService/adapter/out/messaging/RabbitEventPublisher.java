package m2.microservices.AccountService.adapter.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2.microservices.AccountService.domain.event.DomainEvent;
import m2.microservices.AccountService.domain.port.out.EventPublisherPort;
import m2.microservices.AccountService.infrastructure.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(DomainEvent event) {

        String routingKey = event.key();

        log.debug("Publishing Domain Event to RabbitMQ: [Key: {}] [Payload: {}]", routingKey, event);

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                routingKey,
                event
        );
    }


}
