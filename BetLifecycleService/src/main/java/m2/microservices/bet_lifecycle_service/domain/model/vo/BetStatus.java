package m2.microservices.bet_lifecycle_service.domain.model.vo;

public enum BetStatus {
    AWAITING_PAYMENT,
    CONFIRMED,
    REJECTED,
    CANCELLED,
    SETTLED
}
