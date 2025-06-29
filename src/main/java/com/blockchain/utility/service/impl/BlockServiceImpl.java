package com.blockchain.utility.service.impl;

import com.blockchain.utility.model.entity.Block;
import com.blockchain.utility.model.entity.Transaction;
import com.blockchain.utility.repository.BlockRepository;
import com.blockchain.utility.repository.TransactionRepository;
import com.blockchain.utility.service.BlockService;
import com.blockchain.utility.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 区块服务实现类
 * @author zhangrucheng on 2025/1/15
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

    private final BlockRepository blockRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public Block generateBlock(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            log.warn("No transactions provided for block generation");
            return null;
        }

        // 获取最新区块号
        Long nextBlockNumber = getNextBlockNumber();
        
        // 获取父区块哈希
        String parentHash = getParentHash();
        
        // 生成区块哈希
        String blockHash = calculateBlockHash(transactions, nextBlockNumber, parentHash);
        
        // 创建新区块
        Block newBlock = new Block();
        newBlock.setBlockNumber(nextBlockNumber);
        newBlock.setBlockHash(blockHash);
        newBlock.setParentHash(parentHash);
        newBlock.setTimestamp(LocalDateTime.now());
        newBlock.setSize((long) transactions.size());
        newBlock.setNonce(generateNonce());
        
        // 保存区块
        Block savedBlock = blockRepository.save(newBlock);
        log.info("Generated new block: BlockNumber={}, BlockHash={}, TransactionCount={}", 
                savedBlock.getBlockNumber(), savedBlock.getBlockHash(), transactions.size());
        
        // 更新交易的区块信息
        updateTransactionsBlockInfo(transactions, savedBlock.getId(), savedBlock.getBlockNumber());
        
        return savedBlock;
    }

    @Override
    public Block getLatestBlock() {
        Optional<Long> maxBlockNumber = blockRepository.findMaxBlockNumber();
        if (maxBlockNumber.isEmpty()) {
            return null;
        }
        return blockRepository.findByBlockNumber(maxBlockNumber.get()).orElse(null);
    }

    @Override
    public Block getBlockByNumber(Long blockNumber) {
        return blockRepository.findByBlockNumber(blockNumber).orElse(null);
    }

    @Override
    public List<Block> getAllBlocks() {
        return blockRepository.findAll();
    }

    @Override
    @Transactional
    public void checkAndGenerateBlock() {
        log.info("=== 开始检查Active交易并生成区块 ===");
        
        // 获取所有Active状态的交易
        List<Transaction> activeTransactions = transactionRepository.findActiveTransactionsForBlocking();
        Long activeTransactionCount = transactionRepository.countActiveTransactionsForBlocking();
        
        log.info("查询到 {} 个Active状态的交易", activeTransactionCount);
        
        // 判断条件：如果Active交易为空，则不生成区块
        if (activeTransactionCount == 0 || activeTransactions.isEmpty()) {
            log.info("当前Active交易为空，跳过区块生成");
            return;
        }
        
        log.info("开始生成包含 {} 个交易的区块", activeTransactionCount);
        
        // 生成新区块
        Block newBlock = generateBlock(activeTransactions);
        
        if (newBlock != null) {
            log.info("✅ 成功生成区块: BlockNumber={}, BlockHash={}, TransactionCount={}", 
                    newBlock.getBlockNumber(), newBlock.getBlockHash(), activeTransactionCount);
        } else {
            log.error("❌ 区块生成失败，交易数量: {}", activeTransactionCount);
        }
        
        log.info("=== 区块生成检查完成 ===");
    }

    /**
     * 获取下一个区块号
     */
    private Long getNextBlockNumber() {
        Optional<Long> maxBlockNumber = blockRepository.findMaxBlockNumber();
        return maxBlockNumber.map(number -> number + 1).orElse(1L);
    }

    /**
     * 获取父区块哈希
     */
    private String getParentHash() {
        Block latestBlock = getLatestBlock();
        return latestBlock != null ? latestBlock.getBlockHash() : "0000000000000000000000000000000000000000000000000000000000000000";
    }

    /**
     * 计算区块哈希
     */
    private String calculateBlockHash(List<Transaction> transactions, Long blockNumber, String parentHash) {
        StringBuilder data = new StringBuilder();
        data.append(blockNumber).append(parentHash);
        
        for (Transaction transaction : transactions) {
            data.append(transaction.getTxHash());
        }
        
        data.append(LocalDateTime.now().toString());
        
        return StringUtil.applySha256(data.toString());
    }

    /**
     * 生成随机数
     */
    private String generateNonce() {
        return UUID.randomUUID().toString().substring(0, 18);
    }

    /**
     * 更新交易的区块信息
     */
    private void updateTransactionsBlockInfo(List<Transaction> transactions, Long blockId, Long blockNumber) {
        for (Transaction transaction : transactions) {
            transaction.setBlockId(blockId);
            transaction.setBlockNumber(blockNumber);
            transactionRepository.save(transaction);
        }
        log.info("Updated {} transactions with block info: BlockId={}, BlockNumber={}", 
                transactions.size(), blockId, blockNumber);
    }
} 