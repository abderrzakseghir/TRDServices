package m2.microservices.AccountService.domain.port.out;

import m2.microservices.AccountService.domain.model.Account;
import m2.microservices.AccountService.domain.model.BetHistoryItem;
import m2.microservices.AccountService.domain.model.vo.AccountId;
import m2.microservices.AccountService.domain.model.vo.KeycloakId;

import java.util.List;
import java.util.Optional;

public interface AccountRepositoryPort {
    void save(Account account);

    Optional<Account> findById(AccountId id);

    Optional<Account> findByKeycloakId(KeycloakId keycloakId);

    // Used for validation before registration
    boolean existsByEmail(String email);

    List<BetHistoryItem> findHistoryByAccountId(AccountId id, int page, int size);

}
