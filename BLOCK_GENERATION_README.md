# 区块生成定时任务

## 功能概述

本项目实现了一个自动区块生成系统，每隔3分钟检查所有Active状态的交易，并将它们打包成新区块存储到数据库中。

## 核心组件

### 1. 定时任务调度器 (`BlockGenerationScheduler`)
- **位置**: `src/main/java/com/blockchain/utility/scheduler/BlockGenerationScheduler.java`
- **功能**: 每隔3分钟自动执行区块生成检查
- **配置**: 
  - 固定延迟模式: `@Scheduled(fixedDelay = 180000)` - 每次执行完成后等待3分钟
  - 固定频率模式: `@Scheduled(initialDelay = 30000, fixedRate = 180000)` - 应用启动30秒后开始，每3分钟执行

### 2. 区块服务 (`BlockService`)
- **接口**: `src/main/java/com/blockchain/utility/service/BlockService.java`
- **实现**: `src/main/java/com/blockchain/utility/service/impl/BlockServiceImpl.java`
- **功能**: 
  - 生成新区块
  - 获取最新区块
  - 检查并生成区块的定时任务逻辑

### 3. 数据访问层
- **BlockRepository**: 区块数据访问
- **TransactionRepository**: 交易数据访问，包含查询Active状态交易的方法

### 4. REST API (`BlockController`)
- **位置**: `src/main/java/com/blockchain/utility/controller/BlockController.java`
- **端点**:
  - `GET /api/blocks/latest` - 获取最新区块
  - `GET /api/blocks/{blockNumber}` - 根据区块号获取区块
  - `GET /api/blocks` - 获取所有区块
  - `POST /api/blocks/generate` - 手动触发区块生成

## 工作流程

1. **定时检查**: 每3分钟自动执行 `checkAndGenerateBlock()` 方法
2. **查询交易**: 查找所有状态为 'A' (Active) 且未分配到区块的交易
3. **判断条件**: 
   - 如果有Active交易 → 生成新区块
   - 如果没有Active交易 → 跳过本次生成
4. **区块生成**: 
   - 计算新区块号
   - 获取父区块哈希
   - 生成区块哈希
   - 创建新区块实体
   - 保存到数据库
5. **更新交易**: 将相关交易的 `blockId` 和 `blockNumber` 更新为新区块信息

## 配置说明

### 应用配置 (`application.properties`)
```properties
# 定时任务线程池配置
spring.task.scheduling.pool.size=5
spring.task.scheduling.thread-name-prefix=scheduled-task-

# JPA配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 定时任务配置 (`SchedulingConfig`)
```java
@Configuration
@EnableScheduling
public class SchedulingConfig {
    // 启用Spring定时任务功能
}
```

## 数据库表结构

### Block表
- `id`: 主键
- `block_hash`: 区块哈希 (64字符，唯一)
- `block_number`: 区块号
- `parent_hash`: 父区块哈希
- `timestamp`: 区块时间戳
- `size`: 区块大小 (交易数量)
- `nonce`: 随机数
- `created_at`: 创建时间
- `updated_at`: 更新时间

### Transaction表
- `id`: 主键
- `tx_hash`: 交易哈希
- `block_id`: 所属区块ID (外键)
- `block_number`: 所属区块号
- `from_address`: 发送方地址
- `to_address`: 接收方地址
- `uuid`: 交易UUID
- `status`: 交易状态 ('A'=Active, 'D'=Deactive)
- `created_at`: 创建时间

## 使用方法

### 1. 启动应用
应用启动后，定时任务会自动开始工作。

### 2. 查看日志
定时任务执行时会输出详细日志：
```
=== 开始执行定时区块生成任务 ===
Found 5 active transactions for blocking
Generated new block: BlockNumber=1, BlockHash=abc123..., TransactionCount=5
Updated 5 transactions with block info: BlockId=1, BlockNumber=1
=== 定时区块生成任务执行完成 ===
```

### 3. 手动触发
可以通过API手动触发区块生成：
```bash
curl -X POST http://localhost:8194/api/blocks/generate
```

### 4. 查看区块
```bash
# 获取最新区块
curl http://localhost:8194/api/blocks/latest

# 获取所有区块
curl http://localhost:8194/api/blocks

# 获取特定区块
curl http://localhost:8194/api/blocks/1
```

## 注意事项

1. **交易状态**: 只有状态为 'A' (Active) 的交易才会被打包
2. **区块哈希**: 基于交易哈希、区块号、父区块哈希和时间戳计算
3. **事务安全**: 使用 `@Transactional` 确保数据一致性
4. **错误处理**: 定时任务包含异常处理，不会因为单次失败而停止
5. **性能考虑**: 大量交易时可能需要调整定时任务频率

## 扩展功能

- 可以添加区块验证逻辑
- 可以实现区块难度调整
- 可以添加区块奖励机制
- 可以实现分叉处理逻辑 