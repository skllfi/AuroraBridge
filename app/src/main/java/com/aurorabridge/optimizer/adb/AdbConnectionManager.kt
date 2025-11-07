package com.aurorabridge.optimizer.adb

import android.util.Log

object AdbConnectionManager {
    private const val TAG = "AdbConn"

    fun isAdbWifiEnabled(): Boolean {
        return try {
            val p = Runtime.getRuntime().exec(arrayOf("sh","-c","getprop service.adb.tcp.port"))
            val out = p.inputStream.bufferedReader().readText().trim()
            out != "-1" && out.isNotEmpty()
        } catch (e: Exception) {
            Log.w(TAG, "isAdbWifiEnabled failed: ${"$"}e")
            false
        }
    }

    fun enableAdbWifi(port: Int = 5555): Boolean {
        return try {
            Runtime.getRuntime().exec(arrayOf("sh","-c","setprop service.adb.tcp.port $port"))
            Runtime.getRuntime().exec(arrayOf("sh","-c","stop adbd"))
            Runtime.getRuntime().exec(arrayOf("sh","-c","start adbd"))
            true
        } catch (e: Exception) {
            Log.e(TAG, "enableAdbWifi failed: ${"$"}e")
            false
        }
    }

    fun disableAdbWifi(): Boolean {
        return try {
            Runtime.getRuntime().exec(arrayOf("sh","-c","setprop service.adb.tcp.port -1"))
            Runtime.getRuntime().exec(arrayOf("sh","-c","stop adbd"))
            Runtime.getRuntime().exec(arrayOf("sh","-c","start adbd"))
            true
        } catch (e: Exception) {
            Log.e(TAG, "disableAdbWifi failed: ${"$"}e")
            false
        }
    }
}
