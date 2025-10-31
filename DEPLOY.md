# Deploy Neko Kiosk - Quick Guide

## What You Have

✓ **Neko Kiosk App** - Minimal kiosk app for neko remote desktop
✓ **Built APK** - Ready to install at `app/build/outputs/apk/debug/app-debug.apk`
✓ **ADB Installed** - Available at `/home/alonso/Android/Sdk/platform-tools/adb`
✓ **Two Install Methods** - Wireless ADB or SSH via aeon-tc

## Quick Deploy - Wireless ADB (Recommended)

### 1. Enable Wireless Debugging on Tablet

- Settings > Developer Options > Wireless debugging (toggle ON)
- Note the IP address shown (e.g., 192.168.1.100)

### 2. First Time Pairing (One Time Only)

```bash
# Tap "Pair device with pairing code" on tablet
# Note the IP:PORT and 6-digit code shown

adb pair <IP>:<PAIRING_PORT>
# Enter the 6-digit code when prompted
```

### 3. Deploy the App

```bash
cd /home/alonso/Documents/fire/NekoKiosk
./install-wireless.sh <TABLET_IP>
```

**Example:**
```bash
./install-wireless.sh 192.168.1.100
```

## Alternative - SSH Method (via aeon-tc)

If wireless ADB doesn't work or tablet is on different network:

```bash
cd /home/alonso/Documents/fire/NekoKiosk
./install-remote.sh
```

## Build and Deploy in One Command

```bash
cd /home/alonso/Documents/fire/NekoKiosk

# Wireless ADB
./gradlew assembleDebug && ./install-wireless.sh 192.168.1.100

# Or SSH method
./gradlew assembleDebug && ./install-remote.sh
```

## What The App Does

- Opens: `https://vm-4.console.smartsystems.work/?usr=Tablet&pwd=***&cast=1`
- Fullscreen kiosk mode (no exit button)
- Battery indicator (top-left, color-coded)
- 4-finger touch to refresh page
- Landscape orientation
- Screen stays on

## Files Structure

```
NekoKiosk/
├── app/
│   ├── src/main/
│   │   ├── java/com/neko/kiosk/
│   │   │   └── MainActivity.java          # Main app code
│   │   ├── AndroidManifest.xml            # App configuration
│   │   └── res/                           # Resources (icons)
│   ├── build.gradle                       # App build config
│   └── build/outputs/apk/debug/
│       └── app-debug.apk                  # Built APK ✓
├── build.gradle                           # Project build config
├── settings.gradle                        # Gradle settings
├── gradlew                                # Gradle wrapper (Linux/Mac)
├── install-wireless.sh                    # Deploy via wireless ADB
├── install-remote.sh                      # Deploy via SSH + ADB
├── README.md                              # Full documentation
├── QUICKSTART.md                          # Quick reference
├── WIRELESS-ADB.md                        # Wireless ADB guide
└── DEPLOY.md                              # This file
```

## Useful Commands

```bash
# Check connected devices
adb devices

# Force stop app (for testing)
adb shell am force-stop com.neko.kiosk

# Uninstall app
adb uninstall com.neko.kiosk

# View logs
adb logcat | grep -i neko

# Take screenshot
adb shell screencap -p > screenshot.png

# Rebuild app
./gradlew clean assembleDebug
```

## Troubleshooting

**Can't connect via wireless ADB?**
- Make sure tablet and computer on same WiFi network
- Verify wireless debugging is enabled on tablet
- Check IP address is correct
- Try pairing again if unauthorized

**App shows blank screen?**
- Check tablet has internet connection
- Verify URL is accessible from tablet's network
- Check logcat for errors: `adb logcat | grep -i neko`

**Build fails?**
```bash
./gradlew clean assembleDebug
```

**Can't exit app?**
This is intentional (kiosk mode)! Use:
```bash
adb shell am force-stop com.neko.kiosk
```

## Next Steps

1. Enable wireless debugging on tablet
2. Run `./install-wireless.sh <TABLET_IP>`
3. On tablet: Press Home, select "Neko Kiosk" as default launcher
4. Done! App will now auto-launch on boot

## Documentation

- **README.md** - Full documentation
- **QUICKSTART.md** - Quick start guide
- **WIRELESS-ADB.md** - Detailed wireless ADB setup guide
- **DEPLOY.md** - This file

---

**Package**: com.neko.kiosk
**URL**: https://vm-4.console.smartsystems.work/?usr=Tablet&pwd=***&cast=1
**Orientation**: Landscape
**ADB**: `/home/alonso/Android/Sdk/platform-tools/adb`
