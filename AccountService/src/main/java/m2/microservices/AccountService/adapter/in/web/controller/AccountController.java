package m2.microservices.AccountService.adapter.in.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import m2.microservices.AccountService.adapter.in.web.dto.AccountProfileResponse;
import m2.microservices.AccountService.adapter.in.web.dto.BetHistoryResponse;
import m2.microservices.AccountService.adapter.in.web.dto.RegisterRequest;
import m2.microservices.AccountService.adapter.in.web.dto.UpdateProfileRequest;
import m2.microservices.AccountService.domain.model.Account;
import m2.microservices.AccountService.domain.model.BetHistoryItem;
import m2.microservices.AccountService.domain.model.vo.KeycloakId;
import m2.microservices.AccountService.domain.port.in.GetAccountUseCase;
import m2.microservices.AccountService.domain.port.in.GetBettingHistoryUseCase;
import m2.microservices.AccountService.domain.port.in.ManageAccountUseCase;
import m2.microservices.AccountService.domain.port.in.command.RegisterAccountCommand;

import m2.microservices.AccountService.domain.port.in.command.UpdateProfileCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final ManageAccountUseCase manageUseCase;
    private final GetBettingHistoryUseCase historyUseCase;
    private final GetAccountUseCase getUseCase;

    @PostMapping("/register")
    public ResponseEntity<Void> registerAccount(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RegisterRequest request
    ) {
        var command = new RegisterAccountCommand(
                new KeycloakId(jwt.getSubject()),
                request.email(),
                request.firstName(),
                request.lastName(),
                request.phoneNumber()
        );
        manageUseCase.register(command);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AccountProfileResponse> getMyProfile(
            @AuthenticationPrincipal Jwt jwt
    ){
        Account account = getUseCase.getByKeycloakId(jwt.getSubject());
        List<BetHistoryResponse> recentHistory = account.getBetHistory().stream()
                .sorted((a, b) -> b.getPlacedAt().compareTo(a.getPlacedAt()))
                .limit(5) //
                .map(this::mapToHistoryDto)
                .toList();
        var response = new AccountProfileResponse(
                account.getAccountId().value(),
                account.getUserProfile().email(),
                account.getUserProfile().firstName(),
                account.getUserProfile().lastName(),
                account.getUserProfile().phoneNumber(),
                recentHistory
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/bet-history")
    public ResponseEntity<List<BetHistoryResponse>> getFullHistory(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        Account account = getUseCase.getByKeycloakId(jwt.getSubject());
        List<BetHistoryItem> historyItems = historyUseCase.getHistory(
                account.getAccountId(),
                page,
                size
        );
        List<BetHistoryResponse> response = historyItems.stream()
                .map(this::mapToHistoryDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    private BetHistoryResponse mapToHistoryDto(BetHistoryItem item) {
        return new BetHistoryResponse(
                item.getBetId(),
                item.getGameName(),
                item.getMarketName(),
                item.getSelection(),
                item.getOdds(),
                item.getAmountWagered().amount(),
                item.getPayoutAmount().amount(),
                item.getStatus().name(),
                item.getPlacedAt()
        );
    }


    @PatchMapping("/me")
    public ResponseEntity<Void> updateProfile(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody UpdateProfileRequest request
    ) {
        Account current = getUseCase.getByKeycloakId(jwt.getSubject());

        String newFirst = request.firstName() != null ? request.firstName() : current.getUserProfile().firstName();
        String newLast = request.lastName() != null ? request.lastName() : current.getUserProfile().lastName();
        String newPhone = request.phoneNumber() != null ? request.phoneNumber() : current.getUserProfile().phoneNumber();

        manageUseCase.updateProfile(new UpdateProfileCommand(
                current.getAccountId(),
                newFirst,
                newLast,
                newPhone
        ));

        return ResponseEntity.ok().build();
    }
}
