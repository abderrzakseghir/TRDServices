package m2.microservices.WalletService.domain.port.in.command;

import m2.microservices.WalletService.domain.model.vo.Money;

import java.math.BigDecimal;

public record DepositCommand(String accountId, String paymentReference , Money amount) {
    public DepositCommand(String accountId, String paymentReference, BigDecimal amount) {
        this(accountId, paymentReference, Money.of(amount));
    }
}
