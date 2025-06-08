package com.task.wallet.dto.responses;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.task.wallet.services.operations.OperationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@NoArgsConstructor
public class OperationResponse {
    private boolean result;
    private String success;
    private String error;
    private OperationType operationType;
}