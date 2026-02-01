package m2.microservices.WalletService.adapter.in.listener.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2.microservices.WalletService.adapter.in.listener.dto.AccountRegisteredIntegrationEvent;
import m2.microservices.WalletService.adapter.in.listener.dto.BetPlacedIntegrationEvent;
import m2.microservices.WalletService.adapter.in.listener.dto.BetSettledIntegrationEvent;
import m2.microservices.WalletService.domain.port.in.command.CreateWalletCommand;
import m2.microservices.WalletService.domain.port.in.command.ProcessWagerCommand;
import m2.microservices.WalletService.domain.port.in.command.ProcessWinCommand;
import m2.microservices.WalletService.domain.port.in.usecase.ManageWalletUseCase;
import m2.microservices.WalletService.infrastructure.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WalletEventListener {
    private final ManageWalletUseCase walletUseCase;

    // 1. CREATE WALLET (When user registers)
    @RabbitListener(queues = RabbitConfig.Q_ACCOUNT_EVENTS)
    public void onAccountRegistered(AccountRegisteredIntegrationEvent event) {
        log.info("Creating wallet for new account: {}", event.accountId());
        walletUseCase.createWallet(new CreateWalletCommand(event.accountId()));
    }

    // 2. PROCESS WAGER (SAGA Step - Debit)
    @RabbitListener(queues = RabbitConfig.Q_BET_PLACED)
    public void onBetPlaced(BetPlacedIntegrationEvent event) {
        log.info("Processing wager for bet: {}", event.betId());

        var command = new ProcessWagerCommand(
                event.accountId(),
                event.betId(),
                event.amountWagered()
        );

        walletUseCase.processBetWager(command);
    }

    // 3. PROCESS WIN / REFUND (Settlement)
    @RabbitListener(queues = RabbitConfig.Q_BET_SETTLED)
    public void onBetSettled(BetSettledIntegrationEvent event) {
        log.info("Processing settlement for bet: {} | Outcome: {}", event.betId(), event.outcome());

        if ("WON".equals(event.outcome())) {
            var command = new ProcessWinCommand(
                    event.accountId(),
                    event.betId(),
                    event.amountWon()
            );
            walletUseCase.processBetWin(command);

        } else if ("VOID".equals(event.outcome())) {
            var command = new ProcessWinCommand(
                    event.accountId(),
                    event.betId(),
                    event.amountWon() // This should be equal to original stake
            );
            walletUseCase.processRefund(command);
        }
    }
}
