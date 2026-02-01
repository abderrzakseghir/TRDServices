package m2.microservices.WalletService.domain.exception;

public abstract class WalletNotFoundException extends RuntimeException{
    public WalletNotFoundException(String message) {
        super(message);
    }
}
