#!/usr/bin/env bash
# clean.sh — Remove build artifacts and generated files
# Usage: bash scripts/clean.sh

set -euo pipefail

GREEN='\033[0;32m'
CYAN='\033[0;36m'
NC='\033[0m'

info() { echo -e "${GREEN}[clean]${NC} $1"; }

echo ""
echo -e "${CYAN}======================================${NC}"
echo -e "${CYAN}  Harvest Tracker — Clean${NC}"
echo -e "${CYAN}======================================${NC}"
echo ""

# --- Frontend ---

info "Cleaning frontend..."

[ -d "frontend/node_modules" ] && rm -rf frontend/node_modules && info "  Removed frontend/node_modules"
[ -d "frontend/.next"        ] && rm -rf frontend/.next        && info "  Removed frontend/.next"
[ -d "frontend/out"          ] && rm -rf frontend/out          && info "  Removed frontend/out"
[ -d "frontend/dist"         ] && rm -rf frontend/dist         && info "  Removed frontend/dist"

echo ""

# --- Backend ---

info "Cleaning backend..."

[ -d "backend/target" ] && rm -rf backend/target && info "  Removed backend/target"
[ -d "backend/build"  ] && rm -rf backend/build  && info "  Removed backend/build"

echo ""

# --- Temp files ---

info "Removing temporary files..."
find . -name "*.log" -not -path "./.git/*" -delete 2>/dev/null || true
find . -name "*.tmp" -not -path "./.git/*" -delete 2>/dev/null || true
find . -name ".DS_Store" -delete 2>/dev/null || true

echo ""
info "Clean complete."
echo ""
