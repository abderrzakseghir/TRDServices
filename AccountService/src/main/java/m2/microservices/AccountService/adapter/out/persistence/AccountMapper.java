package m2.microservices.AccountService.adapter.out.persistence;

import m2.microservices.AccountService.adapter.out.persistence.entity.AccountEntity;
import m2.microservices.AccountService.adapter.out.persistence.entity.BetHistoryEntity;
import m2.microservices.AccountService.domain.model.Account;
import m2.microservices.AccountService.domain.model.BetHistoryItem;
import m2.microservices.AccountService.domain.model.vo.AccountId;
import m2.microservices.AccountService.domain.model.vo.KeycloakId;
import m2.microservices.AccountService.domain.model.vo.Money;
import m2.microservices.AccountService.domain.model.vo.UserProfile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper {
    public AccountEntity toEntity(Account domain){
        AccountEntity entity = new AccountEntity();
        entity.setId(domain.getAccountId().value());
        entity.setKeycloakId(domain.getKeycloakId().value());
        entity.setEmail(domain.getUserProfile().email());
        entity.setFirstName(domain.getUserProfile().firstName());
        entity.setLastName(domain.getUserProfile().lastName());
        entity.setPhoneNumber(domain.getUserProfile().phoneNumber());

        // Map Children
        List<BetHistoryEntity> historyEntities = domain.getBetHistory().stream()
                .map(item -> toHistoryEntity(item, entity))
                .collect(Collectors.toList());

        entity.setBetHistory(historyEntities);
        return entity;
    }

    public BetHistoryEntity toHistoryEntity(BetHistoryItem item, AccountEntity parent) {
        BetHistoryEntity entity = new BetHistoryEntity();

        entity.setBetId(item.getBetId());
        entity.setAccount(parent); // Link parent
        entity.setGameName(item.getGameName());
        entity.setMarketName(item.getMarketName());
        entity.setSelection(item.getSelection());
        entity.setOdds(item.getOdds());
        entity.setAmountWagered(item.getAmountWagered().amount());
        entity.setPayout(item.getPayoutAmount().amount());
        entity.setStatus(item.getStatus().name());
        entity.setPlacedAt(item.getPlacedAt());
        return entity;
    }

    // --- ENTITY -> DOMAIN ---
    public Account toDomain(AccountEntity entity) {
        return new Account(
                new AccountId(entity.getId()),
                new KeycloakId(entity.getKeycloakId()),
                new UserProfile(
                        entity.getEmail(),
                        entity.getFirstName(),
                        entity.getLastName(),
                        entity.getPhoneNumber()
                ),
                entity.getBetHistory().stream().map(this::toHistoryItem).collect(Collectors.toList())
        );
    }

    public BetHistoryItem toHistoryItem(BetHistoryEntity entity) {

        // Assuming you added this factory to BetHistoryItem:
        return BetHistoryItem.reconstitute(
                entity.getBetId(),
                entity.getGameName(),
                entity.getMarketName(),
                entity.getSelection(),
                entity.getOdds(),
                Money.of(entity.getAmountWagered()),
                Money.of(entity.getPayout()),
                BetHistoryItem.BetStatus.valueOf(entity.getStatus()),
                entity.getPlacedAt()
        );
    }
}
