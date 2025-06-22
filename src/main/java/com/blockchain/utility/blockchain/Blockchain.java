package com.blockchain.utility.blockchain;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangrucheng on 2025/5/25
 */
public class Blockchain {
    private final List<Block> chain;
    private int difficulty;
    private final List<Transaction<?>> pendingTransactions;

    public Blockchain(int difficulty) {
        this.chain = new ArrayList<>();
        this.difficulty = difficulty;
        this.pendingTransactions = new ArrayList<>();

        createGenesisBlock();
    }

    private void createGenesisBlock() {
        chain.add(new Block("0", new ArrayList<>()));
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public void addTransaction(Transaction<?> transaction) {
        pendingTransactions.add(transaction);
    }

    public void minePendingTransactions(String miningRewardAccount) {
        Transaction rewardTx = new Transaction(null, miningRewardAccount, 10);

    }
}
