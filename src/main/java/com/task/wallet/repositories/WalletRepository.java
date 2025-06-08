package com.task.wallet.repositories;
import com.task.wallet.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Integer> {
    Wallet findById(UUID userId);
}