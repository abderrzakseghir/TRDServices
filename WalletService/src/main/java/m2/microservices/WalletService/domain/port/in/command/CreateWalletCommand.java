package m2.microservices.WalletService.domain.port.in.command;

public record CreateWalletCommand (String AccountId) {
    public CreateWalletCommand{
        if (AccountId == null || AccountId.isBlank()){
            throw new IllegalArgumentException("AccountId cannot be null or empty");
        }
    }
}
