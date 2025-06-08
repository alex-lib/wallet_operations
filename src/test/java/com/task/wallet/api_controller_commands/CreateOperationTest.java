package com.task.wallet.api_controller_commands;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.wallet.controllers.ApiController;
import com.task.wallet.dto.requests.OperationRequest;
import com.task.wallet.dto.responses.OperationResponse;
import com.task.wallet.exceptions.InvalidParameterException;
import com.task.wallet.exceptions.WalletNotFoundException;
import com.task.wallet.repositories.WalletRepository;
import com.task.wallet.services.crud.CRUDService;
import com.task.wallet.services.operations.OperationService;
import com.task.wallet.services.operations.OperationType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApiController.class)
class CreateOperationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private OperationService operationService;
    @MockBean
    private CRUDService crudService;
    @MockBean
    private WalletRepository walletRepository;
    private final String walletIdTest = "56897422-d900-4b5c-9d90-5bd95a65917f";

    @Test
    @DisplayName("Test for creating operation for non-existent wallet's id")
    void whenCreateOperationForNonExistentWalletId_thenReturnWalletNotFoundException() throws Exception {
        String nonExistentWalletId = "00000000-0000-0000-0000-000000000000";
        String errorMessage = "Wallet with id: " + nonExistentWalletId + " is not found";
        
        OperationRequest request = new OperationRequest();
        request.setUserId(nonExistentWalletId);
        request.setAmount(String.valueOf(createRandomAmountOfMoneyForOperation()));
        request.setOperationType(chooseRandomlyOperationType());

        when(operationService.processOperation(any(OperationRequest.class)))
                .thenThrow(new WalletNotFoundException(errorMessage));
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DisplayName("Test for creating operation with invalid parameters")
    void whenCreateOperationWithInvalidParameters_thenReturnInvalidParameterException() throws Exception {
        String errorMessage = "Invalid parameters";

        OperationRequest request = new OperationRequest();
        request.setUserId(walletIdTest);
        request.setAmount(String.valueOf(0000000));
        request.setOperationType("INVALID");

        when(operationService.processOperation(any(OperationRequest.class)))
                .thenThrow(new InvalidParameterException(errorMessage));
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DisplayName("Test for deposit operation")
    void whenDepositOperation_thenReturnOperationResponseWithSuccess() throws Exception {
        OperationRequest request = new OperationRequest();
        request.setUserId(walletIdTest);
        request.setAmount(String.valueOf(createRandomAmountOfMoneyForOperation()));
        request.setOperationType("DEPOSIT");

        OperationResponse response = new OperationResponse();
        response.setSuccess("Successful");
        response.setResult(true);
        response.setOperationType(OperationType.DEPOSIT);

        when(operationService.processOperation(any())).thenReturn(response);
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.operationType").value(String.valueOf(OperationType.DEPOSIT)))
                .andExpect(jsonPath(("$.success")).value("Successful"));
    }

    @Test
    @DisplayName("Test for withdraw operation with enough balance to withdraw")
    void whenWithdrawOperation_thenReturnOperationResponseWithSuccess() throws Exception {
        OperationRequest request = new OperationRequest();
        request.setUserId(walletIdTest);
        request.setAmount(String.valueOf(20000.00));
        request.setOperationType("WITHDRAW");

        OperationResponse response = new OperationResponse();
        response.setResult(true);
        response.setSuccess("Successful");
        response.setOperationType(OperationType.WITHDRAW);

        when(operationService.processOperation(any())).thenReturn(response);
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.success").value("Successful"))
                .andExpect(jsonPath("$.operationType").value(String.valueOf(OperationType.WITHDRAW)));
    }

    @Test
    @DisplayName("Test for withdraw operation with not enough balance to withdraw")
    void whenWithdrawOperation_thenReturnOperationResponseWithNotEnoughAmountForOperation() throws Exception {
        OperationRequest request = new OperationRequest();
        request.setUserId(walletIdTest);
        request.setAmount(String.valueOf(1_000_000_000));
        request.setOperationType("WITHDRAW");

        OperationResponse response = new OperationResponse();
        response.setResult(false);
        response.setSuccess("Unsuccessful");
        response.setError("Balance is not enough for withdraw operation");
        response.setOperationType(OperationType.WITHDRAW);

        when(operationService.processOperation(any())).thenReturn(response);
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.success").value("Unsuccessful"))
                .andExpect(jsonPath("$.error").value("Balance is not enough for withdraw operation"))
                .andExpect(jsonPath("$.operationType").value(String.valueOf(OperationType.WITHDRAW)));
    }

    @Test
    @DisplayName("Test for multiple parallel requests to the same wallet")
    void whenCreateOperationsForOneWalletAtTheSameTime_thenReturnOperationResponseWithoutExceptions() throws Exception {
        BigDecimal balance = BigDecimal.valueOf(183641.14);
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(1000);
        List<Exception> exceptions = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            executorService.submit(() -> {
                try {
                    OperationRequest request = new OperationRequest();
                    request.setUserId(walletIdTest);
                    request.setAmount(String.valueOf(createRandomAmountOfMoneyForOperation()));
                    String operationType = chooseRandomlyOperationType();
                    request.setOperationType(operationType);

                    if (operationType.equals("DEPOSIT")) {
                        OperationResponse response = new OperationResponse();
                        response.setSuccess("Successful");
                        response.setResult(true);
                        response.setOperationType(OperationType.DEPOSIT);

                        when(operationService.processOperation(any())).thenReturn(response);
                        mockMvc.perform(post("/api/v1/wallet")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result").value(true))
                                .andExpect(jsonPath("$.operationType").value(String.valueOf(OperationType.DEPOSIT)))
                                .andExpect(jsonPath(("$.success")).value("Successful"));
                    }

                    if (operationType.equals("WITHDRAW")
                            && balance.compareTo(new BigDecimal(request.getAmount())) >= 0) {
                        OperationResponse response = new OperationResponse();
                        response.setResult(true);
                        response.setSuccess("Successful");
                        response.setOperationType(OperationType.WITHDRAW);

                        when(operationService.processOperation(any())).thenReturn(response);
                        mockMvc.perform(post("/api/v1/wallet")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result").value(true))
                                .andExpect(jsonPath("$.success").value("Successful"))
                                .andExpect(jsonPath("$.operationType").value(String.valueOf(OperationType.WITHDRAW)));
                    }

                    if (operationType.equals("WITHDRAW")
                            && balance.compareTo(new BigDecimal(request.getAmount())) < 0) {
                        OperationResponse response = new OperationResponse();
                        response.setResult(false);
                        response.setSuccess("Unsuccessful");
                        response.setError("Balance is not enough for withdraw operation");
                        response.setOperationType(OperationType.WITHDRAW);

                        when(operationService.processOperation(any())).thenReturn(response);
                        mockMvc.perform(post("/api/v1/wallet")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.result").value(false))
                                .andExpect(jsonPath("$.success").value("Unsuccessful"))
                                .andExpect(jsonPath("$.error").value("Balance is not enough for withdraw operation"))
                                .andExpect(jsonPath("$.operationType").value(String.valueOf(OperationType.WITHDRAW)));
                    }
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();
        Assertions.assertTrue(exceptions.isEmpty(), "The list of exceptions should be empty, but it contains errors");
    }

    private String chooseRandomlyOperationType() {
        return Math.random() < 0.5 ? "DEPOSIT" : "WITHDRAW";
    }

    private double createRandomAmountOfMoneyForOperation() {
        double min = 10000.00;
        double max = 500000.00;
        return min + (max - min) * Math.random();
    }
}