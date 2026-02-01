package m2.microservices.WalletService.domain.model;

import lombok.Getter;
import m2.microservices.WalletService.domain.model.vo.Money;
import m2.microservices.WalletService.domain.model.vo.TransactionType;
import m2.microservices.WalletService.domain.model.vo.WalletId;

import java.time.LocalDateTime;

@Getter
public class WalletTransaction {
    private final String id;
    private final TransactionType type ;
    private final Money amount ;
    private final Money balanceAfter;
    private final String referenceId; // e.g. BetId or PaymentId
    private final LocalDateTime createdAt;

    public WalletTransaction(String id, TransactionType type, Money amount, Money balanceAfter, String referenceId) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.referenceId = referenceId;
        this.createdAt = LocalDateTime.now();
    }
}
