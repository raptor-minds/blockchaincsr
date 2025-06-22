package com.blockchain.utility.model.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * @author zhangrucheng on 2025/6/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UpdateTransactionRequest {

    @NotNull
    private UUID transactionId;

    private String sender;

    private String recipient;

    private String endorser;

    private Object transaction;
} 