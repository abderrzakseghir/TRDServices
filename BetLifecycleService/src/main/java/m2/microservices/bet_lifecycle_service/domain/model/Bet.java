package m2.microservices.bet_lifecycle_service.domain.model;

import lombok.Getter;
import m2.microservices.bet_lifecycle_service.domain.event.BetConfirmed;
import m2.microservices.bet_lifecycle_service.domain.event.BetCreated;
import m2.microservices.bet_lifecycle_service.domain.event.DomainEvent;
import m2.microservices.bet_lifecycle_service.domain.model.vo.BetId;
import m2.microservices.bet_lifecycle_service.domain.model.vo.BetStatus;
import m2.microservices.bet_lifecycle_service.domain.model.vo.BetType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Bet {
    private final BetId betId;
    private final String accountId;
    private final List<Selection> selections;
    private final BetType type;

    private final BigDecimal amountWagered;
    private  BigDecimal potentialPayout;
    private  BigDecimal totalOdds;

    private BetStatus status;
    private final LocalDateTime createdAt;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private static final BigDecimal MIN_AMOUNT_WAGERED = BigDecimal.valueOf(1.00);
    private static final BigDecimal MAX_AMOUNT_WAGERED = BigDecimal.valueOf(1000.00);
    private static final int MAX_SELECTIONS = 10;


    public static Bet place(String accountId , List<Selection> selections, BigDecimal amountWagered){
        validate(selections, amountWagered);
        BetId betId = new BetId(UUID.randomUUID().toString());
        BetType type = selections.size() > 1 ? BetType.COMBINED : BetType.SIMPLE;
        Bet bet = new Bet(
                betId,
                accountId,
                type,
                selections,
                amountWagered,
                LocalDateTime.now()
        );
        bet.calculateFinancials();
        bet.addEvent(new BetCreated(
                betId.value(),
                accountId,
                amountWagered,
                bet.potentialPayout
        ));
        return bet;
    }
    public Bet(BetId betId, String accountId, List<Selection> selections, BetType betType, BigDecimal amountWagered, BigDecimal potentialPayout, BigDecimal totalOdds, BetStatus betStatus, LocalDateTime createdAt, List<DomainEvent> domainEvents) {
        this.betId = betId;
        this.accountId = accountId;
        this.selections = selections;
        this.type = betType;
        this.amountWagered = amountWagered;
        this.potentialPayout = potentialPayout;
        this.totalOdds = totalOdds;
        this.status = betStatus;
        this.createdAt = createdAt;
    }


    private Bet(BetId id, String accountId, BetType type, List<Selection> selections, BigDecimal stake, LocalDateTime createdAt) {
        this.betId = id;
        this.accountId = accountId;
        this.type = type;
        this.selections = selections;
        this.amountWagered = stake;
        this.status = BetStatus.AWAITING_PAYMENT;
        this.createdAt = createdAt;
    }

    private static void validate(List<Selection> selections, BigDecimal amountWagered) {
        if (amountWagered.compareTo(MIN_AMOUNT_WAGERED) < 0 ){
            throw new IllegalArgumentException("Amount wagered must be at least " + MIN_AMOUNT_WAGERED);
        }
        if (amountWagered.compareTo(MAX_AMOUNT_WAGERED) > 0 ){
            throw new IllegalArgumentException("Amount wagered must not exceed " + MAX_AMOUNT_WAGERED);
        }
        if (selections.size() > MAX_SELECTIONS) {
            throw new IllegalArgumentException("Number of selections must not exceed " + MAX_SELECTIONS);
        }
    }

    private void calculateFinancials(){
        this.totalOdds = selections.stream()
                .map(Selection::getOdd)
                .reduce(BigDecimal.ONE, BigDecimal::multiply);
        this.potentialPayout = this.amountWagered.multiply(this.totalOdds);
    }

    public void confirmPayment(){
        if (this.status != BetStatus.AWAITING_PAYMENT) return;

        this.status = BetStatus.CONFIRMED;

        // Notify Settlement Service: "This bet is active, track these matches!"
        this.addEvent(new BetConfirmed(
                this.betId.value(),
                this.accountId,
                this.selections, // Pass the list so Settlement knows what matches to watch
                this.potentialPayout
        ));
    }

    private void addEvent(DomainEvent event) { this.domainEvents.add(event); }
    public void clearEvents() { this.domainEvents.clear(); }
    public List<DomainEvent> getDomainEvents() { return Collections.unmodifiableList(domainEvents); }
}
