# Aurora Bridge v3 — Использование в Firebase Studio Preview

## 1️⃣ Подготовка проекта

1. Открой **Firebase Studio Preview** в браузере.
2. Создай новый проект:
   - Название проекта: `AuroraBridgeDeviceOptimizer`.
   - Тип: Android / Kotlin.
   - Минимальный SDK: 33 (Android 13).
   - Включи **Jetpack Compose**.
3. Дождись завершения создания проекта.

---

## 2️⃣ Импорт исходного кода

1. В Firebase Studio → **File Explorer** → `Import Project / Upload`:
   - Загрузите распакованный ZIP **Aurora Bridge v3** или всю папку проекта.
2. Структура проекта должна быть следующей:
app/
docs/
build.gradle.kts
settings.gradle.kts

yaml
Копировать код

---

## 3️⃣ Настройка Gradle и зависимостей

1. Открой `app/build.gradle.kts`.
2. Убедись, что есть следующие зависимости:
```kotlin
implementation("androidx.activity:activity-compose:1.8.0")
implementation("androidx.navigation:navigation-compose:2.7.0")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
Для AutoStarter / AppKillerManager (по желанию):

kotlin
Копировать код
repositories {
    maven { url = uri("https://jitpack.io") }
}

implementation("com.github.judemanutd:AutoStarter:1.0.8")
implementation("com.github.gitter-badger:AppKillerManager:1.1.2")
4️⃣ Структура проекта
MainActivity.kt → NavHost + Compose

ui/screens/ → HomeScreen, DiagnosticsScreen, AdbCompanionScreen, AppListScreen, SettingsScreen

viewmodel/ → HomeViewModel, DiagnosticsViewModel, AdbViewModel, AppListViewModel, SettingsViewModel

adb/ → AdbAnalyzer, AdbOptimizer, AdbProfiles, AdbConnectionManager

services/ → AppMonitorWorker

utils/ → BatteryOptimizationUtils, LogUtils

docs/ → adb_commands.md, honor_magic8_cn_test_guide.md

5️⃣ Настройка проекта для сборки
В Firebase Studio → Build Settings:

Compile SDK: 33

Min SDK: 33

Kotlin Version: 1.9+

Jetpack Compose: включен

Gradle Sync → дождись завершения.

6️⃣ Запуск и тестирование
Выбери устройство:

Эмулятор Android 13+ или реальное устройство через ADB.

На физическом устройстве:

Включи USB Debugging.

Разреши WRITE_SECURE_SETTINGS через ADB:

bash
Копировать код
adb shell pm grant com.aurorabridge.optimizer android.permission.WRITE_SECURE_SETTINGS
Run → Run App.

На экране:

HomeScreen → навигация на Diagnostics, ADB Companion, App List, Settings.

Diagnostics → запустить анализ ограничителей и экспорт отчёта.

ADB Companion → включить ADB Wi-Fi, запустить профили.

Settings → выбрать язык, экспортировать логи, открыть автозапуск и батарею.

7️⃣ Периодические проверки
Используется AppMonitorWorker (WorkManager) каждые 12 часов.

Проверяет новые и старые приложения на ограничения.

В Firebase Studio Preview можно вручную запустить worker:

kotlin
Копировать код
WorkManager.getInstance(context).enqueue(workRequest)
8️⃣ Экспорт и логи
DiagnosticsScreen → экспорт JSON отчётов.

SettingsScreen → возможность шэрить логи через email или share intent.

Файлы сохраняются в context.filesDir.

9️⃣ Рекомендации для устройств
microG должен быть установлен для уведомлений и фоновых сервисов.

На Honor Magic 8 Pro CN:

Включи Developer Options → USB Debugging.

Разреши WRITE_SECURE_SETTINGS через ADB.

Используй Brand Auto-Optimizer → откроет нужные системные экраны батареи, автозапуска и фоновых ограничений.

10️⃣ Дополнительно
Для расширения профилей ADB или оптимизаций:

Модули находятся в adb/ и viewmodel/.

Для быстрой отладки можно использовать логи из LogUtils.

11️⃣ Документы
docs/adb_commands.md — справочник ADB команд.

docs/honor_magic8_cn_test_guide.md — пошаговая инструкция тестирования на Honor Magic 8 Pro CN.

yaml
Копировать код
