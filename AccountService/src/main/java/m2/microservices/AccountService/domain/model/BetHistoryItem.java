package m2.microservices.AccountService.domain.model;

import lombok.Getter;
import m2.microservices.AccountService.domain.model.vo.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class BetHistoryItem {
    private final String betId;

    private final String gameName;
    private final String marketName;
    private final String selection;

    private final Money amountWagered;
    private final BigDecimal odds ;
    private  Money payoutAmount;

    private  BetStatus status;
    private final LocalDateTime placedAt;

    public BetHistoryItem(String betId, String gameName, String marketName, String selection, Money amountWagered, BigDecimal odds, Money payoutAmount, BetStatus status, LocalDateTime settledAt) {
        this.betId = betId;
        this.gameName = gameName;
        this.marketName = marketName;
        this.selection = selection;
        this.amountWagered = amountWagered;
        this.odds = odds;
        this.payoutAmount = payoutAmount;
        this.status = status;
        this.placedAt = settledAt;
    }


    public static BetHistoryItem pending(String betId, String gameName, String marketName, String selection ,BigDecimal odds, Money wagerAmount, LocalDateTime placedAt) {
        return new BetHistoryItem(
                betId,
                gameName,
                marketName,
                selection,
                wagerAmount,
                odds,
                Money.ZERO,
                BetStatus.PENDING,
                placedAt
        );
    }

    public void settle(boolean isWin, Money payout) {
        this.status = isWin ? BetStatus.WON : BetStatus.LOST;
        this.payoutAmount = isWin ? payout : Money.ZERO;
    }

    public static BetHistoryItem reconstitute(
            String betId, String gameName, String marketName, String selection,
            BigDecimal odds, Money wager, Money payout, BetStatus status, LocalDateTime placedAt
    ) {
        return new BetHistoryItem(betId, gameName, marketName, selection,wager, odds, payout, status, placedAt);
    }

    public Money getProfit(){
        return this.payoutAmount.subtract(amountWagered);
    }

    public enum BetStatus {
        WON,
        LOST,
        PENDING,
    }
}
