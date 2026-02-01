package m2.microservices.bet_lifecycle_service.adapter.out.persistence.repository;

import m2.microservices.bet_lifecycle_service.adapter.out.persistence.entity.BetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataBetRepository extends JpaRepository<BetEntity , String> {

    Page<BetEntity> findByAccountIdOrderByCreatedAtDesc(String accountId, Pageable pageable);
}
