#!/usr/bin/env bash
# stop-dev.sh — Stop all development services
# Usage: bash scripts/stop-dev.sh

set -euo pipefail

GREEN='\033[0;32m'
CYAN='\033[0;36m'
NC='\033[0m'

info() { echo -e "${GREEN}[stop-dev]${NC} $1"; }

echo ""
echo -e "${CYAN}======================================${NC}"
echo -e "${CYAN}  Harvest Tracker — Stop Dev${NC}"
echo -e "${CYAN}======================================${NC}"
echo ""

# --- Stop Docker services ---

info "Stopping Docker services..."
docker compose down
info "Docker services stopped."

echo ""

# --- Kill background Java / Node processes if running ---

if pgrep -f "spring-boot:run" > /dev/null 2>&1; then
  info "Stopping Spring Boot backend..."
  pkill -f "spring-boot:run" || true
fi

if pgrep -f "next dev" > /dev/null 2>&1; then
  info "Stopping Next.js frontend..."
  pkill -f "next dev" || true
fi

echo ""
info "All development services stopped."
echo ""
