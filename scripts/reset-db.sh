#!/usr/bin/env bash
# reset-db.sh — Reset the development database (wipe and restart)
# Usage: bash scripts/reset-db.sh

set -euo pipefail

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

info() { echo -e "${GREEN}[reset-db]${NC} $1"; }
warn() { echo -e "${YELLOW}[reset-db]${NC} $1"; }

echo ""
echo -e "${CYAN}======================================${NC}"
echo -e "${CYAN}  Harvest Tracker — Reset Database${NC}"
echo -e "${CYAN}======================================${NC}"
echo ""

warn "This will permanently delete all local database data."
echo ""
read -r -p "Are you sure? (yes/no): " CONFIRM

if [ "$CONFIRM" != "yes" ]; then
  info "Reset cancelled."
  exit 0
fi

echo ""

# --- Stop containers ---

info "Stopping Docker containers..."
docker compose down
info "Containers stopped."

echo ""

# --- Remove database volume ---

info "Removing PostgreSQL data volume..."
docker volume rm harvest-tracker_postgres_data 2>/dev/null || docker volume rm "$(basename "$(pwd)")_postgres_data" 2>/dev/null || true
info "Volume removed."

echo ""

# --- Restart database ---

info "Starting PostgreSQL..."
docker compose up -d
info "PostgreSQL is running."

echo ""
echo -e "${CYAN}======================================${NC}"
info "Database reset complete. Fresh database is ready."
echo ""
