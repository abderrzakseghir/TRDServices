package m2.microservices.AccountService.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import m2.microservices.AccountService.adapter.out.persistence.entity.AccountEntity;
import m2.microservices.AccountService.adapter.out.persistence.entity.BetHistoryEntity;
import m2.microservices.AccountService.domain.model.Account;
import m2.microservices.AccountService.domain.model.BetHistoryItem;
import m2.microservices.AccountService.domain.model.vo.AccountId;
import m2.microservices.AccountService.domain.model.vo.KeycloakId;
import m2.microservices.AccountService.domain.port.out.AccountRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountRepositoryPort {
    private final SpringDataAccountRepository jpaRepository;
    private final SpringDataBetHistoryRepository historyRepo;
    private final AccountMapper mapper;

    @Override
    public void save(Account account) {
        AccountEntity entity = mapper.toEntity(account);
        jpaRepository.save(entity);
    }

    @Override
    public Optional<Account> findById(AccountId id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Account> findByKeycloakId(KeycloakId keycloakId) {
        return jpaRepository.findByKeycloakId(keycloakId.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public List<BetHistoryItem> findHistoryByAccountId(AccountId id, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<BetHistoryEntity> entities = historyRepo.findByAccount_IdOrderByPlacedAtDesc(id.value(), pageable);

        // Map Entity -> Domain (Reuse existing logic in AccountMapper)
        return entities.stream()
                .map(mapper::toHistoryItem) // Ensure you exposed this method in Mapper
                .toList();
    }

}
