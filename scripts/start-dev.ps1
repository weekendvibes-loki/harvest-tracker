# start-dev.ps1 — Start all development services
# Usage: .\scripts\start-dev.ps1

$ErrorActionPreference = "Stop"

function Info($msg) { Write-Host "[start-dev] $msg" -ForegroundColor Green }
function Head($msg) { Write-Host $msg -ForegroundColor Cyan }

Write-Host ""
Head "======================================"
Head "  Harvest Tracker — Start Dev"
Head "======================================"
Write-Host ""

# --- Start Docker services ---

Info "Starting Docker services (PostgreSQL)..."
docker compose up -d
Info "Docker services started."

Write-Host ""

# --- Start backend (new window) ---

Info "Starting Spring Boot backend in a new terminal..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd backend; mvn spring-boot:run" -WindowStyle Normal
Info "Backend starting..."

Write-Host ""

# --- Start frontend (new window) ---

Info "Starting Next.js frontend in a new terminal..."
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd frontend; npm run dev" -WindowStyle Normal
Info "Frontend starting..."

Write-Host ""

# --- Display URLs ---

Head "======================================"
Head "  Development URLs"
Head "======================================"
Write-Host ""
Write-Host "  Frontend   : http://localhost:3000"
Write-Host "  Backend    : http://localhost:8080"
Write-Host "  API Docs   : http://localhost:8080/swagger-ui/index.html"
Write-Host "  Database   : localhost:5432 (harvest_tracker)"
Write-Host ""
Info "Run .\scripts\stop-dev.ps1 to stop all services."
Write-Host ""
