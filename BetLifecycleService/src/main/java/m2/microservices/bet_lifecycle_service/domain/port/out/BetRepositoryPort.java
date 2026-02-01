package m2.microservices.bet_lifecycle_service.domain.port.out;

import m2.microservices.bet_lifecycle_service.domain.model.Bet;
import m2.microservices.bet_lifecycle_service.domain.model.vo.BetId;

import java.util.Optional;

public interface BetRepositoryPort {
    void save(Bet bet);
    Optional<Bet> findById(BetId id);
}
