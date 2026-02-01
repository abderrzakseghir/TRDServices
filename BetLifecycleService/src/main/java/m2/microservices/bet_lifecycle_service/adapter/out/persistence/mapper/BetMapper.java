package m2.microservices.bet_lifecycle_service.adapter.out.persistence.mapper;


import m2.microservices.bet_lifecycle_service.adapter.out.persistence.entity.BetEntity;
import m2.microservices.bet_lifecycle_service.adapter.out.persistence.entity.SelectionEntity;
import m2.microservices.bet_lifecycle_service.domain.model.Bet;
import m2.microservices.bet_lifecycle_service.domain.model.Selection;
import m2.microservices.bet_lifecycle_service.domain.model.vo.BetId;
import m2.microservices.bet_lifecycle_service.domain.model.vo.BetStatus;
import m2.microservices.bet_lifecycle_service.domain.model.vo.BetType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BetMapper {

    public BetEntity toEntity(Bet domain) {
        BetEntity entity = new BetEntity();
        entity.setId(domain.getBetId().value());
        entity.setAccountId(domain.getAccountId());
        entity.setType(domain.getType().name());
        entity.setStatus(domain.getStatus().name());
        entity.setAmountWagered(domain.getAmountWagered());
        entity.setTotalOdds(domain.getTotalOdds());
        entity.setPotentialPayout(domain.getPotentialPayout());
        entity.setCreatedAt(domain.getCreatedAt());

        // Map Selections
        List<SelectionEntity> selectionEntities = domain.getSelections().stream()
                .map(s -> toSelectionEntity(s, entity))
                .collect(Collectors.toList());

        entity.setSelections(selectionEntities);
        return entity;
    }

    private SelectionEntity toSelectionEntity(Selection s, BetEntity parent) {
        SelectionEntity entity = new SelectionEntity();
        entity.setBet(parent);
        entity.setMatchId(s.getMatchId());
        entity.setMarketName(s.getMarketName());
        entity.setSelectionName(s.getSelectionName());
        entity.setOdd(s.getOdd());
        return entity;
    }

    public Bet toDomain(BetEntity entity) {
        List<Selection> selections = entity.getSelections().stream()
                .map(s -> new Selection(s.getMatchId(), s.getMarketName(), s.getSelectionName(), s.getOdd()))
                .collect(Collectors.toList());

        return new Bet(
                new BetId(entity.getId()),
                entity.getAccountId(),
                selections,
                BetType.valueOf(entity.getType()),
                entity.getAmountWagered(),
                entity.getPotentialPayout(),
                entity.getTotalOdds(),
                BetStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                new ArrayList<>()
        );
    }
}
