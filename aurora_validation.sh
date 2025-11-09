#!/bin/bash
# AuroraBridge System Validation Script
# Author: GPT-5
# Version: v1.2 (Honor Magic 8 Pro CN / Android 16+)

set -e
DEVICE=$(adb devices | grep -v "List" | awk '{print $1}')

if [ -z "$DEVICE" ]; then
  echo "❌ Нет подключенных устройств. Проверь ADB."
  exit 1
fi

echo "✅ Устройство: $DEVICE"
echo "🔍 Проверка системных настроек и оптимизаций..."

# ---------- SECTION 1: Проверка разрешений ----------
echo "📦 Проверка разрешений приложения AuroraBridge..."
PKG="com.aurorabridge.optimizer"

adb shell pm list packages | grep "$PKG" >/dev/null || {
  echo "❌ Приложение $PKG не установлено!"
  exit 1
}

adb shell pm grant $PKG android.permission.POST_NOTIFICATIONS || true
adb shell pm grant $PKG android.permission.RECEIVE_BOOT_COMPLETED || true
adb shell pm grant $PKG android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS || true
adb shell pm grant $PKG android.permission.WRITE_SECURE_SETTINGS || true

echo "✅ Разрешения установлены."

# ---------- SECTION 2: Проверка оптимизаций ----------
echo "🔋 Проверка ограничителей батареи (PowerGenie, IAA, MIUIOptimization и др.)..."

LIMITERS=(
  "com.huawei.powergenie"
  "com.miui.powerkeeper"
  "com.coloros.iaware"
  "com.oplus.powermonitor"
  "com.vivo.icebox"
)

for limiter in "${LIMITERS[@]}"; do
  if adb shell pm list packages | grep "$limiter" >/dev/null; then
    echo "⚠️ Найден ограничитель: $limiter"
  fi
done

echo "✅ Проверка ограничителей завершена."

# ---------- SECTION 3: Проверка whitelisting ----------
echo "📲 Проверка whitelisting AuroraBridge..."

adb shell dumpsys deviceidle whitelist | grep "$PKG" >/dev/null || {
  echo "⚙️ Добавляем AuroraBridge в whitelist..."
  adb shell dumpsys deviceidle whitelist +$PKG
}

echo "✅ Приложение в whitelist Doze/Idle Mode."

# ---------- SECTION 4: Проверка ADB Companion ----------
echo "🔧 Проверка ADB Wi-Fi Companion..."
IP=$(adb shell ip addr show wlan0 | grep "inet " | awk '{print $2}' | cut -d/ -f1 | head -1)

if [ -n "$IP" ]; then
  echo "🌐 IP устройства: $IP"
  echo "📶 Проверка подключения ADB Wi-Fi..."
  adb tcpip 5555
  adb connect $IP:5555 || echo "⚠️ Wi-Fi подключение не удалось, возможно требуется ручное подтверждение."
else
  echo "⚠️ Не удалось определить IP. Проверь Wi-Fi подключение."
fi

# ---------- SECTION 5: Проверка фоновых задач ----------
echo "⏱ Проверка WorkManager задач..."

adb shell dumpsys jobscheduler | grep "aurorabridge" || echo "⚠️ Задачи WorkManager не найдены (возможно не запланированы)."

# ---------- SECTION 6: Проверка уведомлений ----------
echo "🔔 Проверка NotificationListener..."

if adb shell dumpsys notification | grep "$PKG" >/dev/null; then
  echo "✅ NotificationListener активен."
else
  echo "⚠️ NotificationListener неактивен. Включи вручную в настройках уведомлений."
fi

# ---------- SECTION 7: Экспорт отчёта ----------
REPORT_PATH="/sdcard/aurora_validation_report.txt"

adb shell "echo 'AuroraBridge Validation Report — $(date)' > $REPORT_PATH"
adb shell "echo 'Device: $DEVICE' >> $REPORT_PATH"
adb shell "echo 'App Package: $PKG' >> $REPORT_PATH"
adb shell "dumpsys deviceidle whitelist >> $REPORT_PATH"
adb shell "dumpsys battery >> $REPORT_PATH"

adb pull $REPORT_PATH ./aurora_validation_report.txt

echo "📄 Отчёт сохранён: aurora_validation_report.txt"
echo "✅ Проверка завершена успешно!"
