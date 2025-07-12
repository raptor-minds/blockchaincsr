#!/bin/bash

# 区块链项目部署脚本
set -e

# 配置变量
PROJECT_NAME="blockchain-utility"
APP_NAME="utility"
VERSION="1.0.0"
JAR_NAME="${APP_NAME}-${VERSION}.jar"
TARGET_DIR="target"
BACKUP_DIR="backups"
LOG_DIR="logs"
PID_FILE="app.pid"
PORT=8194
JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"
PROFILE="prod"

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

# 检查Java环境
check_java() {
    log_step "检查Java环境..."
    if ! command -v java &> /dev/null; then
        log_error "Java未安装"
        exit 1
    fi
    log_info "Java环境检查通过"
}

# 检查Maven环境
check_maven() {
    log_step "检查Maven环境..."
    
    # 首先检查Maven Wrapper
    if [ -f "./mvnw" ]; then
        chmod +x ./mvnw
        MVN_CMD="./mvnw"
        log_info "使用Maven Wrapper"
        return 0
    fi
    
    # 检查系统Maven
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version 2>&1 | grep "Apache Maven" | cut -d' ' -f3)
        log_info "系统Maven版本: $MVN_VERSION"
        
        # 检查版本是否满足要求
        if [[ "$MVN_VERSION" == 3.6.3* ]] || [[ "$MVN_VERSION" == 3.7* ]] || [[ "$MVN_VERSION" == 3.8* ]] || [[ "$MVN_VERSION" == 3.9* ]]; then
            MVN_CMD="mvn"
            log_info "Maven版本满足要求"
            return 0
        else
            log_warn "Maven版本过低，尝试创建Maven Wrapper..."
            if mvn wrapper:wrapper -Dmaven=3.9.6; then
                chmod +x ./mvnw
                MVN_CMD="./mvnw"
                log_info "Maven Wrapper创建成功"
                return 0
            else
                log_error "无法创建Maven Wrapper，请升级Maven到3.6.3+"
                exit 1
            fi
        fi
    else
        log_error "Maven未安装且没有Maven Wrapper"
        exit 1
    fi
}

# 创建目录
create_directories() {
    log_step "创建目录..."
    mkdir -p $BACKUP_DIR $LOG_DIR
}

# 停止应用
stop_app() {
    log_step "停止应用..."
    if [ -f $PID_FILE ]; then
        PID=$(cat $PID_FILE)
        if ps -p $PID > /dev/null 2>&1; then
            kill $PID
            sleep 5
            if ps -p $PID > /dev/null 2>&1; then
                kill -9 $PID
            fi
        fi
        rm -f $PID_FILE
    fi
}

# 备份当前版本
backup_current() {
    log_step "备份当前版本..."
    if [ -f $JAR_NAME ]; then
        BACKUP_NAME="${BACKUP_DIR}/${APP_NAME}-$(date +%Y%m%d_%H%M%S).jar"
        cp $JAR_NAME $BACKUP_NAME
        log_info "备份完成: $BACKUP_NAME"
    fi
}

# 构建项目
build_project() {
    log_step "构建项目..."
    $MVN_CMD clean package -DskipTests -P$PROFILE
    log_info "构建完成"
}

# 复制JAR文件
copy_jar() {
    log_step "复制JAR文件..."
    cp target/utility-*.jar .
}

# 启动应用
start_app() {
    log_step "启动应用..."
    JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=$PROFILE -Dserver.port=$PORT"
    nohup java $JAVA_OPTS -jar $JAR_NAME > $LOG_DIR/startup.log 2>&1 &
    echo $! > $PID_FILE
    log_info "应用启动中，PID: $(cat $PID_FILE)"
}

# 检查状态
check_status() {
    log_step "检查应用状态..."
    sleep 10
    if [ -f $PID_FILE ] && ps -p $(cat $PID_FILE) > /dev/null 2>&1; then
        log_info "应用运行正常"
        log_info "访问地址: http://localhost:$PORT"
        log_info "Swagger文档: http://localhost:$PORT/swagger-ui.html"
    else
        log_error "应用启动失败"
        exit 1
    fi
}

# 显示帮助
show_help() {
    echo "用法: $0 [选项]"
    echo "选项:"
    echo "  -h, --help     显示帮助"
    echo "  -s, --stop     仅停止应用"
    echo "  -r, --restart  重启应用"
    echo "  -p, --port     指定端口 (默认: 8194)"
    echo "  -f, --profile  指定环境 (默认: prod)"
    echo "  -c, --check    检查环境"
}

# 检查环境
check_environment() {
    log_info "环境检查"
    check_java
    check_maven
    log_info "环境检查完成"
}

# 主流程
main() {
    log_info "开始部署 $PROJECT_NAME v$VERSION"
    check_java
    check_maven
    create_directories
    stop_app
    backup_current
    copy_jar
    start_app
    check_status
    log_info "部署完成！"
}

# 处理参数
case "${1:-}" in
    -h|--help) show_help; exit 0;;
    -s|--stop) stop_app; log_info "应用已停止"; exit 0;;
    -r|--restart) stop_app; sleep 2; start_app; check_status; exit 0;;
    -p|--port) PORT="$2"; shift 2;;
    -f|--profile) PROFILE="$2"; shift 2;;
    -c|--check) check_environment; exit 0;;
esac

main 