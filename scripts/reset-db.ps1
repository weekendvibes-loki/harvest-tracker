# reset-db.ps1 — Reset the development database (wipe and restart)
# Usage: .\scripts\reset-db.ps1

$ErrorActionPreference = "Stop"

function Info($msg) { Write-Host "[reset-db] $msg" -ForegroundColor Green }
function Warn($msg) { Write-Host "[reset-db] $msg" -ForegroundColor Yellow }

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "  Harvest Tracker — Reset Database"    -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

Warn "This will permanently delete all local database data."
Write-Host ""
$confirm = Read-Host "Are you sure? (yes/no)"

if ($confirm -ne "yes") {
    Info "Reset cancelled."
    exit 0
}

Write-Host ""

# --- Stop containers ---

Info "Stopping Docker containers..."
docker compose down
Info "Containers stopped."

Write-Host ""

# --- Remove database volume ---

Info "Removing PostgreSQL data volume..."
$projectName = (Get-Item .).Name.ToLower() -replace '[^a-z0-9]', ''
$volumeName = "${projectName}_postgres_data"

docker volume rm $volumeName 2>$null
if ($LASTEXITCODE -ne 0) {
    docker volume rm "harvest-tracker_postgres_data" 2>$null
}
Info "Volume removed."

Write-Host ""

# --- Restart database ---

Info "Starting PostgreSQL..."
docker compose up -d
Info "PostgreSQL is running."

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Info "Database reset complete. Fresh database is ready."
Write-Host ""
