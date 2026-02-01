package m2.microservices.AccountService.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2.microservices.AccountService.domain.model.Account;
import m2.microservices.AccountService.domain.port.in.BettingRecordActivityUseCase;
import m2.microservices.AccountService.domain.port.in.command.RecordBetPlacementCommand;
import m2.microservices.AccountService.domain.port.in.command.RecordBetSettlementCommand;
import m2.microservices.AccountService.domain.port.out.AccountRepositoryPort;
import m2.microservices.AccountService.domain.port.out.EventPublisherPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BettingActivityService implements BettingRecordActivityUseCase {
    private final AccountRepositoryPort accountRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    @Transactional
    public void recordBetPlacement(RecordBetPlacementCommand command) {
        log.debug("Recording bet placement: {}", command.betId());
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + command.accountId()));
        account.placeBet(
                command.betId(),
                command.gameName(),
                command.marketName(),
                command.selection(),
                command.odds(),
                command.amountWagered(),
                command.placedAt()
        );
        saveAndPublishEvents(account);
    }

    @Override
    @Transactional
    public void recordBetSettlement(RecordBetSettlementCommand command) {
        log.debug("Recording bet settlement: {}", command.betId());
        Account account = accountRepository.findById(command.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + command.accountId()));
        account.settleBet(
                command.betId(),
                command.isWin(),
                command.payoutAmount()
        );
        saveAndPublishEvents(account);
    }

    private void saveAndPublishEvents(Account account) {
        accountRepository.save(account);
        account.getDomainEvents().forEach(eventPublisher::publish);
        account.getDomainEvents().clear();
    }


}
