package m2.microservices.WalletService.domain.port.in.command;

import m2.microservices.WalletService.domain.model.vo.Money;

import java.math.BigDecimal;

public record ProcessWagerCommand(
        String accountId,
        String betId,
        Money amount
) {
    public ProcessWagerCommand(String accountId, String betId, BigDecimal amount) {
        this(accountId, betId, Money.of(amount));
    }
}
