package com.task.wallet.dto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Getter
@Setter
public class WalletDto {
    private String id;
    private BigDecimal balance;
    private String ownerFirstName;
    private String ownerLastName;
    private String error;
}