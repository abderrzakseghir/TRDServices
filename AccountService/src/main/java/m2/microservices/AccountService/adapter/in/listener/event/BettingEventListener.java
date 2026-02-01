package m2.microservices.AccountService.adapter.in.listener.event;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2.microservices.AccountService.adapter.in.listener.dto.BetPlacedIntegrationEvent;
import m2.microservices.AccountService.adapter.in.listener.dto.BetSettledIntegrationEvent;
import m2.microservices.AccountService.domain.model.vo.AccountId;
import m2.microservices.AccountService.domain.model.vo.Money;
import m2.microservices.AccountService.domain.port.in.BettingRecordActivityUseCase;
import m2.microservices.AccountService.domain.port.in.command.RecordBetPlacementCommand;
import m2.microservices.AccountService.domain.port.in.command.RecordBetSettlementCommand;
import m2.microservices.AccountService.infrastructure.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BettingEventListener {
    private final BettingRecordActivityUseCase activityUseCase;

    @RabbitListener(queues = RabbitConfig.Q_BET_PLACEMENT)
    public void handleBetPlaced(BetPlacedIntegrationEvent event) {
        log.info("Received Bet PLACED event for betId: {}", event.betId());

        var command = new RecordBetPlacementCommand(
                new AccountId(event.accountId()),
                event.betId(),
                event.gameName(),
                event.marketName(),
                event.selection(),
                event.odds(),
                Money.of(event.amountWagered().doubleValue()),
                event.placedAt()
        );
        activityUseCase.recordBetPlacement(command);

    }

    @RabbitListener(queues = RabbitConfig.Q_BET_SETTLEMENT)
    public void handleBetSettled(BetSettledIntegrationEvent event) {
        log.info("Received Bet SETTLED event for betId: {}", event.betId());

        var command = new RecordBetSettlementCommand(
                new AccountId(event.accountId()),
                event.betId(),
                event.isWin(),
                Money.of(event.payoutMoney().doubleValue())
        );

        activityUseCase.recordBetSettlement(command);
    }
}
