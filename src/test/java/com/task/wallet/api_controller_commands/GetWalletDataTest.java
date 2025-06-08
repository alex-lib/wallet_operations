package com.task.wallet.api_controller_commands;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.wallet.controllers.ApiController;
import com.task.wallet.dto.WalletDto;
import com.task.wallet.exceptions.WalletNotFoundException;
import com.task.wallet.services.crud.CRUDService;
import com.task.wallet.services.operations.OperationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
class GetWalletDataTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OperationService operationService;
    @MockBean
    private CRUDService crudService;

    @Test
    @DisplayName("Test for getting wallet data by existed wallet's id")
    void whenGetWalletData_thenReturnWalletDto() throws Exception {
        String walletId = "56897422-d900-4b5c-9d90-5bd95a65917f";

        WalletDto walletDto = new WalletDto();
        walletDto.setId(walletId);
        walletDto.setBalance(new BigDecimal("183641.14"));
        walletDto.setOwnerFirstName("Moritz");
        walletDto.setOwnerLastName("Marriner");

        when(crudService.getDataWallet(any())).thenReturn(walletDto);
        mockMvc.perform(get("/api/v1/wallet/{walletUuid}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(walletId))
                .andExpect(jsonPath("$.balance").value(183641.14))
                .andExpect(jsonPath("$.ownerFirstName").value("Moritz"))
                .andExpect(jsonPath("$.ownerLastName").value("Marriner"));
    }

    @Test
    @DisplayName("Test for getting wallet data by non-existent wallet's id")
    void whenGetWalletDataWithNonExistentId_thenReturnNotFoundException() throws Exception {
        String nonExistentWalletId = "00000000-0000-0000-0000-000000000000";
        String errorMessage = "Wallet with id: " + nonExistentWalletId + "is not found";

        when(crudService.getDataWallet(any(String.class)))
                .thenThrow(new WalletNotFoundException(errorMessage));

        mockMvc.perform(get("/api/v1/wallet/{walletUuid}", nonExistentWalletId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }
}