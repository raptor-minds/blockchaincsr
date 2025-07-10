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

# 日志函数
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
    if ! command -v mvn &> /dev/null; then
        log_error "Maven未安装"
        exit 1
    fi
    log_info "Java环境检查通过"
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
    mvn clean package -DskipTests -P$PROFILE
    log_info "构建完成"
}

# 复制JAR文件
copy_jar() {
    log_step "复制JAR文件..."
    if [ -f "$TARGET_DIR/$JAR_NAME" ]; then
        cp "$TARGET_DIR/$JAR_NAME" .
        log_info "JAR文件复制完成"
    else
        log_error "JAR文件不存在"
        exit 1
    fi
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

# 主流程
main() {
    log_info "开始部署 $PROJECT_NAME v$VERSION"
    check_java
    create_directories
    stop_app
    backup_current
    build_project
    copy_jar
    start_app
    check_status
    log_info "部署完成！"
}

# 显示帮助
show_help() {
    echo "用法: $0 [选项]"
    echo "选项:"
    echo "  -h, --help     显示帮助"
    echo "  -s, --stop     仅停止应用"
    echo "  -r, --restart  重启应用"
    echo "  -p, --port     指定端口 (默认: 8194)"
}

# 处理参数
case "${1:-}" in
    -h|--help) show_help; exit 0;;
    -s|--stop) stop_app; log_info "应用已停止"; exit 0;;
    -r|--restart) stop_app; sleep 2; start_app; check_status; exit 0;;
    -p|--port) PORT="$2"; shift 2;;
esac

main 