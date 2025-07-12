#!/bin/bash

# Maven版本问题快速修复脚本
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

# 检查当前Maven版本
check_current_maven() {
    log_step "检查当前Maven版本..."
    
    if ! command -v mvn &> /dev/null; then
        log_error "Maven未安装"
        return 1
    fi
    
    MVN_VERSION=$(mvn -version 2>&1 | grep "Apache Maven" | cut -d' ' -f3)
    log_info "当前Maven版本: $MVN_VERSION"
    
    # 检查版本是否满足要求
    if [[ "$MVN_VERSION" == 3.6.3* ]] || [[ "$MVN_VERSION" == 3.7* ]] || [[ "$MVN_VERSION" == 3.8* ]] || [[ "$MVN_VERSION" == 3.9* ]]; then
        log_info "Maven版本满足要求"
        return 0
    else
        log_warn "Maven版本过低，需要 >= 3.6.3"
        return 1
    fi
}

# 创建Maven Wrapper
create_maven_wrapper() {
    log_step "创建Maven Wrapper..."
    
    if [ -f "./mvnw" ]; then
        log_info "Maven Wrapper已存在"
        chmod +x ./mvnw
        return 0
    fi
    
    if command -v mvn &> /dev/null; then
        log_info "使用当前Maven创建Wrapper..."
        mvn wrapper:wrapper -Dmaven=3.9.6
        chmod +x ./mvnw
        log_info "Maven Wrapper创建成功"
        return 0
    else
        log_error "无法创建Maven Wrapper，Maven未安装"
        return 1
    fi
}

# 测试构建
test_build() {
    log_step "测试构建..."
    
    if [ -f "./mvnw" ]; then
        log_info "使用Maven Wrapper构建..."
        ./mvnw clean compile -q
        log_info "构建测试成功"
        return 0
    elif command -v mvn &> /dev/null; then
        log_info "使用系统Maven构建..."
        mvn clean compile -q
        log_info "构建测试成功"
        return 0
    else
        log_error "无法进行构建测试"
        return 1
    fi
}

# 显示使用说明
show_usage() {
    echo ""
    log_step "使用说明"
    echo "=========================================="
    if [ -f "./mvnw" ]; then
        echo "✅ 使用Maven Wrapper构建项目:"
        echo "   ./mvnw clean package -DskipTests"
        echo "   ./mvnw spring-boot:run"
        echo ""
        echo "✅ 使用部署脚本:"
        echo "   ./deploy.sh"
    else
        echo "❌ 请先运行此脚本修复Maven问题:"
        echo "   ./fix-maven.sh"
    fi
    echo "=========================================="
}

# 主函数
main() {
    log_info "Maven版本问题快速修复"
    echo "=========================================="
    
    # 检查当前Maven版本
    if check_current_maven; then
        log_info "Maven版本满足要求，无需修复"
        show_usage
        exit 0
    fi
    
    # 创建Maven Wrapper
    if create_maven_wrapper; then
        log_info "Maven Wrapper创建成功"
        
        # 测试构建
        if test_build; then
            log_info "✅ 修复完成！"
            show_usage
        else
            log_error "❌ 构建测试失败"
            exit 1
        fi
    else
        log_error "❌ 无法创建Maven Wrapper"
        echo ""
        log_step "手动解决方案"
        echo "=========================================="
        echo "1. 升级Maven到3.6.3+版本"
        echo "2. 或者手动创建Maven Wrapper:"
        echo "   mvn wrapper:wrapper -Dmaven=3.9.6"
        echo "   chmod +x ./mvnw"
        echo "=========================================="
        exit 1
    fi
}

# 显示帮助
show_help() {
    echo "Maven版本问题快速修复脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  -h, --help     显示帮助"
    echo "  -t, --test     仅测试构建"
    echo ""
    echo "功能:"
    echo "  1. 检查Maven版本"
    echo "  2. 创建Maven Wrapper (如果需要)"
    echo "  3. 测试构建功能"
    echo "  4. 显示使用说明"
}

# 处理参数
case "${1:-}" in
    -h|--help) show_help; exit 0;;
    -t|--test) test_build; exit 0;;
esac

main 