package m2.microservices.WalletService.adapter.out.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2.microservices.WalletService.domain.event.DomainEvent;
import m2.microservices.WalletService.domain.port.out.EventPublisherPort;
import m2.microservices.WalletService.infrastructure.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitEventPublisher implements EventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(DomainEvent event) {
        // Sends the SAGA response (FundsReserved) back to Bet Service
        String routingKey = event.key();
        log.info("Publishing Wallet Event: key={}", routingKey);

        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE_NAME, routingKey, event);
    }
}