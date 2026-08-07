# setup.ps1 — Install dependencies and verify required software
# Usage: .\scripts\setup.ps1

$ErrorActionPreference = "Stop"

function Info($msg)  { Write-Host "[setup] $msg" -ForegroundColor Green }
function Warn($msg)  { Write-Host "[setup] $msg" -ForegroundColor Yellow }
function Fail($msg)  { Write-Host "[setup] $msg" -ForegroundColor Red; exit 1 }

Write-Host ""
Write-Host "==============================" -ForegroundColor Cyan
Write-Host "  Harvest Tracker — Setup"    -ForegroundColor Cyan
Write-Host "==============================" -ForegroundColor Cyan
Write-Host ""

# --- Check required software ---

Info "Checking required software..."

if (-not (Get-Command node -ErrorAction SilentlyContinue))  { Fail "Node.js is not installed. Visit https://nodejs.org" }
if (-not (Get-Command npm  -ErrorAction SilentlyContinue))  { Fail "npm is not installed. It comes with Node.js." }
if (-not (Get-Command java -ErrorAction SilentlyContinue))  { Fail "Java is not installed. Install JDK 21+." }
if (-not (Get-Command mvn  -ErrorAction SilentlyContinue))  { Fail "Maven is not installed. Visit https://maven.apache.org" }
if (-not (Get-Command docker -ErrorAction SilentlyContinue)){ Fail "Docker is not installed. Visit https://docker.com" }

Info "Node.js  : $(node -v)"
Info "Java     : $(java -version 2>&1 | Select-Object -First 1)"
Info "Maven    : $(mvn -v 2>&1 | Select-Object -First 1)"
Info "Docker   : $(docker -v)"

Write-Host ""

# --- Install frontend dependencies ---

Info "Installing frontend dependencies..."
Set-Location frontend
npm install
Set-Location ..
Info "Frontend dependencies installed."

Write-Host ""

# --- Verify backend ---

Info "Verifying backend dependencies..."
Set-Location backend
mvn dependency:resolve -q
Set-Location ..
Info "Backend dependencies resolved."

Write-Host ""

# --- Copy .env if not present ---

if (-not (Test-Path ".env")) {
    Copy-Item ".env.example" ".env"
    Warn ".env file created from .env.example — update your credentials before starting."
} else {
    Info ".env file already exists."
}

Write-Host ""
Write-Host "==============================" -ForegroundColor Cyan
Write-Host "  Setup Complete"              -ForegroundColor Cyan
Write-Host "==============================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Next steps:"
Write-Host "  1. Edit .env with your local credentials"
Write-Host "  2. Run: .\scripts\start-dev.ps1"
Write-Host ""
