package com.task.wallet.exceptions;
import com.task.wallet.dto.responses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HandlerGlobalException {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleJsonErrors(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Malformed JSON request"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleWalletNotFound(WalletNotFoundException ex) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInvalidParameterException(InvalidParameterException ex) {
        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}