package m2.microservices.AccountService.domain.port.in;

import m2.microservices.AccountService.domain.model.Account;
import m2.microservices.AccountService.domain.model.vo.AccountId;

public interface GetAccountUseCase {
    Account getById(AccountId id);
    Account getByKeycloakId(String keycloakId);
}
