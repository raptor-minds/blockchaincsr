package com.blockchain.utility.scheduler;

import com.blockchain.utility.service.BlockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 区块生成定时任务调度器
 * @author zhangrucheng on 2025/1/15
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BlockGenerationScheduler {

    private final BlockService blockService;

    /**
     * 每隔3分钟执行一次区块生成检查
     * 应用启动后延迟30秒开始执行，然后每隔3分钟执行一次
     * 如果当前Active交易为空，则不生成区块
     */
    @Scheduled(initialDelay = 30000, fixedRate = 180000) // 30秒后开始，每3分钟执行
    public void scheduleBlockGeneration() {
        try {
            log.info("=== 开始执行定时区块生成任务 ===");
            blockService.checkAndGenerateBlock();
            log.info("=== 定时区块生成任务执行完成 ===");
        } catch (Exception e) {
            log.error("定时区块生成任务执行失败", e);
        }
    }
} 