# Wireless ADB Setup Guide

This guide explains how to use wireless ADB debugging to deploy the Neko Kiosk app directly to your tablet without a USB cable or SSH middle host.

## Prerequisites

- Tablet and computer must be on the same network
- ADB installed on your computer
- Android 11 or higher on tablet (for native wireless debugging)

## One-Time Setup on Tablet

### Step 1: Enable Developer Options

1. Go to **Settings** > **About tablet**
2. Tap **Build number** 7 times
3. Enter your PIN/password if prompted
4. You'll see "You are now a developer!" message

### Step 2: Enable Wireless Debugging

1. Go to **Settings** > **System** > **Developer options**
2. Scroll down to **Wireless debugging**
3. Toggle it **ON**
4. Tap on **Wireless debugging** to see connection details

### Step 3: Pair Device (First Time Only)

For the first connection, you need to pair:

1. In Wireless debugging settings, tap **Pair device with pairing code**
2. You'll see a 6-digit pairing code and IP:PORT
3. On your computer, run:
   ```bash
   adb pair <IP>:<PORT>
   ```
4. Enter the 6-digit code when prompted

**Example:**
```bash
# If tablet shows: 192.168.1.100:37891 with code 123456
adb pair 192.168.1.100:37891
# Enter: 123456
```

### Step 4: Note the Connection Details

After pairing, the main Wireless debugging screen shows:
- **IP address** (e.g., 192.168.1.100)
- **Port** (usually 5555 or shown on screen)

## Deploying the App

### Quick Deploy

```bash
cd /home/alonso/Documents/fire/NekoKiosk
./install-wireless.sh <TABLET_IP>
```

**Example:**
```bash
# If tablet IP is 192.168.1.100
./install-wireless.sh 192.168.1.100

# Or with custom port
./install-wireless.sh 192.168.1.100:37885
```

### Manual Deploy

```bash
# 1. Connect to tablet
adb connect 192.168.1.100:5555

# 2. Verify connection
adb devices

# 3. Install APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 4. Launch app
adb shell am start -n com.neko.kiosk/.MainActivity
```

## Build and Deploy in One Command

```bash
cd /home/alonso/Documents/fire/NekoKiosk
./gradlew assembleDebug && ./install-wireless.sh 192.168.1.100
```

## Finding Your Tablet's IP Address

### Method 1: Wireless Debugging Screen
Settings > System > Developer options > Wireless debugging (shows IP and port)

### Method 2: WiFi Settings
Settings > Network & Internet > WiFi > Tap connected network > Advanced

### Method 3: Quick Settings
Pull down notification shade > Long press WiFi icon > Tap connected network

## Common Commands

### Connection Management
```bash
# Connect to tablet
adb connect 192.168.1.100:5555

# Disconnect
adb disconnect 192.168.1.100:5555

# List connected devices
adb devices

# Check connection status
adb devices -l
```

### App Management
```bash
# Install app (replace existing)
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Uninstall app
adb uninstall com.neko.kiosk

# Force stop app
adb shell am force-stop com.neko.kiosk

# Launch app
adb shell am start -n com.neko.kiosk/.MainActivity

# Clear app data
adb shell pm clear com.neko.kiosk
```

### Debugging
```bash
# View app logs
adb logcat | grep -i neko

# View all logs with tag filter
adb logcat -s NekoKiosk

# Take screenshot
adb shell screencap -p > screenshot.png

# View battery level
adb shell dumpsys battery

# Check if app is running
adb shell pidof com.neko.kiosk
```

### Multiple Devices
If you have multiple devices connected:
```bash
# List devices to get serial/IP
adb devices

# Use -s flag to target specific device
adb -s 192.168.1.100:5555 install -r app/build/outputs/apk/debug/app-debug.apk
adb -s 192.168.1.100:5555 shell am start -n com.neko.kiosk/.MainActivity
```

## Troubleshooting

### "Unable to connect to <IP>:<PORT>"

**Possible causes:**
- Wireless debugging is disabled on tablet
- Tablet and computer on different networks
- Firewall blocking connection
- Wrong IP address or port

**Solutions:**
1. Verify wireless debugging is enabled: Settings > Developer options > Wireless debugging
2. Check both devices on same WiFi network
3. Try disabling and re-enabling wireless debugging
4. Verify IP address matches what's shown on tablet
5. Try connecting with explicit port: `adb connect 192.168.1.100:37885`

### "Device unauthorized"

This means pairing is required:
```bash
# Pair first
adb pair <IP>:<PAIRING_PORT>
# Enter 6-digit code from tablet

# Then connect
adb connect <IP>:5555
```

### Connection Keeps Dropping

Wireless debugging may timeout. To reconnect:
```bash
adb disconnect
adb connect <IP>:5555
```

Or disable and re-enable wireless debugging on the tablet.

### Can't Find ADB Command

Make sure ADB is in your PATH:
```bash
# Find ADB location
which adb

# If not found, use full path
/path/to/android-sdk/platform-tools/adb connect 192.168.1.100
```

Or on systems with Android Studio:
```bash
~/Android/Sdk/platform-tools/adb connect 192.168.1.100
```

### Pairing Port vs Connection Port

Android wireless debugging uses TWO different ports:
- **Pairing port** (shown during pairing, e.g., 37891) - one-time use
- **Connection port** (usually 5555 or shown on main screen) - for ongoing connections

Make sure you're using the connection port (5555) after initial pairing.

### "Failed to install" Errors

```bash
# Clear existing installation
adb uninstall com.neko.kiosk

# Try installing again
adb install app/build/outputs/apk/debug/app-debug.apk

# Or force reinstall
adb install -r -d app/build/outputs/apk/debug/app-debug.apk
```

## Android Version Compatibility

### Android 11+ (Recommended)
Native wireless debugging support in Developer Options.

### Android 10 and Earlier
Wireless ADB requires initial USB connection:
```bash
# 1. Connect via USB first
# 2. Enable USB debugging in Developer Options
# 3. Enable TCP/IP mode
adb tcpip 5555

# 4. Disconnect USB
# 5. Connect wirelessly
adb connect <TABLET_IP>:5555
```

## Security Notes

- Wireless debugging should only be enabled on trusted networks
- Disable wireless debugging when not in use
- Connection is only active while "Wireless debugging" is enabled
- Pairing code expires after a short time
- Each new network may require re-pairing

## Comparison: Wireless ADB vs SSH Method

| Feature | Wireless ADB | SSH + distrobox |
|---------|-------------|-----------------|
| Setup | Enable on tablet once | Requires SSH host setup |
| Speed | Direct connection (fast) | Extra network hop |
| Network | Same WiFi required | Works over internet |
| Portability | Works anywhere | Requires specific host |
| Simplicity | Simpler | More complex |
| Reliability | May timeout | More stable |

**Recommendation**: Use wireless ADB for local development, SSH method for remote/permanent kiosk installations.

## Quick Reference Card

```bash
# SETUP (one-time)
adb pair <IP>:<PAIRING_PORT>    # Enter 6-digit code from tablet

# CONNECT
adb connect <IP>:5555

# BUILD & DEPLOY
cd NekoKiosk
./gradlew assembleDebug && ./install-wireless.sh <IP>

# COMMON OPERATIONS
adb devices                                          # List connected devices
adb install -r app/build/outputs/apk/debug/app-debug.apk  # Install app
adb shell am start -n com.neko.kiosk/.MainActivity  # Launch app
adb shell am force-stop com.neko.kiosk              # Stop app
adb logcat | grep -i neko                           # View logs
adb shell screencap -p > screenshot.png             # Screenshot
```

---

Save your tablet's IP address for quick reference:
```bash
export TABLET_IP="192.168.1.100"
./install-wireless.sh $TABLET_IP
```
