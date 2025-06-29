# 定时任务测试指南

## 功能验证

### 1. 启动应用
```bash
# 启动Spring Boot应用
./mvnw spring-boot:run
```

### 2. 观察启动日志
应用启动后，你应该看到类似以下的日志：
```
2025-01-15 10:00:00.000  INFO 12345 --- [main] c.b.u.s.BlockGenerationScheduler : === 开始执行定时区块生成任务 ===
2025-01-15 10:00:00.001  INFO 12345 --- [main] c.b.u.s.i.BlockServiceImpl : === 开始检查Active交易并生成区块 ===
2025-01-15 10:00:00.002  INFO 12345 --- [main] c.b.u.s.i.BlockServiceImpl : 查询到 0 个Active状态的交易
2025-01-15 10:00:00.003  INFO 12345 --- [main] c.b.u.s.i.BlockServiceImpl : 当前Active交易为空，跳过区块生成
2025-01-15 10:00:00.004  INFO 12345 --- [main] c.b.u.s.i.BlockServiceImpl : === 区块生成检查完成 ===
2025-01-15 10:00:00.005  INFO 12345 --- [main] c.b.u.s.BlockGenerationScheduler : === 定时区块生成任务执行完成 ===
```

### 3. 测试场景

#### 场景1：无Active交易
**预期行为**: 不生成区块
**验证方法**: 
1. 确保数据库中没有状态为 'A' 的交易
2. 观察日志输出：`当前Active交易为空，跳过区块生成`

#### 场景2：有Active交易
**预期行为**: 生成包含所有Active交易的区块
**验证方法**:
1. 创建一些Active状态的交易
2. 观察日志输出：`成功生成区块: BlockNumber=1, BlockHash=xxx, TransactionCount=5`

### 4. 手动触发测试
```bash
# 手动触发区块生成
curl -X POST http://localhost:8194/api/blocks/generate

# 查看最新区块
curl http://localhost:8194/api/blocks/latest

# 查看所有区块
curl http://localhost:8194/api/blocks
```

### 5. 定时验证
- 应用启动30秒后开始第一次执行
- 之后每隔3分钟执行一次
- 可以通过日志时间戳验证执行间隔

## 关键检查点

### ✅ 定时任务配置
- `@EnableScheduling` 已启用
- `@Scheduled(initialDelay = 30000, fixedRate = 180000)` 配置正确

### ✅ 条件判断逻辑
```java
if (activeTransactionCount == 0 || activeTransactions.isEmpty()) {
    log.info("当前Active交易为空，跳过区块生成");
    return;
}
```

### ✅ 数据库查询
```sql
-- 查询Active状态的交易
SELECT t FROM Transaction t WHERE t.status = 'A' AND t.blockId IS NULL ORDER BY t.createdAt ASC

-- 统计Active状态的交易数量
SELECT COUNT(t) FROM Transaction t WHERE t.status = 'A' AND t.blockId IS NULL
```

### ✅ 事务安全
- 使用 `@Transactional` 确保数据一致性
- 区块生成和交易更新在同一个事务中

## 常见问题排查

### 1. 定时任务不执行
- 检查 `SchedulingConfig` 是否正确配置
- 确认应用启动日志中是否有定时任务相关输出

### 2. 区块生成失败
- 检查数据库连接
- 查看详细的错误日志
- 确认交易数据格式正确

### 3. 重复执行
- 确保只有一个定时任务方法
- 检查是否有多个调度器实例

## 性能监控

### 日志监控
- 定时任务执行时间
- 交易数量统计
- 区块生成成功率

### 数据库监控
- 区块表增长情况
- 交易状态变化
- 查询性能

## 扩展建议

1. **添加监控指标**: 使用Micrometer记录定时任务执行情况
2. **配置外部化**: 将3分钟间隔配置到配置文件中
3. **健康检查**: 添加定时任务健康检查端点
4. **告警机制**: 当区块生成失败时发送告警 