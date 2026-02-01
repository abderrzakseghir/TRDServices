package m2.microservices.bet_lifecycle_service.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import m2.microservices.bet_lifecycle_service.adapter.out.persistence.mapper.BetMapper;
import m2.microservices.bet_lifecycle_service.adapter.out.persistence.repository.SpringDataBetRepository;
import m2.microservices.bet_lifecycle_service.domain.model.Bet;
import m2.microservices.bet_lifecycle_service.domain.model.vo.BetId;
import m2.microservices.bet_lifecycle_service.domain.port.out.BetRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BetPersistenceAdapter implements BetRepositoryPort {
    private final SpringDataBetRepository repository;
    private final BetMapper mapper;

    @Override
    public void save(Bet bet) {
        repository.save(mapper.toEntity(bet));
    }

    @Override
    public Optional<Bet> findById(BetId id) {
        return repository.findById(id.value())
                .map(mapper::toDomain);
    }
}
