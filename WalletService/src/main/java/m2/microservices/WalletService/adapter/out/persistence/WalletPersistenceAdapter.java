package m2.microservices.WalletService.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2.microservices.WalletService.adapter.out.persistence.entity.WalletEntity;
import m2.microservices.WalletService.adapter.out.persistence.entity.WalletTransactionEntity;
import m2.microservices.WalletService.adapter.out.persistence.mapper.WalletMapper;
import m2.microservices.WalletService.adapter.out.persistence.repository.SpringDataTransactionRepository;
import m2.microservices.WalletService.adapter.out.persistence.repository.SpringDataWalletRepository;
import m2.microservices.WalletService.domain.model.Wallet;
import m2.microservices.WalletService.domain.model.WalletTransaction;
import m2.microservices.WalletService.domain.port.out.WalletRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletPersistenceAdapter implements WalletRepositoryPort {
    private final SpringDataWalletRepository walletRepo;
    private final SpringDataTransactionRepository transactionRepo;
    private final WalletMapper mapper;

    @Override
    public void save(Wallet wallet) {

        WalletEntity entity = walletRepo.findById(wallet.getId().value())
                .orElseGet(() -> mapper.toEntity(wallet));

        entity.setBalance(wallet.getBalance().amount());


        List<WalletTransactionEntity> newTxEntities = wallet.getTransactions().stream()
                .map(tx -> mapper.toTransactionEntity(tx, entity))
                .collect(Collectors.toList());


        entity.getTransactions().addAll(newTxEntities);

        walletRepo.save(entity);
    }

    @Override
    public Optional<Wallet> findByAccountId(String accountId) {
        return walletRepo.findByAccountId(accountId)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByAccountId(String accountId) {
        return walletRepo.existsByAccountId(accountId);
    }
}
