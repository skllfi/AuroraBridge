package com.aurorabridge.optimizer.utils

import com.google.gson.Gson
import java.io.File

object LogUtils {
    fun saveReport(basePath: String, report: Any) {
        try {
            val gson = Gson()
            val json = gson.toJson(report)
            val f = File(basePath, "diagnostics.json")
            f.parentFile?.mkdirs()
            f.writeText(json)
        } catch (e: Exception) {
            // ignore
        }
    }
}
