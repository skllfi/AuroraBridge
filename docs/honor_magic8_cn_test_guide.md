# Honor Magic 8 Pro CN — Test Guide for Aurora Bridge Device Optimizer v3

## Preparation
1. Enable Developer Options: Settings → About phone → tap Build number 7 times.
2. Enable USB debugging in Developer options.
3. (Optional but recommended) Connect device to PC via USB for the first time and run:
   ```bash
   adb devices
   adb tcpip 5555
   adb connect <device_ip>:5555
   ```
4. Grant WRITE_SECURE_SETTINGS (only via PC once, required for some commands):
   ```bash
   adb shell pm grant com.aurorabridge.optimizer android.permission.WRITE_SECURE_SETTINGS
   ```

## Basic Checks
- Open app, go to Diagnostics → Run ADB Analyzer.
- Observe found limiters (PowerGenie, MIUI etc.)

## Auto-Optimization
- Home → Brand Auto-Optimize (this will open relevant system screens for Honor/Huawei)
- Use ADB Companion → Run HuaweiFix profile for automated command execution.

## Battery & Notifications
- Settings → Export logs if needed.
- Settings → Request battery optimization whitelist (if prompted).
- ADB Companion → Run UniversalFix to disable deviceidle and whitelist app.

## Verification
- Install a test app that sends periodic notifications in background.
- Reboot device; ensure app receives notifications and stays running.
- If failures occur, check diagnostics.json in app files directory and export logs.

## Troubleshooting
- If ADB Wi-Fi commands fail: ensure device and PC are on same network and adb connection established.
- Some OEM screens vary by firmware version; Brand Auto-Optimize may open the closest matching screen.
