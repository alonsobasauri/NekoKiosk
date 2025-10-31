#!/bin/bash
# Deploy Neko Kiosk APK to tablet via wireless ADB

set -e

APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
PACKAGE_NAME="com.neko.kiosk"

echo "=== Neko Kiosk Wireless ADB Deployment ==="
echo ""

# Check if APK exists
if [ ! -f "$APK_PATH" ]; then
    echo "Error: APK not found at $APK_PATH"
    echo "Run './gradlew assembleDebug' first to build the APK"
    exit 1
fi

# Check if tablet IP is provided
if [ -z "$1" ]; then
    echo "Usage: $0 <TABLET_IP>[:PORT]"
    echo ""
    echo "Example: $0 192.168.1.100"
    echo "         $0 192.168.1.100:5555"
    echo ""
    echo "To enable wireless debugging on tablet:"
    echo "  1. Enable Developer Options (tap Build Number 7 times)"
    echo "  2. Settings > Developer Options > Wireless debugging"
    echo "  3. Enable Wireless debugging"
    echo "  4. Tap 'Pair device with pairing code' (first time only)"
    echo "  5. Or tap 'Wireless debugging' to see IP address and port"
    exit 1
fi

TABLET_ADDR="$1"

# Add default port if not specified
if [[ ! "$TABLET_ADDR" =~ .*:.* ]]; then
    TABLET_ADDR="${TABLET_ADDR}:5555"
fi

echo "Tablet address: $TABLET_ADDR"
echo ""

# Check if already connected
if adb devices | grep -q "$TABLET_ADDR"; then
    echo "✓ Already connected to $TABLET_ADDR"
else
    echo "1. Connecting to tablet..."
    adb connect "$TABLET_ADDR"

    # Wait a moment for connection to establish
    sleep 2

    # Verify connection
    if ! adb devices | grep -q "$TABLET_ADDR"; then
        echo "Error: Failed to connect to $TABLET_ADDR"
        echo ""
        echo "Troubleshooting:"
        echo "  - Make sure wireless debugging is enabled on tablet"
        echo "  - Check that tablet and computer are on same network"
        echo "  - Verify IP address is correct"
        echo "  - Try pairing first if this is first connection"
        exit 1
    fi
fi

echo "2. Installing APK..."
adb -s "$TABLET_ADDR" install -r "$APK_PATH"

echo "3. Launching app..."
adb -s "$TABLET_ADDR" shell am start -n ${PACKAGE_NAME}/.MainActivity

echo ""
echo "=== Deployment Complete ==="
echo "App: Neko Kiosk"
echo "Package: ${PACKAGE_NAME}"
echo "Device: $TABLET_ADDR"
echo ""
echo "Useful commands:"
echo "  Force stop:  adb -s $TABLET_ADDR shell am force-stop ${PACKAGE_NAME}"
echo "  Uninstall:   adb -s $TABLET_ADDR uninstall ${PACKAGE_NAME}"
echo "  Screenshot:  adb -s $TABLET_ADDR shell screencap -p > screenshot.png"
echo "  Logs:        adb -s $TABLET_ADDR logcat | grep -i neko"
