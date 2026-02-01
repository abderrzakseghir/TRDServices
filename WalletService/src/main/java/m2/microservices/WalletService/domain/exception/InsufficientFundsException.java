package m2.microservices.WalletService.domain.exception;

public abstract class InsufficientFundsException extends RuntimeException{
    public InsufficientFundsException(String message) {
        super(message);
    }
}
