package com.task.wallet.dto.requests;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class OperationRequest {
    private String userId;
    private String operationType;
    private String amount;
}