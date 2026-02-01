package m2.microservices.AccountService.domain.model;

import lombok.Getter;
import m2.microservices.AccountService.domain.event.*;
import m2.microservices.AccountService.domain.model.vo.AccountId;
import m2.microservices.AccountService.domain.model.vo.KeycloakId;
import m2.microservices.AccountService.domain.model.vo.Money;
import m2.microservices.AccountService.domain.model.vo.UserProfile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Account {
    private final AccountId accountId;
    private final KeycloakId keycloakId;
    private  UserProfile userProfile;


    private final List<BetHistoryItem> betHistory;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    public Account(AccountId accountId, KeycloakId keycloakId, UserProfile userProfile, List<BetHistoryItem> betHistory) {
        this.accountId = accountId;
        this.keycloakId = keycloakId;
        this.userProfile = userProfile;
        this.betHistory = betHistory;
    }

    // Register Account
    public static Account create(AccountId accountId, KeycloakId keycloakId, UserProfile userProfile) {
        Account account = new Account(
                accountId,
                keycloakId,
                userProfile,
                List.of()
        );

        account.addEvent(new AccountRegistered(keycloakId.value() , userProfile.email() ));
        return account;
    }

    public void updateProfile(String firstName, String lastName, String phoneNumber) {
        this.userProfile = new UserProfile(firstName, lastName, this.userProfile.email(), phoneNumber);
        this.addEvent(new AccountProfileUpdated(this.accountId,this.userProfile.email(), firstName, lastName, phoneNumber));

    }

    public void placeBet(String betId, String gameName, String marketName, String selection, BigDecimal odds, Money amount, LocalDateTime placedAt) {
        if (amount.isPositive()) {
            BetHistoryItem betHistoryItem = BetHistoryItem.pending(betId, gameName,marketName, selection,odds,amount , placedAt);
            this.betHistory.add(betHistoryItem);
            this.addEvent(new BetAddedToHistory(this.accountId, betId, gameName, amount));
        } else {
            throw new IllegalArgumentException("Insufficient balance or invalid bet amount");
        }
    }


    public void settleBet(String betId, boolean isWin, Money payoutAmount) {
        BetHistoryItem item = this.betHistory.stream()
                .filter(b -> b.getBetId().equals(betId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("History mismatch"));

        item.settle(isWin, payoutAmount);

        this.addEvent(new BetSettlementRecorded(this.accountId, betId, isWin , payoutAmount));
    }

    public void clearEvents() {
        this.domainEvents.clear();
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(this.domainEvents);
    }

    private void addEvent(DomainEvent event) {
        domainEvents.add(event);
    }

}
