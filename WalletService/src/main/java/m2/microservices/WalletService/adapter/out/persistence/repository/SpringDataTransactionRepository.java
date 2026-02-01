package m2.microservices.WalletService.adapter.out.persistence.repository;

import m2.microservices.WalletService.adapter.out.persistence.entity.WalletTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataTransactionRepository extends JpaRepository<WalletTransactionEntity, String> {
}
