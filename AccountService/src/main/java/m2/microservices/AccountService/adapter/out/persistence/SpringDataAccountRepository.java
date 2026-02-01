package m2.microservices.AccountService.adapter.out.persistence;

import m2.microservices.AccountService.adapter.out.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

interface SpringDataAccountRepository extends JpaRepository<AccountEntity , String> {
    Optional<AccountEntity> findByKeycloakId(String keycloakId);

    boolean existsByEmail(String email);

    // OPTIMIZATION: When loading Aggregate, fetch history in one query to avoid "N+1" problem
    // Note: Be careful if history grows to 10,000 items!
    @Query("SELECT a FROM AccountEntity a LEFT JOIN FETCH a.betHistory WHERE a.id = :id")
    Optional<AccountEntity> findByIdWithHistory(String id);

}
