package m2.microservices.bet_lifecycle_service.adapter.in.listener.dto;

public record PaymentResultEvent(
        String betId,
        String status ,
        String reason
) {
}
