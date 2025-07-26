package com.blockchain.utility.model.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author zhangrucheng on 2025/6/2
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CreateTransactionRequest {

    @NotNull
    private String sender;

    private String recipient;

    @NotNull
    private String endorser;

    private Object transaction;

    @NotNull
    private UUID uuid;

    private LocalDateTime createdTime;
}
