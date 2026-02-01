package m2.microservices.AccountService.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2.microservices.AccountService.domain.event.DomainEvent;
import m2.microservices.AccountService.domain.model.Account;
import m2.microservices.AccountService.domain.model.BetHistoryItem;
import m2.microservices.AccountService.domain.model.vo.AccountId;
import m2.microservices.AccountService.domain.model.vo.KeycloakId;
import m2.microservices.AccountService.domain.model.vo.UserProfile;
import m2.microservices.AccountService.domain.port.in.GetAccountUseCase;
import m2.microservices.AccountService.domain.port.in.GetBettingHistoryUseCase;
import m2.microservices.AccountService.domain.port.in.ManageAccountUseCase;
import m2.microservices.AccountService.domain.port.in.command.RegisterAccountCommand;
import m2.microservices.AccountService.domain.port.in.command.UpdateProfileCommand;
import m2.microservices.AccountService.domain.port.out.AccountRepositoryPort;
import m2.microservices.AccountService.domain.port.out.EventPublisherPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountManagementService implements ManageAccountUseCase , GetAccountUseCase, GetBettingHistoryUseCase {
    private final AccountRepositoryPort accountRepository;
    private final EventPublisherPort eventPublisher;


    @Override
    @Transactional
    public void register(RegisterAccountCommand command) {
        log.info("Registering account for email: {}", command.email());
        if (accountRepository.existsByEmail(command.email())){
            throw new IllegalArgumentException("Email already exists : " + command.email());
        }
        AccountId accountId = new AccountId(UUID.randomUUID().toString());
        UserProfile userProfile = new UserProfile(
                command.email(),
                command.firstName(),
                command.lastName(),
                command.phoneNumber()
        );
        Account newAccount = Account.create(accountId , command.keycloakId() , userProfile);
        saveAndPublishEvents(newAccount);
    }


    @Override
    @Transactional
    public void updateProfile(UpdateProfileCommand command) {
        Account account = loadAccount(command.accountId());
        account.updateProfile(
                command.newFirstName(),
                command.newLastName(),
                command.newPhoneNumber()
        );
        saveAndPublishEvents(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getById(AccountId id) {
        return loadAccount(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getByKeycloakId(String keycloakId) {
        return accountRepository.findByKeycloakId(new KeycloakId(keycloakId))
                .orElseThrow(() -> new IllegalArgumentException("Account not found with Keycloak ID: " + keycloakId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BetHistoryItem> getHistory(AccountId id, int page, int size) {
        return accountRepository.findHistoryByAccountId(id, page, size);
    }

    private Account loadAccount(AccountId accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId.value()));
    }

    private void saveAndPublishEvents(Account newAccount) {
        accountRepository.save(newAccount);
        for (DomainEvent event : newAccount.getDomainEvents()) {
            eventPublisher.publish(event);
        }
        newAccount.clearEvents();
    }
}
