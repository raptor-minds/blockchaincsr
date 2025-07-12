# Maven版本问题解决方案

## 问题描述

您遇到的错误：
```
Failed to execute goal org.apache.maven.plugins:maven-clean-plugin:3.4.1:clean (default-clean) on project utility: The plugin org.apache.maven.plugins:maven-clean-plugin:3.4.1 requires Maven version 3.6.3
```

这表明您的Maven版本过低，需要升级到3.6.3或更高版本。

## 快速解决方案

### 方案1：使用Maven Wrapper（推荐）

运行修复脚本：
```bash
chmod +x fix-maven.sh
./fix-maven.sh
```

这个脚本会：
1. 检查当前Maven版本
2. 自动创建Maven Wrapper
3. 测试构建功能
4. 显示使用说明

### 方案2：手动创建Maven Wrapper

如果您有可用的Maven（即使版本较低），可以手动创建Maven Wrapper：

```bash
# 创建Maven Wrapper
mvn wrapper:wrapper -Dmaven=3.9.6

# 设置执行权限
chmod +x ./mvnw
```

### 方案3：升级系统Maven

#### Ubuntu/Debian
```bash
sudo apt update
sudo apt install maven
```

#### CentOS/RHEL
```bash
sudo yum install maven
```

#### macOS
```bash
brew install maven
```

#### 手动安装
```bash
# 下载Maven 3.9.6
wget https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz

# 解压
tar -xzf apache-maven-3.9.6-bin.tar.gz

# 移动到/opt目录
sudo mv apache-maven-3.9.6 /opt/

# 添加到PATH
echo 'export PATH=/opt/apache-maven-3.9.6/bin:$PATH' >> ~/.bashrc
source ~/.bashrc
```

## 使用Maven Wrapper

创建Maven Wrapper后，您可以使用以下命令：

### 构建项目
```bash
./mvnw clean package -DskipTests
```

### 运行应用
```bash
./mvnw spring-boot:run
```

### 运行测试
```bash
./mvnw test
```

### 清理项目
```bash
./mvnw clean
```

## 使用部署脚本

修复Maven问题后，您可以使用部署脚本：

```bash
# 检查环境
./deploy.sh -c

# 完整部署
./deploy.sh

# 指定端口部署
./deploy.sh -p 8080

# 指定环境部署
./deploy.sh -f dev

# 仅停止应用
./deploy.sh -s

# 重启应用
./deploy.sh -r
```

## 项目配置说明

### pom.xml修改

我们已经在`pom.xml`中添加了以下配置来降低Maven版本要求：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-clean-plugin</artifactId>
    <version>3.2.0</version>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <source>20</source>
        <target>20</target>
        <encoding>UTF-8</encoding>
    </configuration>
</plugin>
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.1.2</version>
</plugin>
```

### 版本要求

- **Java**: 20+
- **Maven**: 3.6.3+ (或使用Maven Wrapper)
- **Spring Boot**: 3.5.0

## 验证修复

运行以下命令验证修复是否成功：

```bash
# 检查Maven版本
./check-maven.sh

# 测试构建
./fix-maven.sh -t

# 完整构建测试
./mvnw clean compile
```

## 常见问题

### Q: Maven Wrapper创建失败
A: 确保您有可用的Maven（即使版本较低），或者手动下载Maven Wrapper文件。

### Q: 构建仍然失败
A: 检查Java版本是否为20+，确保所有依赖都能正常下载。

### Q: 权限问题
A: 确保脚本有执行权限：
```bash
chmod +x *.sh
chmod +x ./mvnw
```

## 推荐工作流程

1. **首次设置**：
   ```bash
   ./fix-maven.sh
   ```

2. **日常开发**：
   ```bash
   ./mvnw clean compile
   ./mvnw spring-boot:run
   ```

3. **部署**：
   ```bash
   ./deploy.sh
   ```

4. **环境检查**：
   ```bash
   ./deploy.sh -c
   ```

## 总结

使用Maven Wrapper是最推荐的解决方案，因为：
- 不依赖系统Maven版本
- 确保团队成员使用相同的Maven版本
- 简化CI/CD流程
- 避免版本冲突问题 