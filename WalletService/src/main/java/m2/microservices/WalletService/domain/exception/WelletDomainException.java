package m2.microservices.WalletService.domain.exception;

public abstract class WelletDomainException extends RuntimeException{
    public WelletDomainException(String message) {
        super(message);
    }
}
