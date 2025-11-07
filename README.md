Aurora Bridge — Device Optimizer v2 (Offline)
============================================

This is a Jetpack Compose Android project skeleton designed to help optimize
Chinese OEM ROMs (Honor/Huawei, Xiaomi, Realme, Oppo, Vivo) via local utilities
and an ADB Companion (Wi-Fi) to execute allowed system commands.

- minSdk 33 (Android 13+) / targetSdk 34
- Jetpack Compose UI, WorkManager, NotificationListenerService
- Local-only operation (no Firebase)

How to use:
1. Open this project in Android Studio Arctic Fox or newer.
2. Sync Gradle and run on a device (Android 13+).
3. Grant runtime permissions when requested (notifications, etc.).
4. Use ADB Companion to connect via Wi-Fi for advanced system commands.


## Testing & Permissions
- To grant WRITE_SECURE_SETTINGS (required for some ADB commands) run on PC:
  `adb shell pm grant com.aurorabridge.optimizer android.permission.WRITE_SECURE_SETTINGS`
- To enable ADB over Wi-Fi from PC: `adb tcpip 5555` then `adb connect <ip>:5555`
- Use 'ADB Companion' screen to enable/disable ADB Wi-Fi from device (may require WRITE_SECURE_SETTINGS)


## v3 updates
- NavHost + ViewModels added
- Full ADB Analyzer/Optimizer modules wired to UI
- Docs: Honor Magic 8 Pro CN test guide added
- AutoStarter/AppKiller placeholders mentioned in build.gradle.kts
