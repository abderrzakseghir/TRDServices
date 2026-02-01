package m2.microservices.WalletService.domain.port.in.usecase;

import m2.microservices.WalletService.domain.model.Wallet;
import m2.microservices.WalletService.domain.port.in.command.*;

public interface ManageWalletUseCase {

    void createWallet(CreateWalletCommand command);

    boolean processBetWager(ProcessWagerCommand command);
    void processBetWin(ProcessWinCommand command);
    void processRefund(ProcessWinCommand command);

    void depositFunds(DepositCommand command);
    void withdrawFunds(WithdrawCommand command);

    Wallet getWalletByAccountId(String accountId);
}
