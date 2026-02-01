package m2.microservices.AccountService.adapter.out.persistence.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
    @Id
    private String id;
    @Column(name = "keycloak_id", nullable = false, unique = true)
    private String keycloakId;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name="phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BetHistoryEntity> betHistory = new ArrayList<>();

}
