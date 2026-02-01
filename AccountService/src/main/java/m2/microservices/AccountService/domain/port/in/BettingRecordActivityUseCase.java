package m2.microservices.AccountService.domain.port.in;

import m2.microservices.AccountService.domain.port.in.command.RecordBetPlacementCommand;
import m2.microservices.AccountService.domain.port.in.command.RecordBetSettlementCommand;

public interface BettingRecordActivityUseCase {
    void recordBetPlacement(RecordBetPlacementCommand command);

    // Called when "BetSettledEvent" arrives
    void recordBetSettlement(RecordBetSettlementCommand command);
}
