package m2.microservices.bet_lifecycle_service.adapter.in.listener.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2.microservices.bet_lifecycle_service.adapter.in.listener.dto.PaymentResultEvent;
import m2.microservices.bet_lifecycle_service.domain.port.in.usecase.PlaceBetUseCase;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {
    private final PlaceBetUseCase placeBetUseCase;

    @RabbitListener(queues = "${spring.rabbitmq.queues.payment-updates}")
    public void onPaymentResult(PaymentResultEvent event) {
        log.info("Received Payment Event for BetID: {} | Status: {}", event.betId(), event.status());

        if ("APPROVED".equalsIgnoreCase(event.status())) {
            // Happy Path: Payment successful -> Confirm Bet
            placeBetUseCase.confirmBet(event.betId());
        } else {
            log.warn("Payment failed for bet: {}. Reason: {}", event.betId(), event.reason());
        }
    }
}
