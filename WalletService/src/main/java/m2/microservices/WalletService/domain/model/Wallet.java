package m2.microservices.WalletService.domain.model;

import lombok.Getter;
import m2.microservices.WalletService.domain.event.DomainEvent;
import m2.microservices.WalletService.domain.model.vo.Money;
import m2.microservices.WalletService.domain.model.vo.TransactionType;
import m2.microservices.WalletService.domain.model.vo.WalletId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Wallet {
    private final WalletId id;
    private final String accountId;
    private  Money balance;

    private final LocalDateTime createdAt;

    private final List<WalletTransaction> transactions = new ArrayList<>();
    private final List<DomainEvent>  domainEvents = new ArrayList<>();


    public Wallet(WalletId id, String accountId, Money balance, LocalDateTime createdAt) {
        this.id = id;
        this.accountId = accountId;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public static Wallet create(String accountId){
        return  new Wallet(
                new WalletId(UUID.randomUUID().toString()),
                accountId,
                Money.ZERO,
                LocalDateTime.now()
        );
    }

    public void credit(Money amount, TransactionType type , String referenceId){
        this.balance = this.balance.add(amount);
        recordTransaction(amount, type, referenceId);
    }

    public void debit(Money amount , TransactionType type , String referenceId){
        if (!this.balance.isGreaterThanOrEqual(amount)) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
        recordTransaction(amount, type, referenceId);
    }

    public void debit(Money amount , TransactionType type){
        if (!this.balance.isGreaterThanOrEqual(amount)) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        this.balance = this.balance.subtract(amount);
        recordTransaction(amount, type,"N/A");
    }

    private void recordTransaction(Money amount, TransactionType type, String referenceId) {
        this.transactions.add(new WalletTransaction(
                UUID.randomUUID().toString(),
                type,
                amount,
                this.balance,
                referenceId
        ));
    }

    public void clearTransactions() { this.transactions.clear(); }
    public void addEvent(DomainEvent event) { this.domainEvents.add(event); }
    public void clearEvents() { this.domainEvents.clear(); }
    public List<DomainEvent> getDomainEvents() { return Collections.unmodifiableList(domainEvents); }
}
