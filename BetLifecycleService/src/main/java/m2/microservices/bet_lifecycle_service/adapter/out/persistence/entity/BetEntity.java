package m2.microservices.bet_lifecycle_service.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bets")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "selections")
@EqualsAndHashCode(exclude = "selections")
public class BetEntity {
    @Id
    private String id; // UUID

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    private String type; // SIMPLE , COMBINED

    @Column(nullable = false)
    private String status; // AWAITING_PAYMENT, CONFIRMED ... etc.

    @Column(name = "stake", nullable = false , precision = 19 , scale = 2)
    private BigDecimal amountWagered;

    @Column(nullable = false , precision = 19 , scale = 2)
    private BigDecimal potentialPayout;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalOdds;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "bet", cascade = CascadeType.ALL, fetch = FetchType.LAZY , orphanRemoval = true)
    private List<SelectionEntity> selections;


}
