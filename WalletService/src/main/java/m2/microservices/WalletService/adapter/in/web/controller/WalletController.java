package m2.microservices.WalletService.adapter.in.web.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import m2.microservices.WalletService.adapter.in.web.dto.TransactionRequest;
import m2.microservices.WalletService.adapter.in.web.dto.WalletResponse;
import m2.microservices.WalletService.domain.model.Wallet;
import m2.microservices.WalletService.domain.port.in.command.DepositCommand;
import m2.microservices.WalletService.domain.port.in.command.WithdrawCommand;
import m2.microservices.WalletService.domain.port.in.usecase.ManageWalletUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {
    private final ManageWalletUseCase manageWalletUseCase;

    public ResponseEntity<WalletResponse> getMyWallet(@AuthenticationPrincipal Jwt jwt) {
        String accountId = jwt.getSubject();

        Wallet wallet = manageWalletUseCase.getWalletByAccountId(accountId);

        return ResponseEntity.ok(new WalletResponse(
                wallet.getAccountId(),
                wallet.getBalance().amount(),
                "EUR"
        ));
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TransactionRequest request
    ) {
        var command = new DepositCommand(
                jwt.getSubject(),
                request.paymentReference(),
                request.amount()
        );

        manageWalletUseCase.depositFunds(command);

        return ResponseEntity.ok().build();
    }


    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TransactionRequest request
    ) {
        var command = new WithdrawCommand(
                jwt.getSubject(),
                request.amount()
        );

        manageWalletUseCase.withdrawFunds(command);

        return ResponseEntity.ok().build();
    }
}
