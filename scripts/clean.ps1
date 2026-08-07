# clean.ps1 — Remove build artifacts and generated files
# Usage: .\scripts\clean.ps1

$ErrorActionPreference = "SilentlyContinue"

function Info($msg) { Write-Host "[clean] $msg" -ForegroundColor Green }

Write-Host ""
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "  Harvest Tracker — Clean"             -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

# --- Frontend ---

Info "Cleaning frontend..."

$frontendPaths = @(
    "frontend\node_modules",
    "frontend\.next",
    "frontend\out",
    "frontend\dist"
)

foreach ($path in $frontendPaths) {
    if (Test-Path $path) {
        Remove-Item -Recurse -Force $path
        Info "  Removed $path"
    }
}

Write-Host ""

# --- Backend ---

Info "Cleaning backend..."

$backendPaths = @(
    "backend\target",
    "backend\build"
)

foreach ($path in $backendPaths) {
    if (Test-Path $path) {
        Remove-Item -Recurse -Force $path
        Info "  Removed $path"
    }
}

Write-Host ""

# --- Temp files ---

Info "Removing temporary files..."

Get-ChildItem -Recurse -File -Include "*.log","*.tmp" |
    Where-Object { $_.FullName -notmatch '\\.git\\' } |
    Remove-Item -Force

Write-Host ""
Info "Clean complete."
Write-Host ""
