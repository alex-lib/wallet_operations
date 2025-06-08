package com.task.wallet.controllers;
import com.task.wallet.dto.WalletDto;
import com.task.wallet.dto.requests.OperationRequest;
import com.task.wallet.dto.responses.OperationResponse;
import com.task.wallet.services.crud.CRUDService;
import com.task.wallet.services.operations.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class ApiController {
    private final OperationService operationService;
    private final CRUDService crudService;

    @PostMapping("/wallet")
    public ResponseEntity<OperationResponse> createOperation(@RequestBody OperationRequest operationRequest) {
        return ResponseEntity.ok(operationService.processOperation(operationRequest));
    }

    @GetMapping("/wallet/{walletUuid}")
    public ResponseEntity<WalletDto> getDataWallet(@PathVariable String walletUuid) {
        return ResponseEntity.ok(crudService.getDataWallet(walletUuid));
    }
}