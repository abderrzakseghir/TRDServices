package m2.microservices.WalletService.domain.port.out;

import m2.microservices.WalletService.domain.model.Wallet;

import java.util.Optional;

public interface WalletRepositoryPort {
    void save(Wallet wallet);
    Optional<Wallet> findByAccountId(String accountId);
    boolean existsByAccountId(String accountId);
}
