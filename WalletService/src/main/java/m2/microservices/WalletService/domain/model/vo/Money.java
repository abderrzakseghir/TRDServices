package m2.microservices.WalletService.domain.model.vo;

import java.math.BigDecimal;

public record Money(BigDecimal amount) {
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    public Money{
        if(amount == null || amount.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Amount cannot be null or negative");
        }
    }

    public static Money of(BigDecimal value){
        return new Money(value);
    }

    public static Money of(double value){
        return new Money(BigDecimal.valueOf(value));
    }

    public Money add(Money other){
        return new Money(this.amount.add(other.amount));
    }

    public Money subtract(Money other){
        return new Money(this.amount.subtract(other.amount));
    }

    public Money negate() {
        return new Money(this.amount.negate());
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }
    public boolean isGreaterThanOrEqual(Money other) {
        return this.amount.compareTo(other.amount) >= 0;
    }
}
