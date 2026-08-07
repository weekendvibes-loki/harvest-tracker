#!/usr/bin/env bash
# setup.sh — Install dependencies and verify required software
# Usage: bash scripts/setup.sh

set -euo pipefail

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

info()    { echo -e "${GREEN}[setup]${NC} $1"; }
warn()    { echo -e "${YELLOW}[setup]${NC} $1"; }
error()   { echo -e "${RED}[setup]${NC} $1"; exit 1; }

echo ""
echo "=============================="
echo "  Harvest Tracker — Setup"
echo "=============================="
echo ""

# --- Check required software ---

info "Checking required software..."

command -v node  >/dev/null 2>&1 || error "Node.js is not installed. Visit https://nodejs.org"
command -v npm   >/dev/null 2>&1 || error "npm is not installed. It comes with Node.js."
command -v java  >/dev/null 2>&1 || error "Java is not installed. Install JDK 21+."
command -v mvn   >/dev/null 2>&1 || error "Maven is not installed. Visit https://maven.apache.org"
command -v docker >/dev/null 2>&1 || error "Docker is not installed. Visit https://docker.com"

NODE_VERSION=$(node -v)
JAVA_VERSION=$(java -version 2>&1 | head -1)
MVN_VERSION=$(mvn -v 2>&1 | head -1)
DOCKER_VERSION=$(docker -v)

info "Node.js  : $NODE_VERSION"
info "Java     : $JAVA_VERSION"
info "Maven    : $MVN_VERSION"
info "Docker   : $DOCKER_VERSION"

echo ""

# --- Install frontend dependencies ---

info "Installing frontend dependencies..."
cd frontend
npm install
cd ..

info "Frontend dependencies installed."

echo ""

# --- Verify backend ---

info "Verifying backend build..."
cd backend
mvn dependency:resolve -q
cd ..

info "Backend dependencies resolved."

echo ""

# --- Copy .env if not present ---

if [ ! -f .env ]; then
  cp .env.example .env
  warn ".env file created from .env.example — update your credentials before starting."
else
  info ".env file already exists."
fi

echo ""
echo "=============================="
echo "  Setup Complete"
echo "=============================="
echo ""
echo "Next steps:"
echo "  1. Edit .env with your local credentials"
echo "  2. Run: bash scripts/start-dev.sh"
echo ""
