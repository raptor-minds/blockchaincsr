package com.blockchain.utility.blockchain;

import lombok.Getter;
import lombok.ToString;

/**
 * @author zhangrucheng on 2025/5/25
 */
@Getter
@ToString
public class Transaction<T> {

    private final String sender;
    private final String recipient;
    private final T detail;

    public Transaction(String sender, String recipient, T detail) {
        this.sender = sender;
        this.recipient = recipient;
        this.detail = detail;
    }
}
