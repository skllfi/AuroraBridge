
{ pkgs, ... }: {
  # Канал Nix-пакетов, определяющий доступные версии.
  channel = "stable-24.05"; 

  # Пакеты, необходимые для Android-разработки.
  packages = [
    pkgs.android-tools  # Включает ADB (Android Debug Bridge).
    pkgs.openjdk17      # Java Development Kit, необходимый для Gradle.
  ];

  # Расширения VS Code для улучшения опыта разработки на Kotlin.
  idx.extensions = [
    "fwcd.kotlin"
    "mtxr.sqltools"
    "vscjava.vscode-java-pack"
  ];

  # Хуки жизненного цикла рабочего пространства.
  idx.workspace = {
    # Команды, которые выполняются при первом создании рабочего пространства.
    onCreate = {
      # Делаем скрипт сборки Gradle исполняемым.
      make-gradle-executable = "chmod +x ./gradlew";
    };

    # Команды, которые выполняются при каждом запуске рабочего пространства.
    onStart = {};
  };
}
