package m2.microservices.bet_lifecycle_service.domain.port.in.usecase;

import m2.microservices.bet_lifecycle_service.domain.model.vo.BetId;
import m2.microservices.bet_lifecycle_service.domain.port.in.command.PlaceBetCommand;

public interface PlaceBetUseCase {
    BetId placeBet(PlaceBetCommand command);

    void confirmBet(String betId);
}
