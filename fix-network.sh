#!/bin/bash

# Maven网络问题修复脚本
set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

log_info() { echo -e "${GREEN}[INFO]${NC} $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step() { echo -e "${BLUE}[STEP]${NC} $1"; }

# 检查网络连接
check_network() {
    log_step "检查网络连接..."
    
    # 测试阿里云镜像
    if curl -s --connect-timeout 10 https://maven.aliyun.com/repository/public > /dev/null; then
        log_info "阿里云镜像连接正常"
        return 0
    else
        log_warn "阿里云镜像连接失败"
    fi
    
    # 测试华为云镜像
    if curl -s --connect-timeout 10 https://mirrors.huaweicloud.com/repository/maven/ > /dev/null; then
        log_info "华为云镜像连接正常"
        return 0
    else
        log_warn "华为云镜像连接失败"
    fi
    
    # 测试中央仓库
    if curl -s --connect-timeout 10 https://repo1.maven.org/maven2/ > /dev/null; then
        log_info "中央仓库连接正常"
        return 0
    else
        log_warn "中央仓库连接失败"
    fi
    
    log_error "所有Maven仓库连接失败"
    return 1
}

# 清理Maven缓存
clean_maven_cache() {
    log_step "清理Maven缓存..."
    
    if [ -d "$HOME/.m2/repository" ]; then
        rm -rf "$HOME/.m2/repository/org/apache/maven/plugins/maven-surefire-plugin"
        rm -rf "$HOME/.m2/repository/org/apache/maven/plugins/maven-clean-plugin"
        rm -rf "$HOME/.m2/repository/org/apache/maven/plugins/maven-compiler-plugin"
        log_info "Maven插件缓存清理完成"
    fi
    
    if [ -d "target" ]; then
        rm -rf target
        log_info "项目target目录清理完成"
    fi
}

# 配置Maven设置
configure_maven_settings() {
    log_step "配置Maven设置..."
    
    # 检查是否存在settings.xml
    if [ -f "settings.xml" ]; then
        log_info "发现项目settings.xml文件"
        
        # 备份用户设置
        if [ -f "$HOME/.m2/settings.xml" ]; then
            cp "$HOME/.m2/settings.xml" "$HOME/.m2/settings.xml.backup"
            log_info "用户设置已备份"
        fi
        
        # 复制项目设置
        cp settings.xml "$HOME/.m2/settings.xml"
        log_info "项目设置已应用到用户目录"
    else
        log_warn "未发现项目settings.xml文件"
    fi
}

# 测试Maven构建
test_maven_build() {
    log_step "测试Maven构建..."
    
    # 使用Maven Wrapper或系统Maven
    if [ -f "./mvnw" ]; then
        MVN_CMD="./mvnw"
        log_info "使用Maven Wrapper"
    elif command -v mvn &> /dev/null; then
        MVN_CMD="mvn"
        log_info "使用系统Maven"
    else
        log_error "未找到Maven"
        return 1
    fi
    
    # 测试编译
    log_info "测试编译..."
    if $MVN_CMD clean compile -q; then
        log_info "编译测试成功"
        return 0
    else
        log_error "编译测试失败"
        return 1
    fi
}

# 显示解决方案
show_solutions() {
    echo ""
    log_step "网络问题解决方案"
    echo "=========================================="
    echo "1. 使用代理（如果有）:"
    echo "   export MAVEN_OPTS='-DproxySet=true -DproxyHost=proxy.company.com -DproxyPort=8080'"
    echo ""
    echo "2. 跳过SSL验证（不推荐）:"
    echo "   export MAVEN_OPTS='-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true'"
    echo ""
    echo "3. 使用离线模式（如果之前下载过依赖）:"
    echo "   mvn clean package -o"
    echo ""
    echo "4. 手动下载依赖:"
    echo "   mvn dependency:resolve -Dclassifier=sources"
    echo ""
    echo "5. 使用不同的镜像源:"
    echo "   编辑 ~/.m2/settings.xml 文件"
    echo "=========================================="
}

# 主函数
main() {
    log_info "Maven网络问题修复"
    echo "=========================================="
    
    # 检查网络连接
    if ! check_network; then
        log_error "网络连接问题，请检查网络设置"
        show_solutions
        exit 1
    fi
    
    # 清理缓存
    clean_maven_cache
    
    # 配置设置
    configure_maven_settings
    
    # 测试构建
    if test_maven_build; then
        log_info "✅ 网络问题修复成功！"
        echo ""
        log_info "现在可以正常构建项目:"
        if [ -f "./mvnw" ]; then
            echo "  ./mvnw clean package -DskipTests"
        else
            echo "  mvn clean package -DskipTests"
        fi
    else
        log_error "❌ 构建测试失败"
        show_solutions
        exit 1
    fi
}

# 显示帮助
show_help() {
    echo "Maven网络问题修复脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help     显示帮助"
    echo "  -c, --clean    仅清理缓存"
    echo "  -t, --test     仅测试构建"
    echo "  -n, --network  仅检查网络"
    echo ""
    echo "功能:"
    echo "  1. 检查网络连接"
    echo "  2. 清理Maven缓存"
    echo "  3. 配置Maven设置"
    echo "  4. 测试构建功能"
}

# 处理参数
case "${1:-}" in
    -h|--help) show_help; exit 0;;
    -c|--clean) clean_maven_cache; log_info "缓存清理完成"; exit 0;;
    -t|--test) test_maven_build; exit 0;;
    -n|--network) check_network; exit 0;;
esac

main 