package m2.microservices.WalletService.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "wallet")
@EqualsAndHashCode(exclude = "wallet")
public class WalletTransactionEntity {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private WalletEntity wallet;

    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private String type; // WAGER, WIN...
    private String referenceId;
    private LocalDateTime createdAt;
}
