package m2.microservices.bet_lifecycle_service.domain.model.vo;

public record BetId(String value) {
    public BetId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("BetId cannot be null or blank");
        }
    }
}
