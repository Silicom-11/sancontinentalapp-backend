package com.iepca.app.model.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount {
    private String bankName;
    private String accountNumber;
    private String cci;
    private String accountType; // ahorros, corriente
}

