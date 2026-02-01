package m2.microservices.AccountService.domain.port.in;

import m2.microservices.AccountService.domain.model.BetHistoryItem;
import m2.microservices.AccountService.domain.model.vo.AccountId;

import java.util.List;

public interface GetBettingHistoryUseCase {
    List<BetHistoryItem> getHistory(AccountId accountId , int page , int size);
}
