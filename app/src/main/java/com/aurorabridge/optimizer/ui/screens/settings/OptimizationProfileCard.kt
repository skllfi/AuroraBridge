package com.aurorabridge.optimizer.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.optimizer.OptimizationProfile

@Composable
fun OptimizationProfileCard(
    profile: OptimizationProfile,
    onRunOptimization: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = "Info")
                Spacer(modifier = Modifier.padding(start = 16.dp))
                Text(
                    text = stringResource(R.string.settings_detected_brand, profile.brandName),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = stringResource(id = profile.description))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRunOptimization, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.settings_run_optimization, profile.brandName))
            }
        }
    }
}
