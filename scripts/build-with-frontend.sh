#!/usr/bin/env bash
# Build frontend and embed it into the backend JAR so one deployment serves the full app.
set -e
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$ROOT/attendance-frontend"
echo "Building frontend..."
npm ci
npm run build

echo "Copying frontend into backend resources..."
mkdir -p "$ROOT/src/main/resources/static"
cp -r dist/* "$ROOT/src/main/resources/static/"

cd "$ROOT"
echo "Building backend JAR (with embedded frontend)..."
./mvnw package -DskipTests -q

echo "Done. JAR: target/attendanceApp-0.0.1-SNAPSHOT.jar"
