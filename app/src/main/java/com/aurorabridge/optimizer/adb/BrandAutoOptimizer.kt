package com.aurorabridge.optimizer.adb

import android.content.Context
import android.content.Intent
import android.content.ComponentName
import android.provider.Settings
import com.aurorabridge.optimizer.utils.BrandDetectionUtils
import java.util.Locale

abstract class BaseOptimizer {
    abstract val brandName: String
    abstract fun openBatteryOptimization(context: Context)
    abstract fun openAutoStartSettings(context: Context)
    abstract fun openNotificationSettings(context: Context)
}

class HonorOptimizer : BaseOptimizer() {
    override val brandName = "honor"
    override fun openBatteryOptimization(context: Context) {
        try {
            val intent = Intent()
            intent.setClassName("com.huawei.systemmanager","com.huawei.systemmanager.optimize.process.ProtectActivity")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
    override fun openAutoStartSettings(context: Context) {
        try {
            val intent = Intent()
            intent.component = ComponentName("com.huawei.systemmanager","com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
    override fun openNotificationSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}

class XiaomiOptimizer : BaseOptimizer() {
    override val brandName = "xiaomi"
    override fun openBatteryOptimization(context: Context) {
        try {
            val intent = Intent()
            intent.setClassName("com.miui.powerkeeper","com.miui.powerkeeper.ui.HiddenAppsContainerManagementActivity")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
    override fun openAutoStartSettings(context: Context) {
        try {
            val intent = Intent()
            intent.component = ComponentName("com.miui.securitycenter","com.miui.permcenter.autostart.AutoStartManagementActivity")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
    override fun openNotificationSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}

class SamsungOptimizer : BaseOptimizer() {
    override val brandName = "samsung"
    override fun openBatteryOptimization(context: Context) {
        try {
            val intent = Intent()
            intent.component = ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun openAutoStartSettings(context: Context) {
        // Samsung does not have a direct autostart manager, it's managed by battery settings
    }

    override fun openNotificationSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}

class OnePlusOptimizer : BaseOptimizer() {
    override val brandName = "oneplus"
    override fun openBatteryOptimization(context: Context) {
        try {
            val intent = Intent()
            intent.component = ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }

    override fun openAutoStartSettings(context: Context) {
        // OnePlus autostart is also tied to battery optimization
    }

    override fun openNotificationSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}

object BrandOptimizerManager {
    fun getOptimizerFor(manufacturer: String): BaseOptimizer? {
        val m = manufacturer.lowercase(Locale.getDefault())
        return when {
            m.contains("huawei") || m.contains("honor") -> HonorOptimizer()
            m.contains("xiaomi") || m.contains("redmi") -> XiaomiOptimizer()
            m.contains("samsung") -> SamsungOptimizer()
            m.contains("oneplus") -> OnePlusOptimizer()
            else -> null
        }
    }

    fun runBrandOptimization(context: Context) {
        val manufacturer = BrandDetectionUtils.getManufacturer()
        val opt = getOptimizerFor(manufacturer)
        if (opt != null) {
            opt.openBatteryOptimization(context)
            opt.openAutoStartSettings(context)
            opt.openNotificationSettings(context)
        } else {
            // fallback: open general settings
            context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}
