package m2.microservices.AccountService.adapter.out.persistence;

import m2.microservices.AccountService.adapter.out.persistence.entity.BetHistoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataBetHistoryRepository extends JpaRepository<BetHistoryEntity, Long> {
    // SELECT * FROM bet_history WHERE account_id = ? ORDER BY placed_at DESC
    Page<BetHistoryEntity> findByAccount_IdOrderByPlacedAtDesc(String accountId, Pageable pageable);
}
