package m2.microservices.WalletService.adapter.out.persistence.mapper;

import m2.microservices.WalletService.adapter.out.persistence.entity.WalletEntity;
import m2.microservices.WalletService.adapter.out.persistence.entity.WalletTransactionEntity;
import m2.microservices.WalletService.domain.model.Wallet;
import m2.microservices.WalletService.domain.model.WalletTransaction;
import m2.microservices.WalletService.domain.model.vo.Money;
import m2.microservices.WalletService.domain.model.vo.WalletId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class WalletMapper {
    public WalletEntity toEntity(Wallet domain) {
        WalletEntity entity = new WalletEntity();
        entity.setId(domain.getId().value());
        entity.setAccountId(domain.getAccountId());
        entity.setBalance(domain.getBalance().amount());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setTransactions(new ArrayList<>());
        return entity;
    }

    public WalletTransactionEntity toTransactionEntity(WalletTransaction domain, WalletEntity parent) {
        WalletTransactionEntity entity = new WalletTransactionEntity();
        entity.setId(domain.getId());
        entity.setWallet(parent); // <--- Link the Object
        entity.setAmount(domain.getAmount().amount());
        entity.setBalanceAfter(domain.getBalanceAfter().amount());
        entity.setType(domain.getType().name());
        entity.setReferenceId(domain.getReferenceId());
        entity.setCreatedAt(domain.getCreatedAt());
        return entity;
    }


    public Wallet toDomain(WalletEntity entity) {
        return new Wallet(
                new WalletId(entity.getId()),
                entity.getAccountId(),
                Money.of(entity.getBalance()),
                entity.getCreatedAt()
        );
    }
}
