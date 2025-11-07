# adb_commands.md — ADB Commands & Profiles for Aurora Bridge Device Optimizer

## General tips
- Use `adb shell` to run shell commands on device.
- For Wi-Fi ADB: `adb tcpip 5555` then `adb connect <device_ip>:5555`.
- Some commands require `WRITE_SECURE_SETTINGS` permission; grant once via PC `adb shell pm grant <pkg> android.permission.WRITE_SECURE_SETTINGS`.

## Diagnostics (examples)
- List packages: `pm list packages`
- Check device idle: `dumpsys deviceidle`
- Check battery: `dumpsys battery`

## Energy / Background
- Disable Doze: `dumpsys deviceidle disable`
- Whitelist app: `dumpsys deviceidle whitelist +<package>`
- Remove whitelist: `dumpsys deviceidle whitelist -<package>`

## Notifications & AppOps
- Allow notification listener: `cmd notification allow_listener <component>`
- AppOps allow background run: `cmd appops set <package> RUN_IN_BACKGROUND allow`

## OEM specific quick fixes
### Huawei / Honor
- Disable PowerGenie: `pm disable-user com.huawei.powergenie`
- Open autostart settings (intent): `am start -n com.huawei.systemmanager/.startupmgr.ui.StartupNormalAppListActivity`

### Xiaomi (MIUI)
- Disable MIUI optimization: `settings put global miui_optimization 0`
- Open autostart: `am start -n com.miui.securitycenter/com.miui.permcenter.autostart.AutoStartManagementActivity`

### Oppo / Realme
- Disable aggressive timers (example): `settings put global device_idle_constants inactive_to=9999999,sensing_to=9999999`

### Generic commands
- Set locale (may need reboot): `setprop persist.sys.locale ru-RU; setprop ctl.restart zygote`
- Enable adb over TCP: `setprop service.adb.tcp.port 5555; stop adbd; start adbd`

## Profiles
- HuaweiFix: disable powergenie, whitelist app, etc.
- XiaomiFix: disable MIUI optimization, grant battery ignore permission.
- UniversalFix: generic disables of app standby and doze.
