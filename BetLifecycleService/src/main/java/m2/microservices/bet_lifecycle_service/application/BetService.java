package m2.microservices.bet_lifecycle_service.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2.microservices.bet_lifecycle_service.domain.event.DomainEvent;
import m2.microservices.bet_lifecycle_service.domain.model.Bet;
import m2.microservices.bet_lifecycle_service.domain.model.Selection;
import m2.microservices.bet_lifecycle_service.domain.model.vo.BetId;
import m2.microservices.bet_lifecycle_service.domain.port.in.command.PlaceBetCommand;
import m2.microservices.bet_lifecycle_service.domain.port.in.usecase.PlaceBetUseCase;
import m2.microservices.bet_lifecycle_service.domain.port.out.BetRepositoryPort;
import m2.microservices.bet_lifecycle_service.domain.port.out.EventPublisherPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BetService implements PlaceBetUseCase {
    private final BetRepositoryPort betRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    @Transactional
    public BetId placeBet(PlaceBetCommand command) {
        log.info("Placing bet for accountId: {}", command.accountId());

        List<Selection> selections = command.selections().stream()
                .map(selCmd -> new Selection(
                        selCmd.matchId(),
                        selCmd.marketName(),
                        selCmd.selectionName(),
                        selCmd.odd()
                ))
                .toList();

        Bet bet = Bet.place(
                command.accountId(),
                selections,
                command.amountWagered()
        );

        saveAndPublishEvents(bet);
        return bet.getBetId();
    }

    @Override
    @Transactional
    public void confirmBet(String betId) {
        log.info("Confirm bet with betId : {}" , betId);
        Bet bet = betRepository.findById(new BetId(betId))
                .orElseThrow(() -> new IllegalArgumentException("Bet not found with id: " + betId));

        bet.confirmPayment();
        saveAndPublishEvents(bet);
    }

    private void saveAndPublishEvents(Bet bet) {
        // A. Save to Postgres
        betRepository.save(bet);

        // (BetCreated -> Payment Service, BetConfirmed -> Settlement Service)
        for (DomainEvent event : bet.getDomainEvents()) {
            eventPublisher.publish(event);
        }

        bet.clearEvents();
    }
}
