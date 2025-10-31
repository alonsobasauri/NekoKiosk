# Neko Kiosk

A minimalist fullscreen kiosk Android app for displaying the Neko remote desktop in cast mode.

## Features

- **Fullscreen Cast Mode**: Displays Neko WebRTC stream with no controls
- **Auto-authentication**: Embedded credentials for seamless access
- **Battery Monitor**: Color-coded battery percentage indicator (top-left)
  - White: >50% battery
  - Yellow: 21-50% battery
  - Red: ≤20% battery
- **4-Finger Refresh**: Place 4 fingers on screen to reload the page
- **Kiosk Mode**: Disabled back button, prevents accidental exit
- **Screen Always On**: Prevents screen from sleeping
- **Landscape Orientation**: Fixed landscape mode

## Target URL

The app connects to:
```
https://vm-4.console.smartsystems.work/?usr=Tablet&pwd=TheLegend0fZeld@&cast=1
```

To change the URL, edit `MainActivity.java:16` and rebuild.

## Building

### Prerequisites
- Android Studio installed at `/opt/android-studio/`
- Android SDK with API 35
- Java 8 or higher

### Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build and deploy to remote tablet
./gradlew assembleDebug && ./install-remote.sh

# Clean build
./gradlew clean assembleDebug
```

The APK will be generated at: `app/build/outputs/apk/debug/app-debug.apk`

## Installation

### Method 1: Remote Deployment (Recommended)

The tablet is connected via ADB to `alonso@aeon-tc` through a distrobox container:

```bash
# Build and deploy
./gradlew assembleDebug
./install-remote.sh
```

### Method 2: Manual Installation

```bash
# Copy APK to tablet host
scp app/build/outputs/apk/debug/app-debug.apk alonso@aeon-tc:/tmp/neko-kiosk.apk

# Install via remote ADB
ssh alonso@aeon-tc "distrobox enter tumbleweed -- adb install -r /tmp/neko-kiosk.apk"

# Launch app
ssh alonso@aeon-tc "distrobox enter tumbleweed -- adb shell am start -n com.neko.kiosk/.MainActivity"
```

### Method 3: Direct ADB (if tablet connected locally)

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.neko.kiosk/.MainActivity
```

## Kiosk Mode Setup

To prevent users from exiting the app:

### App Pinning (Simple)
1. Settings > Security > App Pinning
2. Enable App Pinning
3. Open Neko Kiosk
4. Tap Recent Apps button
5. Tap app icon and select "Pin"

To unpin: Long press Back + Recent Apps

### Set as Home Launcher (Recommended)
The app registers as a home launcher category. On first launch, Android will ask which launcher to use - select "Neko Kiosk" and choose "Always".

To change launcher later:
```bash
# Via ADB
adb shell pm clear com.android.launcher3
# Then press Home button and select Neko Kiosk again
```

## Development

### Key Files
- `MainActivity.java` - Main activity with WebView and kiosk logic
- `AndroidManifest.xml` - App configuration and permissions
- `app/build.gradle` - Build configuration

### Useful Commands

```bash
# Force stop app
ssh alonso@aeon-tc "distrobox enter tumbleweed -- adb shell am force-stop com.neko.kiosk"

# View app logs
ssh alonso@aeon-tc "distrobox enter tumbleweed -- adb logcat -s NekoKiosk"

# Take screenshot
ssh alonso@aeon-tc "distrobox enter tumbleweed -- adb shell screencap -p" > /tmp/screenshot.png

# Check if app is running
ssh alonso@aeon-tc "distrobox enter tumbleweed -- adb shell dumpsys activity | grep com.neko.kiosk"
```

## Customization

### Change URL
Edit `MainActivity.java` line 16:
```java
private static final String NEKO_URL = "https://your-url-here";
```

### Change Orientation
Edit `AndroidManifest.xml` line 21:
```xml
android:screenOrientation="portrait"  <!-- or "landscape" -->
```

### Adjust Battery Indicator Position
Edit `MainActivity.java` in the `setupBatteryIndicator()` method:
```java
params.topMargin = 10;   // Distance from top
params.leftMargin = 10;  // Distance from left
```

## Troubleshooting

### App doesn't build
```bash
./gradlew clean
./gradlew assembleDebug
```

### Remote deployment fails
Check SSH access:
```bash
ssh alonso@aeon-tc "echo 'SSH works'"
ssh alonso@aeon-tc "distrobox enter tumbleweed -- adb devices"
```

### WebView shows blank page
- Check internet connection
- Verify URL is accessible in a regular browser
- Check logcat for errors

### Can't exit app during testing
```bash
# Force stop via ADB
ssh alonso@aeon-tc "distrobox enter tumbleweed -- adb shell am force-stop com.neko.kiosk"

# Or uninstall
ssh alonso@aeon-tc "distrobox enter tumbleweed -- adb uninstall com.neko.kiosk"
```

## Related Projects

- **KioskWebApp**: Full-featured kiosk app with keyboard shortcuts and radial menu
- **SimpleKioskApp**: Basic Android kiosk template

## Architecture

```
NekoKiosk (Android App)
    ↓ HTTPS + WebRTC
vm-4.console.smartsystems.work (Neko Server)
    ↓ SPICE
QEMU/KVM VM (Ubuntu Desktop)
```

## License

Free to use and modify.
