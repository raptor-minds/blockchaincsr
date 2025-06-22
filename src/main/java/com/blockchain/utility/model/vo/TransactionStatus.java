package com.blockchain.utility.model.vo;

import lombok.Getter;

/**
 * @author zhangrucheng on 2025/6/22
 */
@Getter
public enum TransactionStatus {

    ACTIVE("A"),
    DE_ACTIVE("D");

    private final String status;

    TransactionStatus(String status) {
        this.status = status;
    }
}
