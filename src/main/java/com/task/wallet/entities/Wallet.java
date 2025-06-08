package com.task.wallet.entities;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallets")
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;
    @Column(name = "balance", columnDefinition = "NUMERIC")
    private BigDecimal balance;
    @Column(name = "owner_first_name", columnDefinition = "VARCHAR(50)", nullable = false)
    private String ownerFirstName;
    @Column(name = "owner_last_name", columnDefinition = "VARCHAR(50)", nullable = false)
    private String ownerLastName;
}