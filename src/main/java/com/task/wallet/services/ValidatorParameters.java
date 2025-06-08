package com.task.wallet.services;
import com.task.wallet.exceptions.InvalidParameterException;
import com.task.wallet.services.operations.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
public class ValidatorParameters {
    public static UUID validateUserId(String userIdStr) {
        try {
            return UUID.fromString(userIdStr);
        } catch (IllegalArgumentException | NullPointerException | MethodArgumentTypeMismatchException e) {
            log.error("Invalid id's format");
            throw new InvalidParameterException("Invalid id's format");
        }
    }

    public static OperationType validateOperationType(String operationTypeStr) {
        try {
            return OperationType.valueOf(operationTypeStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.error("Invalid operation's type");
            throw new InvalidParameterException("Invalid operation's type");
        }
    }

    public static BigDecimal validateAmount(String amountStr) {
        try {
            return BigDecimal.valueOf(Float.parseFloat(amountStr));
        } catch (IllegalArgumentException | NullPointerException e) {
            log.error("Invalid amount of money's format");
            throw new InvalidParameterException("Invalid amount of money's format");
        }
    }
}