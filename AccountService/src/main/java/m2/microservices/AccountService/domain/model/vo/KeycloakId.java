package m2.microservices.AccountService.domain.model.vo;

public record KeycloakId(String value) {
    public KeycloakId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("KeycloakId cannot be null or blank");
        }
    }
}
