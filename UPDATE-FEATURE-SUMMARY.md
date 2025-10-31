# Auto-Update Feature - Summary

## ✅ What Was Added

The Neko Kiosk app now has automatic update checking and installation capability using GitHub Gist as the update server.

### New Files Created

1. **UpdateChecker.java** - Core update logic
   - Checks GitHub Gist for new versions
   - Downloads APK automatically
   - Triggers installation

2. **file_paths.xml** - FileProvider configuration
   - Enables secure file sharing for APK installation

3. **AUTO-UPDATE-SETUP.md** - Complete setup guide
   - Step-by-step GitHub Gist setup
   - How to publish updates
   - Troubleshooting tips

4. **version.json.example** - Template for version file

### Modified Files

1. **AndroidManifest.xml**
   - Added `REQUEST_INSTALL_PACKAGES` permission
   - Added FileProvider configuration

2. **build.gradle**
   - Updated versionCode to 2
   - Updated versionName to "1.1"
   - Added androidx.core dependency

3. **MainActivity.java**
   - Added update checker initialization
   - Checks for updates on app launch

## 🔧 Configuration Required

Before the auto-update feature works, you must:

### 1. Create GitHub Gist

Go to https://gist.github.com/ and create a public gist with:
- `version.json` - Version info
- `app-debug.apk` - Your APK file

### 2. Update UpdateChecker.java

Edit line 22 in `app/src/main/java/com/neko/kiosk/UpdateChecker.java`:

```java
private static final String UPDATE_URL = "https://gist.githubusercontent.com/YOUR_USERNAME/YOUR_GIST_ID/raw/version.json";
```

Replace:
- `YOUR_USERNAME` with your GitHub username
- `YOUR_GIST_ID` with your gist's ID

### 3. Rebuild the App

```bash
./gradlew clean assembleDebug
adb -s 100.94.227.90:46525 install -r app/build/outputs/apk/debug/app-debug.apk
```

## 📋 How It Works

```
App Launch
    ↓
Check GitHub Gist
    ↓
Compare versionCode
    ↓
If newer version found
    ↓
Download APK (background)
    ↓
Prompt user to install
    ↓
Update installed ✓
```

## 📦 Current Version

- **Version Code:** 2
- **Version Name:** 1.1
- **APK Size:** ~32 KB

## 🚀 Publishing Future Updates

1. Increment versionCode in `app/build.gradle`
2. Build: `./gradlew assembleDebug`
3. Upload new APK to gist
4. Update `version.json` in gist
5. Done! All devices auto-update

## 📱 First Deployment

The app is built and ready. To deploy with update checking:

```bash
# Option 1: Direct ADB (if configured)
adb -s 100.94.227.90:46525 install -r app/build/outputs/apk/debug/app-debug.apk
adb -s 100.94.227.90:46525 shell am start -n com.neko.kiosk/.MainActivity

# Option 2: Use wireless install script
./install-wireless.sh 100.94.227.90
```

## 🔍 Testing Updates

To test the update mechanism:

1. Deploy current version (1.1) to device
2. Update `build.gradle` to versionCode 3, versionName "1.2"
3. Build new APK
4. Upload to gist with updated version.json
5. Launch app on device
6. Should automatically detect and prompt for update

## ⚠️ Important Notes

- **UPDATE_URL must be configured** before auto-update works
- GitHub Gist must be **public**, not secret
- User must allow "Install unknown apps" on Android 8+
- Update check happens on every app launch
- No user interaction needed for download (only for install)

## 📚 Documentation

- **AUTO-UPDATE-SETUP.md** - Detailed setup guide
- **version.json.example** - Template for version file
- **UpdateChecker.java** - Source code (well commented)

## 🔗 Next Steps

1. Read **AUTO-UPDATE-SETUP.md** for detailed instructions
2. Create your GitHub Gist
3. Update UpdateChecker.java with your gist URL
4. Rebuild and deploy
5. Test the update flow

---

**Status:** ✅ Feature implemented, ⚠️ Configuration required

**Build Status:** ✅ Successful (version 1.1, code 2)

**Deployment:** Ready for installation
