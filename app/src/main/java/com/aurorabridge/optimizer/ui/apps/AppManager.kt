package com.aurorabridge.optimizer.ui.apps

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.aurorabridge.optimizer.adb.AdbHelper

data class AppInfo(val name: String, val packageName: String, val icon: Int, val isSystemApp: Boolean)

enum class AppFilter {
    ALL,
    USER_ONLY,
    SYSTEM_ONLY
}

class AppManager(private val context: Context) {

    fun getInstalledApps(filter: AppFilter = AppFilter.ALL): List<AppInfo> {
        val pm = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val appList = mutableListOf<AppInfo>()

        for (app in packages) {
            val isSystemApp = (app.flags and ApplicationInfo.FLAG_SYSTEM) != 0

            val shouldAdd = when (filter) {
                AppFilter.ALL -> true
                AppFilter.USER_ONLY -> !isSystemApp
                AppFilter.SYSTEM_ONLY -> isSystemApp
            }

            if (shouldAdd) {
                val appName = app.loadLabel(pm).toString()
                val packageName = app.packageName
                val appIcon = app.icon
                appList.add(AppInfo(appName, packageName, appIcon, isSystemApp))
            }
        }

        return appList.sortedBy { it.name }
    }

    fun uninstallApps(packageNames: List<String>): String {
        val results = packageNames.map { "pm uninstall $it" }
        return AdbHelper.runCommands(results).joinToString("\n")
    }

    fun disableApps(packageNames: List<String>): String {
        val results = packageNames.map { "pm disable-user --user 0 $it" }
        return AdbHelper.runCommands(results).joinToString("\n")
    }

    fun enableApps(packageNames: List<String>): String {
        val results = packageNames.map { "pm enable $it" }
        return AdbHelper.runCommands(results).joinToString("\n")
    }
}
