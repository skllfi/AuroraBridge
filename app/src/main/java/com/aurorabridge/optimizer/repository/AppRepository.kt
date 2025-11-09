package com.aurorabridge.optimizer.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.aurorabridge.optimizer.model.AppCategory
import com.aurorabridge.optimizer.model.AppItem
import com.aurorabridge.optimizer.services.BloatwareApps
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getInstalledApps(): List<AppItem> {
        val packageManager = context.packageManager
        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        return apps.mapNotNull { appInfo ->
            try {
                val packageInfo = packageManager.getPackageInfo(appInfo.packageName, PackageManager.GET_PERMISSIONS)
                val permissions = packageInfo.requestedPermissions?.toList() ?: emptyList()

                val category = classifyApp(appInfo)
                AppItem(
                    name = appInfo.loadLabel(packageManager).toString(),
                    packageName = appInfo.packageName,
                    icon = appInfo.loadIcon(packageManager),
                    category = category,
                    permissions = permissions
                )
            } catch (e: PackageManager.NameNotFoundException) {
                null
            }
        }
    }

    fun getApp(packageName: String): AppItem? {
        val packageManager = context.packageManager
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val permissions = packageInfo.requestedPermissions?.toList() ?: emptyList()
            val category = classifyApp(appInfo)

            AppItem(
                name = appInfo.loadLabel(packageManager).toString(),
                packageName = appInfo.packageName,
                icon = appInfo.loadIcon(packageManager),
                category = category,
                permissions = permissions
            )
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun classifyApp(appInfo: ApplicationInfo): AppCategory {
        return when {
            BloatwareApps.packageNames.contains(appInfo.packageName) -> AppCategory.BLOATWARE
            (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0 -> AppCategory.SYSTEM
            else -> AppCategory.USER
        }
    }
}
