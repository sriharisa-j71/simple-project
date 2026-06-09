#!/usr/bin/env bash
set -euo pipefail

RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m'

missing=0

check_cmd() {
    if command -v "$1" &>/dev/null; then
        echo -e "${GREEN}✓${NC} $1 found: $(command -v "$1")"
    else
        echo -e "${RED}✗${NC} $1 not found"
        missing=1
    fi
}

check_version() {
    local cmd=$1
    local flag=${2:---version}
    if command -v "$cmd" &>/dev/null; then
        echo "  $($cmd $flag 2>&1 | head -1)"
    fi
}

echo "=== Checking required dependencies ==="
echo ""

echo "--- Container Runtime ---"
check_cmd docker
check_version docker

echo ""
echo "--- Infrastructure as Code ---"
check_cmd tofu
check_version tofu

echo ""
echo "--- Build Tools ---"
check_cmd mvn
check_version mvn
check_cmd go
check_version go

echo ""
echo "--- AWS CLI (optional) ---"
if command -v aws &>/dev/null; then
    echo -e "${GREEN}✓${NC} aws found"
else
    echo -e "${GREEN}ℹ${NC} aws cli not installed (optional for debugging)"
fi

echo ""
if [ "$missing" -eq 1 ]; then
    echo -e "${RED}Some dependencies are missing. Please install them before proceeding.${NC}"
    exit 1
else
    echo -e "${GREEN}All required dependencies are installed.${NC}"
fi
