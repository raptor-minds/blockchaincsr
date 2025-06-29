package com.blockchain.utility.service;

import com.blockchain.utility.model.entity.Block;

import java.util.List;

/**
 * 区块服务接口
 */
public interface BlockService {
    
    /**
     * 生成新区块
     * @param transactions 要打包的交易列表
     * @return 生成的区块
     */
    Block generateBlock(List<com.blockchain.utility.model.entity.Transaction> transactions);
    
    /**
     * 获取最新区块
     * @return 最新区块
     */
    Block getLatestBlock();
    
    /**
     * 根据区块号获取区块
     * @param blockNumber 区块号
     * @return 区块
     */
    Block getBlockByNumber(Long blockNumber);
    
    /**
     * 获取所有区块
     * @return 区块列表
     */
    List<Block> getAllBlocks();
    
    /**
     * 定时任务：检查并生成区块
     */
    void checkAndGenerateBlock();
} 