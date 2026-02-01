package m2.microservices.bet_lifecycle_service.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "bet_selections")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "bet")
@EqualsAndHashCode(exclude = "bet")
public class SelectionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bet_id", nullable = false)
    private BetEntity bet ;

    private String matchId;
    private String marketName;
    private String selectionName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal odd;
}
