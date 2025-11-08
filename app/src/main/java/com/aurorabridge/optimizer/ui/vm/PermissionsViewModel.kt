package com.aurorabridge.optimizer.ui.vm

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel

class PermissionsViewModel(private val context: Context) : ViewModel() {

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 101
    }

    fun arePermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            PERMISSIONS_REQUEST_CODE
        )
    }
}
