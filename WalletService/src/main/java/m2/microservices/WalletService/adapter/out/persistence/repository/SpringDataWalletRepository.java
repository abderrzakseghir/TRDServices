
package m2.microservices.WalletService.adapter.out.persistence.repository;

import m2.microservices.WalletService.adapter.out.persistence.entity.WalletEntity;
import m2.microservices.WalletService.domain.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataWalletRepository extends JpaRepository<WalletEntity, String> {
    Optional<WalletEntity> findByAccountId(String accountId);

    // Derived Query: Generates SQL "SELECT COUNT(*) > 0 FROM wallets WHERE account_id = ?"
    boolean existsByAccountId(String accountId);
}
