package com.task.wallet.services.crud;
import com.task.wallet.dto.WalletDto;
import com.task.wallet.entities.Wallet;
import com.task.wallet.exceptions.WalletNotFoundException;
import com.task.wallet.repositories.WalletRepository;
import com.task.wallet.services.ValidatorParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.RoundingMode;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CRUDServiceImpl implements CRUDService {
private final WalletRepository walletRepository;

    @Override
    public WalletDto getDataWallet(String userId) {
        WalletDto walletDto = new WalletDto();
        UUID walletId = ValidatorParameters.validateUserId(userId);
        Wallet wallet = walletRepository.findById(walletId);
        if (wallet != null) {
           walletDto.setId(String.valueOf(walletId));
           walletDto.setBalance(wallet.getBalance().setScale(2, RoundingMode.HALF_UP));
           walletDto.setOwnerFirstName(wallet.getOwnerFirstName());
           walletDto.setOwnerLastName(wallet.getOwnerLastName());
           log.debug("Wallet found for user ID: {}", userId);
            return walletDto;
        } else {
            log.error("Wallet not found for user ID: {}", userId);
            throw new WalletNotFoundException("Wallet with id: " + userId + " is not found");
        }
    }
}