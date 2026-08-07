#!/usr/bin/env bash
# start-dev.sh — Start all development services
# Usage: bash scripts/start-dev.sh

set -euo pipefail

GREEN='\033[0;32m'
CYAN='\033[0;36m'
NC='\033[0m'

info() { echo -e "${GREEN}[start-dev]${NC} $1"; }
head() { echo -e "${CYAN}$1${NC}"; }

echo ""
head "======================================"
head "  Harvest Tracker — Start Dev"
head "======================================"
echo ""

# --- Start Docker services ---

info "Starting Docker services (PostgreSQL)..."
docker compose up -d
info "Docker services started."

echo ""

# --- Start backend (background) ---

info "Starting Spring Boot backend..."
(cd backend && mvn spring-boot:run > /tmp/harvest-backend.log 2>&1) &
BACKEND_PID=$!
info "Backend starting in background (PID: $BACKEND_PID)"

echo ""

# --- Start frontend (background) ---

info "Starting Next.js frontend..."
(cd frontend && npm run dev > /tmp/harvest-frontend.log 2>&1) &
FRONTEND_PID=$!
info "Frontend starting in background (PID: $FRONTEND_PID)"

echo ""

# --- Display URLs ---

head "======================================"
head "  Development URLs"
head "======================================"
echo ""
echo "  Frontend   : http://localhost:3000"
echo "  Backend    : http://localhost:8080"
echo "  API Docs   : http://localhost:8080/swagger-ui/index.html"
echo "  Database   : localhost:5432 (harvest_tracker)"
echo ""
echo "  Logs:"
echo "    Backend  : /tmp/harvest-backend.log"
echo "    Frontend : /tmp/harvest-frontend.log"
echo ""
info "Press Ctrl+C or run scripts/stop-dev.sh to stop services."
echo ""
