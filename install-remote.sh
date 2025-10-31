#!/bin/bash
# Deploy Neko Kiosk APK to remote tablet via SSH and ADB

set -e

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
REMOTE_HOST="aeon-tc"
REMOTE_USER="alonso"
REMOTE_PATH="/tmp/neko-kiosk.apk"
PACKAGE_NAME="com.neko.kiosk"

echo "=== Neko Kiosk Remote Deployment ==="
echo ""

# Check if APK exists
if [ ! -f "$APK_PATH" ]; then
    echo "Error: APK not found at $APK_PATH"
    echo "Run './gradlew assembleDebug' first to build the APK"
    exit 1
fi

echo "1. Copying APK to remote host..."
scp "$APK_PATH" "${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_PATH}"

echo "2. Installing APK via remote ADB..."
ssh "${REMOTE_USER}@${REMOTE_HOST}" "distrobox enter tumbleweed -- adb install -r ${REMOTE_PATH}"

echo "3. Launching app..."
ssh "${REMOTE_USER}@${REMOTE_HOST}" "distrobox enter tumbleweed -- adb shell am start -n ${PACKAGE_NAME}/.MainActivity"

echo ""
echo "=== Deployment Complete ==="
echo "App: Neko Kiosk"
echo "Package: ${PACKAGE_NAME}"
echo "URL: https://vm-4.console.smartsystems.work/?usr=Tablet&pwd=***&cast=1"
