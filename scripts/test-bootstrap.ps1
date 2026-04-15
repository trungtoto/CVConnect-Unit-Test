# Standalone Test Repository Bootstrap Script
# Clones CVConnect main repo and builds dependencies for local test run

param(
    [string]$RepoUrl = "https://github.com/trungtoto/CVConnect.git",
    [string]$Branch = "master",
    [string]$SourceDir = ".cvconnect-source"
)

$ErrorActionPreference = "Stop"

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "CVConnect Tests Bootstrap" -ForegroundColor Cyan
Write-Host "=====================================================" -ForegroundColor Cyan

# Get script directory and test repo root
$scriptPath = Split-Path -Parent -Path $MyInvocation.MyCommand.Definition
$testRepoRoot = Split-Path -Parent -Path $scriptPath
$sourceRoot = Join-Path -Path $testRepoRoot -ChildPath $SourceDir

Write-Host "`nTest repo root: $testRepoRoot" -ForegroundColor Yellow
Write-Host "Source dir: $sourceRoot" -ForegroundColor Yellow

# Step 1: Clone CVConnect source if not exists
if (-not (Test-Path $sourceRoot)) {
    Write-Host "`n[1/4] Cloning CVConnect repository..." -ForegroundColor Cyan
    & git clone --depth 1 --branch $Branch $RepoUrl $sourceRoot
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to clone repository"
    }
} else {
    Write-Host "`n[1/4] CVConnect source already exists, skipping clone" -ForegroundColor Green
}

# Step 2: Build CVConnect main services
Write-Host "`n[2/4] Building core-service, api-gateway, user-service, notify-service..." -ForegroundColor Cyan
& mvn -f "$sourceRoot/BE/pom.xml" -pl core-service,api-gateway,user-service,notify-service clean install -DskipTests=true
if ($LASTEXITCODE -ne 0) {
    throw "Failed to build CVConnect services"
}

# Step 3: Run tests with Maven
Write-Host "`n[3/4] Running unit tests..." -ForegroundColor Cyan
& mvn -f (Join-Path $testRepoRoot "pom.xml") clean test "-Dmaven.test.failure.ignore=true"
$testExitCode = $LASTEXITCODE

# Step 4: Summary
Write-Host "`n[4/4] Test run complete" -ForegroundColor Cyan
Write-Host "`n=====================================================" -ForegroundColor Cyan
if ($testExitCode -eq 0) {
    Write-Host "All tests passed!" -ForegroundColor Green
} else {
    Write-Host "Some tests failed (details above)" -ForegroundColor Yellow
}
Write-Host "=====================================================" -ForegroundColor Cyan

exit $testExitCode
