package m2.microservices.AccountService.domain.port.in.command;

import m2.microservices.AccountService.domain.model.vo.AccountId;
import m2.microservices.AccountService.domain.model.vo.Money;

public record RecordBetSettlementCommand(
        AccountId accountId,
        String betId,
        Boolean isWin,
        Money payoutAmount
) {}
