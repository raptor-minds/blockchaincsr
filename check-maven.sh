#!/bin/bash

# Maven版本检查脚本
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

# 检查Maven版本
check_maven_version() {
    log_step "检查Maven版本..."
    
    if ! command -v mvn &> /dev/null; then
        log_error "Maven未安装"
        return 1
    fi
    
    MVN_VERSION=$(mvn -version 2>&1 | grep "Apache Maven" | cut -d' ' -f3)
    log_info "当前Maven版本: $MVN_VERSION"
    
    # 检查版本是否满足要求
    if [[ "$MVN_VERSION" == 3.6.3* ]] || [[ "$MVN_VERSION" == 3.7* ]] || [[ "$MVN_VERSION" == 3.8* ]] || [[ "$MVN_VERSION" == 3.9* ]]; then
        log_info "Maven版本满足要求 (>= 3.6.3)"
        return 0
    else
        log_warn "Maven版本过低，需要 >= 3.6.3"
        return 1
    fi
}

# 检查Maven Wrapper
check_maven_wrapper() {
    log_step "检查Maven Wrapper..."
    
    if [ -f "./mvnw" ]; then
        log_info "发现Maven Wrapper"
        chmod +x ./mvnw
        return 0
    else
        log_warn "未发现Maven Wrapper"
        return 1
    fi
}

# 创建Maven Wrapper
create_maven_wrapper() {
    log_step "创建Maven Wrapper..."
    
    if command -v mvn &> /dev/null; then
        mvn wrapper:wrapper -Dmaven=3.9.6
        chmod +x ./mvnw
        log_info "Maven Wrapper创建完成"
        return 0
    else
        log_error "无法创建Maven Wrapper，Maven未安装"
        return 1
    fi
}

# 显示升级建议
show_upgrade_suggestions() {
    echo ""
    log_step "升级建议"
    echo "=========================================="
    echo "1. 使用Maven Wrapper (推荐):"
    echo "   ./mvnw clean package -DskipTests"
    echo ""
    echo "2. 升级Maven:"
    echo "   # 下载地址: https://maven.apache.org/download.cgi"
    echo "   # 推荐版本: 3.9.6"
    echo ""
    echo "3. 使用包管理器:"
    echo "   # Ubuntu/Debian: sudo apt install maven"
    echo "   # CentOS/RHEL: sudo yum install maven"
    echo "   # macOS: brew install maven"
    echo "=========================================="
}

# 主函数
main() {
    log_info "Maven版本检查"
    
    MVN_OK=false
    WRAPPER_OK=false
    
    if check_maven_version; then
        MVN_OK=true
    fi
    
    if check_maven_wrapper; then
        WRAPPER_OK=true
    fi
    
    echo ""
    if [ "$MVN_OK" = true ]; then
        log_info "Maven版本满足要求，可以直接使用"
        echo "构建命令: mvn clean package -DskipTests"
    elif [ "$WRAPPER_OK" = true ]; then
        log_info "使用Maven Wrapper构建项目"
        echo "构建命令: ./mvnw clean package -DskipTests"
    else
        log_warn "需要升级Maven或创建Maven Wrapper"
        show_upgrade_suggestions
    fi
}

main 