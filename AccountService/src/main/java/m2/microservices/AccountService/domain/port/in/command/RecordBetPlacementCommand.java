package m2.microservices.AccountService.domain.port.in.command;

import m2.microservices.AccountService.domain.model.vo.AccountId;
import m2.microservices.AccountService.domain.model.vo.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RecordBetPlacementCommand(
        AccountId accountId,
        String betId,
        String gameName,
        String marketName,
        String selection,
        BigDecimal odds,
        Money amountWagered,
        LocalDateTime placedAt
) {}
