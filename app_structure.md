# Aurora Bridge v3 — Структура приложения для Gemini

## 1️⃣ Визуальная схема экранов и связей

```mermaid
flowchart TD
    Home[HomeScreen] -->|Навигация| Diagnostics[DiagnosticsScreen]
    Home -->|Навигация| ADB[ADB Companion Screen]
    Home -->|Навигация| AppList[App List Screen]
    Home -->|Навигация| Settings[Settings Screen]

    Diagnostics -->|Запустить анализ| AdbAnalyzer[AdbAnalyzer Module]
    Diagnostics -->|Экспорт отчета| LogUtils[LogUtils]

    ADB -->|Включить/Выключить ADB Wi-Fi| AdbConnectionManager[AdbConnectionManager]
    ADB -->|Запуск профиля| AdbProfiles[ADB Profiles Module]

    AppList -->|Открыть настройки| SystemSettings[System Settings Intent]
    AppList -->|Проверка ограничений| AdbAnalyzer

    Settings -->|Выбор языка| SettingsViewModel[SettingsViewModel]
    Settings -->|Экспорт логов| LogUtils
    Settings -->|Brand Auto-Optimize| BrandOptimize[Brand Auto-Optimize Intent]

    AppMonitorWorker[WorkManager / AppMonitorWorker] -->|Проверка приложений| Diagnostics
    AppMonitorWorker -->|Проверка приложений| AppList
2️⃣ Экранная структура
2.1 HomeScreen
Центральная точка навигации на все экраны.

Кнопки: Diagnostics, ADB Companion, App List, Settings.

Отображает краткую информацию о состоянии оптимизации.

2.2 DiagnosticsScreen
Кнопка “Запустить анализ”.

Список активных ограничителей (PowerGenie, MIUIOptimization и т.д.).

Кнопка “Экспорт отчёта” (JSON).

ViewModel: DiagnosticsViewModel

runAnalysis() → сбор ограничителей через AdbAnalyzer.

exportReport() → сохранение JSON.

2.3 ADB Companion Screen
Кнопки “Включить/Выключить ADB Wi-Fi”.

Список профилей оптимизации по бренду.

Кнопка “Запустить выбранный профиль”.

ViewModel: AdbViewModel

enableAdbWifi()

runProfile(brand) → выполняет команды ADB через AdbProfiles.

2.4 App List Screen
Список всех приложений с индикаторами ограничений.

Кнопка “Открыть настройки приложения”.

ViewModel: AppListViewModel

getInstalledApps()

openAppSettings(appPackage)

2.5 Settings Screen
Выбор языка (RU / EN).

Настройка автозапуска и фоновой работы.

Экспорт логов.

Переход к Brand Auto-Optimize.

ViewModel: SettingsViewModel

setLanguage(lang)

exportLogs()

3️⃣ Фоновые задачи
AppMonitorWorker (WorkManager) запускается каждые 12 часов.

Проверяет новые и старые приложения на ограничения.

Обновляет данные DiagnosticsScreen и AppListScreen.

4️⃣ Модули и компоненты
Модуль	Назначение
AdbAnalyzer	Сбор информации об активных ограничителях
AdbOptimizer	Выполнение команд оптимизации
AdbProfiles	Набор готовых команд для брендов
AdbConnectionManager	Управление ADB Wi-Fi
BatteryOptimizationUtils	Автоматизация настройки батареи
LogUtils	Сохранение и экспорт логов / отчетов
AppMonitorWorker	Периодические проверки приложений

5️⃣ Навигация (NavHost)
kotlin
Копировать код
NavHost(
    navController = navController,
    startDestination = "home"
) {
    composable("home") { HomeScreen(navController) }
    composable("diagnostics") { DiagnosticsScreen() }
    composable("adb_companion") { AdbCompanionScreen() }
    composable("app_list") { AppListScreen() }
    composable("settings") { SettingsScreen() }
}
6️⃣ Использование ADB
Все команды выполняются через AdbExecutor внутри ViewModel.

Профили брендов содержат:

Разрешение автозапуска

Выключение ограничений фоновой активности

Оптимизация батареи

7️⃣ Логика для Gemini
Gemini должен понимать:

Все экраны и кнопки.

Связь ViewModel и UI (StateFlow / LiveData).

Навигацию через NavHost.

Фоновые задачи через WorkManager.

Экспорт логов и отчётов локально.

Интеграцию модулей ADB, анализа и Brand Auto-Optimize.

С такой структурой Gemini сможет полностью сгенерировать проект на Jetpack Compose с навигацией и рабочими ViewModel.