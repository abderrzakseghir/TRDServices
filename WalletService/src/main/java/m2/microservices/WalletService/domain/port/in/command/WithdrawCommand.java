package m2.microservices.WalletService.domain.port.in.command;

import m2.microservices.WalletService.domain.model.vo.Money;

import java.math.BigDecimal;

public record WithdrawCommand(
        String accountId,
        Money amount
) {
    public WithdrawCommand(String accountId,   BigDecimal amount) {
        this(accountId, Money.of(amount));
    }
}
