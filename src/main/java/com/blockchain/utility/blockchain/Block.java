package com.blockchain.utility.blockchain;

import com.blockchain.utility.util.StringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * @author zhangrucheng on 2025/5/25
 */
@Slf4j
@Getter
public class Block {
    private String hash;
    private final String previousHash;
    private final List<Transaction<?>> transactions;
    private final long timeStamp;
    private int nonce;

    public Block(String previousHash, List<Transaction<?>> transactions) {
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    private String calculateHash() {
        return StringUtil.applySha256(previousHash +
                timeStamp +
                nonce +
                transactions.toString());
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while(!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash= calculateHash();
        }
        log.info("new blocked generated : " + hash);
    }
}
