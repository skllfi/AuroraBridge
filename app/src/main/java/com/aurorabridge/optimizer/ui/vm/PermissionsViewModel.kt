package com.aurorabridge.optimizer.ui.vm

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel

class PermissionsViewModel(application: Application) : AndroidViewModel(application) {

    fun arePermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            getApplication(),
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
