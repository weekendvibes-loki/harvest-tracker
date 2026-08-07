# stop-dev.ps1 — Stop all development services
# Usage: .\scripts\stop-dev.ps1

$ErrorActionPreference = "SilentlyContinue"

function Info($msg) { Write-Host "[stop-dev] $msg" -ForegroundColor Green }

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "  Harvest Tracker — Stop Dev"          -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# --- Stop Docker services ---

Info "Stopping Docker services..."
docker compose down
Info "Docker services stopped."

Write-Host ""

# --- Kill Java processes (Spring Boot) ---

$javaProcs = Get-Process -Name "java" -ErrorAction SilentlyContinue
if ($javaProcs) {
    Info "Stopping Java (Spring Boot) processes..."
    $javaProcs | Stop-Process -Force
}

# --- Kill Node processes (Next.js) ---

$nodeProcs = Get-Process -Name "node" -ErrorAction SilentlyContinue
if ($nodeProcs) {
    Info "Stopping Node.js (Next.js) processes..."
    $nodeProcs | Stop-Process -Force
}

Write-Host ""
Info "All development services stopped."
Write-Host ""
