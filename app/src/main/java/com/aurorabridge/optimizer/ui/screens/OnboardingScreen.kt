package com.aurorabridge.optimizer.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.ui.onboarding.OnboardingUiState
import com.aurorabridge.optimizer.ui.vm.OnboardingViewModel

@Composable
fun OnboardingScreen(navController: NavController, viewModel: OnboardingViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(id = R.string.onboarding_welcome_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.onboarding_welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        InstructionsCard()

        Spacer(modifier = Modifier.height(32.dp))

        AnimatedContent(targetState = uiState, label = "OnboardingStateAnimation") { state ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                when (state) {
                    is OnboardingUiState.Idle -> {
                        Button(onClick = { viewModel.checkAdbConnection() }) {
                            Icon(Icons.Default.Link, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Check ADB Connection")
                        }
                    }
                    is OnboardingUiState.Checking -> {
                        CircularProgressIndicator()
                        Text("Checking connection...", modifier = Modifier.padding(top = 8.dp))
                    }
                    is OnboardingUiState.Success -> {
                        SuccessState(navController, viewModel)
                    }
                    is OnboardingUiState.Failure -> {
                        FailureState(message = state.message, onRetry = { viewModel.resetState() })
                    }
                }
            }
        }
    }
}

@Composable
fun InstructionsCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("How to Enable Wireless Debugging:", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("1. Go to Settings > About Phone > Build Number.")
            Text("2. Tap 'Build Number' 7 times to enable Developer Options.")
            Text("3. Go back to Settings > System > Developer Options.")
            Text("4. Enable 'Wireless debugging' and allow it on your network.")
            Text("5. Your phone and this device must be on the same Wi-Fi network.")
        }
    }
}

@Composable
fun SuccessState(navController: NavController, viewModel: OnboardingViewModel) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Color.Green, modifier = Modifier.height(48.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Connection Successful!", style = MaterialTheme.typography.headlineSmall, color = Color.Green)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            viewModel.setOnboardingComplete()
            navController.navigate("permissions") { popUpTo("onboarding") { inclusive = true } }
        }) {
            Icon(Icons.Default.ArrowForward, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continue")
        }
    }
}

@Composable
fun FailureState(message: String, onRetry: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.Default.Error, contentDescription = "Failure", tint = MaterialTheme.colorScheme.error, modifier = Modifier.height(48.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Connection Failed", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onRetry) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}
