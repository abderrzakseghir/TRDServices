package m2.microservices.AccountService.adapter.out.persistence.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bet_history")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BetHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bet_id" , nullable = false)
    private String betId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    private String gameName;
    private String marketName;
    private String selection;

    @Column(precision = 10, scale = 2) // Stores 1.50, 2.25
    private BigDecimal odds;

    @Column(precision = 19, scale = 2) // Stores Money
    private BigDecimal amountWagered;

    @Column(precision = 19, scale = 2)
    private BigDecimal payout;

    private String status; // PENDING, WON, LOST

    private LocalDateTime placedAt;
}
