package m2.microservices.WalletService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import m2.microservices.WalletService.domain.event.DomainEvent;
import m2.microservices.WalletService.domain.event.FundsReservedEvent;
import m2.microservices.WalletService.domain.exception.InsufficientFundsException;
import m2.microservices.WalletService.domain.exception.WalletNotFoundException;
import m2.microservices.WalletService.domain.model.Wallet;
import m2.microservices.WalletService.domain.model.vo.TransactionType;
import m2.microservices.WalletService.domain.port.in.command.*;
import m2.microservices.WalletService.domain.port.in.usecase.ManageWalletUseCase;
import m2.microservices.WalletService.domain.port.out.EventPublisherPort;
import m2.microservices.WalletService.domain.port.out.WalletRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService implements ManageWalletUseCase {
    private final WalletRepositoryPort walletRepository;
    private final EventPublisherPort eventPublisher;

    @Override
    @Transactional
    public void createWallet(CreateWalletCommand command) {
        if (walletRepository.existsByAccountId(command.AccountId())) {
            log.warn("Wallet already exists for accountId: {}", command.AccountId());
            return;
        }
        log.info("Creating wallet for accountId: {}", command.AccountId());
        Wallet wallet = Wallet.create(
                command.AccountId()
        );
        saveAndPublishEvents(wallet);
    }

    @Override
    @Transactional
    public boolean processBetWager(ProcessWagerCommand command) {
        Wallet wallet = getWalletOrThrow(command.accountId());
        try {
            log.debug("Attempting to wager {} on bet {}" , command.amount(), command.betId());
            wallet.debit(command.amount(), TransactionType.WAGER, command.betId());
            walletRepository.save(wallet);

            eventPublisher.publish(new FundsReservedEvent(
                    command.betId(),
                    "APPROVED",
                    null
            ));
            flushDomainEvents(wallet);

            return true;
        }catch (InsufficientFundsException e) {
            log.warn("SAGA REJECTED: Insufficient funds for bet {}", command.betId());

            // 4. SAGA FAILURE: Notify Bet Service to Cancel Ticket
            eventPublisher.publish(new FundsReservedEvent(
                    command.betId(),
                    "REJECTED",
                    "Insufficient Funds"
            ));
            return false;
        }

    }

    @Override
    @Transactional
    public void processBetWin(ProcessWinCommand command) {
        log.info("Processing Win: {} for bet {}", command.amount(), command.betId());

        Wallet wallet = getWalletOrThrow(command.accountId());

        wallet.credit(
                command.amount(),
                TransactionType.WIN,
                command.betId()
        );

        saveAndPublishEvents(wallet);
    }

    @Override
    @Transactional
    public void processRefund(ProcessWinCommand command) {
        log.info("Processing Refund: {} for bet {}", command.amount(), command.betId());

        Wallet wallet = getWalletOrThrow(command.accountId());

        wallet.credit(
                command.amount(),
                TransactionType.REFUND,
                command.betId()
        );

        saveAndPublishEvents(wallet);
    }



    @Override
    @Transactional
    public void depositFunds(DepositCommand command) {
        log.info("Processing Deposit: {} from ref {}", command.amount(), command.paymentReference());

        Wallet wallet = getWalletOrThrow(command.accountId());

        wallet.credit(
                command.amount(),
                TransactionType.DEPOSIT,
                command.paymentReference()
        );

        saveAndPublishEvents(wallet);
    }

    @Override
    @Transactional
    public void withdrawFunds(WithdrawCommand command) {
        log.info("Processing Withdraw: {} from ref {}", command.amount());

        Wallet wallet = getWalletOrThrow(command.accountId());

        wallet.debit(
                command.amount(),
                TransactionType.WITHDRAWAL
        );

        saveAndPublishEvents(wallet);
    }

    @Override
    @Transactional(readOnly = true)
    public Wallet getWalletByAccountId(String accountId) {
        return getWalletOrThrow(accountId);
    }


    private Wallet getWalletOrThrow(String accountId) {
        return walletRepository.findByAccountId(accountId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for account: " + accountId) {
                });
    }

    private void saveAndPublishEvents(Wallet wallet) {
        walletRepository.save(wallet);
        flushDomainEvents(wallet);
    }

    private void flushDomainEvents(Wallet wallet) {
        for (DomainEvent event : wallet.getDomainEvents()) {
            eventPublisher.publish(event);
        }
        wallet.clearEvents();
//        wallet.clearTransactions();
    }
}
