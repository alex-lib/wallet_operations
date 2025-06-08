package com.task.wallet.services.crud;
import com.task.wallet.dto.WalletDto;

public interface CRUDService {
    WalletDto getDataWallet(String userId);
}