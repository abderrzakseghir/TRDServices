package m2.microservices.WalletService.domain.model.vo;

public record WalletId(String value) {
    public WalletId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("WalletId cannot be null or blank");
        }
    }
}
