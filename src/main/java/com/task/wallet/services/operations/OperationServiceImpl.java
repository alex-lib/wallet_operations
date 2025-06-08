package com.task.wallet.services.operations;
import com.task.wallet.dto.requests.OperationRequest;
import com.task.wallet.dto.responses.OperationResponse;
import com.task.wallet.entities.Wallet;
import com.task.wallet.exceptions.InvalidParameterException;
import com.task.wallet.exceptions.WalletNotFoundException;
import com.task.wallet.repositories.WalletRepository;
import com.task.wallet.services.ValidatorParameters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperationServiceImpl implements OperationService {
    private final WalletRepository walletRepository;
    private final ExecutorService executorService = new ThreadPoolExecutor(
            10,
            100,
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadPoolExecutor.CallerRunsPolicy());

    @Override
    public OperationResponse processOperation(OperationRequest operationRequest) {
        try {
            Future<OperationResponse> future = executorService.submit(() -> processSingleOperation(operationRequest));
            return future.get();
        } catch (InterruptedException e) {
            log.error("Operation was interrupted");
            throw new RuntimeException();
        } catch (ExecutionException e) {
            log.error("Unexpected error during operation processing: {}", e.getMessage());
            if (e.getCause() instanceof WalletNotFoundException) {
                throw (WalletNotFoundException) e.getCause();
            }
            if (e.getCause() instanceof InvalidParameterException) {
                throw (InvalidParameterException) e.getCause();
            }
            log.error("Unexpected error during operation processing: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private OperationResponse processSingleOperation(OperationRequest operationRequest) {
        UUID userId = ValidatorParameters.validateUserId(operationRequest.getUserId());
        OperationType operationType = ValidatorParameters.validateOperationType(operationRequest.getOperationType());
        BigDecimal amount = ValidatorParameters.validateAmount(operationRequest.getAmount());
        Wallet wallet = walletRepository.findById(userId);
        if (wallet == null) {
            log.error("Wallet not found for user ID: {}", userId);
            throw new WalletNotFoundException("Wallet with id: " + userId + " is not found");
        }
        synchronized (wallet) {
            return switch (operationType) {
                case DEPOSIT -> deposit(wallet, amount);
                case WITHDRAW -> withdraw(wallet, amount);
            };
        }
    }

    private OperationResponse withdraw(Wallet wallet, BigDecimal amount) {
        if (!checkBalance(wallet.getBalance(), amount)) {
           return buildOperationResponseWhenNotEnoughBalanceForOperation();
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
        log.debug("Withdraw is completed successfully for user's id: {}", wallet.getId());
        return buildSuccessResponse(OperationType.WITHDRAW, amount);
    }

    private boolean checkBalance(BigDecimal balance, BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    private OperationResponse deposit(Wallet wallet, BigDecimal amount) {
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
        log.debug("Deposit is completed successfully for user's id: {}", wallet.getId());
        return buildSuccessResponse(OperationType.DEPOSIT, amount);
    }

    private OperationResponse buildSuccessResponse(OperationType operationType, BigDecimal amount) {
        OperationResponse operationResponse = new OperationResponse();
        operationResponse.setResult(true);
        operationResponse.setSuccess("Successful");
        operationResponse.setOperationType(operationType);
        return operationResponse;
    }

    private OperationResponse buildOperationResponseWhenNotEnoughBalanceForOperation() {
        OperationResponse operationResponse = new OperationResponse();
        operationResponse.setResult(false);
        operationResponse.setError("Balance of wallet is not enough for withdraw operation");
        operationResponse.setSuccess("Unsuccessful");
        return operationResponse;
    }
}