package com.task.wallet.services.operations;
import com.task.wallet.dto.requests.OperationRequest;
import com.task.wallet.dto.responses.OperationResponse;

public interface OperationService {
OperationResponse processOperation(OperationRequest operationRequest);
}