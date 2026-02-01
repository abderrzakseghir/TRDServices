package m2.microservices.bet_lifecycle_service.adapter.in.web.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import m2.microservices.bet_lifecycle_service.adapter.in.web.dto.BetResponse;
import m2.microservices.bet_lifecycle_service.adapter.in.web.dto.PlaceBetRequest;
import m2.microservices.bet_lifecycle_service.domain.model.vo.BetId;
import m2.microservices.bet_lifecycle_service.domain.port.in.command.PlaceBetCommand;
import m2.microservices.bet_lifecycle_service.domain.port.in.usecase.PlaceBetUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/v1/bets")
@RequiredArgsConstructor
public class BetController {

    private final PlaceBetUseCase placeBetUseCase;

    @PostMapping
    public ResponseEntity<BetResponse> placeBet(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody PlaceBetRequest request

    ){
        System.out.println("🔥🔥🔥 REQUEST RECEIVED IN CONTROLLER 🔥🔥🔥");
        String accountId = jwt.getSubject();
        var command = new PlaceBetCommand(
                accountId,
                request.amountWagered(),
                request.selections().stream()
                        .map(s -> new PlaceBetCommand.SelectionCommand(
                                s.matchId(), s.marketName(), s.selectionName(), s.odd()
                        )).toList()
        );

        BetId betId = placeBetUseCase.placeBet(command);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BetResponse(betId.value(), "AWAITING_PAYMENT", null));
    }
}
