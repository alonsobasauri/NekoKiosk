# Neko Kiosk - Quick Start Guide

## One-Line Deploy

```bash
cd /home/alonso/Documents/fire/NekoKiosk && ./gradlew assembleDebug && ./install-remote.sh
```

## What This App Does

Opens `https://vm-4.console.smartsystems.work/?usr=Tablet&pwd=TheLegend0fZeld@&cast=1` in fullscreen kiosk mode.

## Key Features

- 4-finger touch = refresh page
- Battery indicator (top-left, color-coded)
- No back button, no exit
- Screen stays on
- Landscape mode

## First Time Setup

1. Build the app:
   ```bash
   cd NekoKiosk
   ./gradlew assembleDebug
   ```

2. Deploy to tablet:
   ```bash
   ./install-remote.sh
   ```

3. On tablet: Press Home button, select "Neko Kiosk" as default launcher

## Common Tasks

### Rebuild and Deploy
```bash
./gradlew assembleDebug && ./install-remote.sh
```

### Force Stop App (for testing)
```bash
ssh alonso@aeon-tc "distrobox enter tumbleweed -- adb shell am force-stop com.neko.kiosk"
```

### Take Screenshot
```bash
ssh alonso@aeon-tc "distrobox enter tumbleweed -- adb shell screencap -p" > /tmp/screenshot.png
```

### View Logs
```bash
ssh alonso@aeon-tc "distrobox enter tumbleweed -- adb logcat | grep -i neko"
```

## Changing the URL

Edit `app/src/main/java/com/neko/kiosk/MainActivity.java` line 16:

```java
private static final String NEKO_URL = "https://your-new-url-here";
```

Then rebuild and redeploy.

## Troubleshooting

**Build fails**: Try `./gradlew clean assembleDebug`

**Deployment fails**: Check SSH access with `ssh alonso@aeon-tc "echo test"`

**Blank screen**: Check if URL is accessible from tablet's network

**Can't exit app**: This is intentional! Use `adb shell am force-stop com.neko.kiosk`
