# Auto-Update Setup Guide

This guide explains how to set up automatic updates for the Neko Kiosk app using GitHub Gists.

## How It Works

The app checks a GitHub Gist on startup for a newer version. If found:
1. Downloads the new APK automatically
2. Prompts user to install the update
3. Installs seamlessly on the device

## Setup Steps

### 1. Create a GitHub Gist

1. Go to https://gist.github.com/
2. Sign in to your GitHub account
3. Create a **new gist** with the following file:

**Filename:** `version.json`

**Content:**
```json
{
  "versionCode": 2,
  "versionName": "1.1",
  "apkUrl": "https://gist.githubusercontent.com/USERNAME/GIST_ID/raw/app-debug.apk"
}
```

4. Make sure the gist is **PUBLIC**
5. Click "Create public gist"

### 2. Upload Your APK to the Gist

1. Open your newly created gist
2. Click "Edit" button
3. Click "Add file"
4. Upload `app/build/outputs/apk/debug/app-debug.apk`
5. Click "Update public gist"

### 3. Get the Raw URLs

After creating the gist, you'll need two URLs:

**For version.json:**
- Click on the "Raw" button next to `version.json`
- Copy the URL (looks like: `https://gist.githubusercontent.com/USERNAME/GIST_ID/raw/HASH/version.json`)
- Remove the `/HASH` part to get: `https://gist.githubusercontent.com/USERNAME/GIST_ID/raw/version.json`

**For app-debug.apk:**
- Click on the "Raw" button next to `app-debug.apk`
- Copy the URL (looks like: `https://gist.githubusercontent.com/USERNAME/GIST_ID/raw/HASH/app-debug.apk`)
- Remove the `/HASH` part to get: `https://gist.githubusercontent.com/USERNAME/GIST_ID/raw/app-debug.apk`

### 4. Update UpdateChecker.java

Edit `app/src/main/java/com/neko/kiosk/UpdateChecker.java`:

```java
private static final String UPDATE_URL = "https://gist.githubusercontent.com/USERNAME/GIST_ID/raw/version.json";
```

Replace:
- `USERNAME` with your GitHub username
- `GIST_ID` with your gist ID (found in the gist URL)

### 5. Update version.json Content

Edit your gist's `version.json` file with the correct APK URL:

```json
{
  "versionCode": 2,
  "versionName": "1.1",
  "apkUrl": "https://gist.githubusercontent.com/USERNAME/GIST_ID/raw/app-debug.apk"
}
```

### 6. Rebuild and Deploy

```bash
cd /home/alonso/Documents/fire/NekoKiosk
./gradlew assembleDebug
adb -s 100.94.227.90:46525 install -r app/build/outputs/apk/debug/app-debug.apk
```

## Publishing Updates

When you want to release a new version:

### Step 1: Update Version in build.gradle

Edit `app/build.gradle`:

```gradle
defaultConfig {
    applicationId "com.neko.kiosk"
    minSdk 24
    targetSdk 35
    versionCode 3        // Increment this
    versionName "1.2"    // Update this
}
```

### Step 2: Build the New APK

```bash
./gradlew clean assembleDebug
```

### Step 3: Update GitHub Gist

1. Go to your gist on GitHub
2. Click "Edit"
3. Update `version.json`:
   ```json
   {
     "versionCode": 3,
     "versionName": "1.2",
     "apkUrl": "https://gist.githubusercontent.com/USERNAME/GIST_ID/raw/app-debug.apk"
   }
   ```
4. Delete the old `app-debug.apk` file
5. Upload the new `app/build/outputs/apk/debug/app-debug.apk`
6. Click "Update public gist"

### Step 4: Test the Update

Launch the app on the tablet - it should automatically detect and install the new version!

## Version Numbering

- **versionCode**: Integer that must increment (1, 2, 3, 4...)
- **versionName**: Human-readable version (1.0, 1.1, 1.2, 2.0...)

The app compares `versionCode` to determine if an update is available.

## Update Behavior

- ✅ Checks for updates on every app launch
- ✅ Downloads APK in background
- ✅ Shows notification when download completes
- ✅ Prompts user to install (Android security requirement)
- ✅ Silent check - no annoying popups if up-to-date

## Troubleshooting

### Update not detected

1. Check that `versionCode` in gist is higher than in `app/build.gradle`
2. Verify the UPDATE_URL is correct in `UpdateChecker.java`
3. Check logcat: `adb logcat | grep UpdateChecker`

### Download fails

1. Make sure gist is **public**, not secret
2. Verify APK URL is correct in `version.json`
3. Test the APK URL in a browser - it should download

### Install fails

1. Make sure `REQUEST_INSTALL_PACKAGES` permission is in manifest
2. On Android 8+, user must allow "Install unknown apps" from this source
3. Settings > Apps > Neko Kiosk > Install unknown apps > Allow

### How to force update check

Simply close and reopen the app - it checks on every launch.

## Security Notes

- ⚠️ GitHub Gist URLs are public - anyone can download your APK
- ⚠️ No signature verification - only use this for internal/testing
- ⚠️ For production apps, use Google Play Store or proper MDM solution

## Example Gist Structure

Your gist should contain exactly 2 files:

```
version.json          (280 bytes)
app-debug.apk        (22 KB)
```

## Quick Reference

**Current version:** 1.1 (versionCode 2)

**Update flow:**
1. App launches → Checks gist
2. If new version → Downloads APK
3. Download completes → Prompts install
4. User taps → Installs update
5. Update installed → Done!

**Typical update time:** 5-10 seconds (depending on network)

## Advanced: Automation Script

Create `upload-update.sh`:

```bash
#!/bin/bash
# Quick script to upload updates to GitHub Gist

GIST_ID="your_gist_id_here"
VERSION_CODE="3"
VERSION_NAME="1.2"

# Build APK
./gradlew clean assembleDebug

# Create version.json
cat > /tmp/version.json <<EOF
{
  "versionCode": ${VERSION_CODE},
  "versionName": "${VERSION_NAME}",
  "apkUrl": "https://gist.githubusercontent.com/USERNAME/${GIST_ID}/raw/app-debug.apk"
}
EOF

echo "Now manually update the gist at:"
echo "https://gist.github.com/USERNAME/${GIST_ID}"
echo ""
echo "Files to upload:"
echo "  1. /tmp/version.json"
echo "  2. app/build/outputs/apk/debug/app-debug.apk"
```

---

**Note:** Replace `USERNAME` and `GIST_ID` throughout this guide with your actual GitHub username and gist ID.
