#!/bin/bash
# Standalone Test Repository Bootstrap Script - Bash version
# Clones CVConnect main repo and builds dependencies for local test run

REPO_URL="${1:-https://github.com/trungtoto/CVConnect.git}"
BRANCH="${2:-master}"
SOURCE_DIR="${3:-.cvconnect-source}"

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TEST_REPO_ROOT="$(dirname "$SCRIPT_DIR")"
SOURCE_ROOT="$TEST_REPO_ROOT/$SOURCE_DIR"

echo "====================================================="
echo "CVConnect Tests Bootstrap"
echo "====================================================="

echo ""
echo "Test repo root: $TEST_REPO_ROOT"
echo "Source dir: $SOURCE_ROOT"

# Step 1: Clone CVConnect source if not exists
if [ ! -d "$SOURCE_ROOT" ]; then
    echo ""
    echo "[1/4] Cloning CVConnect repository..."
    git clone --depth 1 --branch "$BRANCH" "$REPO_URL" "$SOURCE_ROOT"
else
    echo ""
    echo "[1/4] CVConnect source already exists, skipping clone"
fi

# Step 2: Build CVConnect main services
echo ""
echo "[2/4] Building core-service, api-gateway, user-service..."
mvn -f "$SOURCE_ROOT/BE/pom.xml" -pl core-service,api-gateway,user-service clean install -DskipTests=true

# Step 3: Run tests with Maven
echo ""
echo "[3/4] Running unit tests..."
mvn -f "$TEST_REPO_ROOT/pom.xml" clean test "-Dmaven.test.failure.ignore=true"
TEST_EXIT_CODE=$?

# Step 4: Summary
echo ""
echo "[4/4] Test run complete"
echo "====================================================="
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo "All tests passed!"
else
    echo "Some tests failed (details above)"
fi
echo "====================================================="

exit $TEST_EXIT_CODE
