#!/usr/bin/env bash
set -euo pipefail

JAVA_HOME="${JAVA_HOME:-/home/rudra/.sdkman/candidates/java/21.0.2-graalce}"
FITNESSE_JAR="/home/rudra/study/simple-project/fitnesse-tests/target/fitnesse-tests-1.0.0.jar"
WIKI_DIR="/home/rudra/study/simple-project/fitnesse-wiki"
PORT="${PORT:-9090}"
LOG="/tmp/fitnesse-${PORT}.log"

case "${1:-}" in
    start)
        export AWS_DEFAULT_REGION=us-east-1
        export AWS_REGION=us-east-1
        export AWS_ENDPOINT_URL=http://localhost:4566
        export AWS_ACCESS_KEY_ID=test
        export AWS_SECRET_ACCESS_KEY=test
        export JTE_TEMPLATES=/home/rudra/study/simple-project/common/src/main/jte
        nohup "$JAVA_HOME/bin/java" -jar "$FITNESSE_JAR" -p "$PORT" -d "$WIKI_DIR" &>"$LOG" &
        echo $! > /tmp/fitnesse-${PORT}.pid
        echo "FitNesse started on port $PORT (PID $(cat /tmp/fitnesse-${PORT}.pid))"
        ;;
    stop)
        if [ -f /tmp/fitnesse-${PORT}.pid ]; then
            kill "$(cat /tmp/fitnesse-${PORT}.pid)" 2>/dev/null || true
            rm -f /tmp/fitnesse-${PORT}.pid
        fi
        pkill -f "FitNesseMain.*$PORT" 2>/dev/null || true
        echo "FitNesse stopped"
        ;;
    status)
        if pgrep -f "FitNesseMain.*$PORT" >/dev/null 2>&1; then
            echo "FitNesse running on port $PORT (PID $(pgrep -f "FitNesseMain.*$PORT"))"
        else
            echo "FitNesse not running"
        fi
        ;;
    restart)
        "$0" stop
        sleep 2
        "$0" start
        ;;
    *)
        echo "Usage: $0 {start|stop|status|restart}"
        exit 1
        ;;
esac
