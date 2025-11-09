package com.aurorabridge.optimizer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aurorabridge.optimizer.R
import com.aurorabridge.optimizer.model.OptimizationCategory
import com.aurorabridge.optimizer.model.OptimizationCommand
import com.aurorabridge.optimizer.ui.vm.OptimizationWizardViewModel
import com.aurorabridge.optimizer.ui.vm.WizardStep

@Composable
fun OptimizationWizardScreen(viewModel: OptimizationWizardViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.optimization_wizard_title)) }) }
    ) {
        if (uiState.isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(uiState.error!!, color = MaterialTheme.colorScheme.error)
            }
        } else {
            when (uiState.currentStep) {
                WizardStep.Welcome -> WelcomeStep(viewModel)
                WizardStep.CategorySelection -> CategorySelectionStep(viewModel)
                WizardStep.CommandSelection -> CommandSelectionStep(viewModel)
                WizardStep.Confirmation -> ConfirmationStep(viewModel)
                WizardStep.Execution -> ExecutionStep(viewModel)
                WizardStep.Finished -> FinishedStep(viewModel)
            }
        }
    }
}

@Composable
fun WelcomeStep(viewModel: OptimizationWizardViewModel) {
    val profile = viewModel.uiState.collectAsState().value.profile!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to the Optimization Wizard!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This wizard will guide you through applying the '${profile.name}' profile.",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = profile.description,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(onClick = { viewModel.nextStep() }) {
                Text("Next")
            }
        }
    }
}

@Composable
fun CategorySelectionStep(viewModel: OptimizationWizardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.profile!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Categories",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(profile.categories) { category ->
                val isSelected = uiState.selectedCommands.containsAll(category.commands)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { viewModel.toggleCategorySelection(category, it) }
                    )
                    Column {
                        Text(text = category.name, style = MaterialTheme.typography.bodyLarge)
                        Text(text = category.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { viewModel.previousStep() }) {
                Text("Previous")
            }
            Button(onClick = { viewModel.nextStep() }) {
                Text("Next")
            }
        }
    }
}

@Composable
fun CommandSelectionStep(viewModel: OptimizationWizardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.profile!!
    val commands = profile.categories.flatMap { it.commands }.filter { 
        val category = profile.categories.find { cat -> cat.commands.contains(it) }!!
        val categoryCommands = uiState.selectedCommands.intersect(category.commands.toSet())
        categoryCommands.isNotEmpty()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Select Commands",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(commands) { command ->
                val isSelected = uiState.selectedCommands.contains(command)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { viewModel.toggleCommandSelection(command) }
                    )
                    Column {
                        Text(text = command.name, style = MaterialTheme.typography.bodyLarge)
                        Text(text = command.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { viewModel.previousStep() }) {
                Text("Previous")
            }
            Button(onClick = { viewModel.nextStep() }) {
                Text("Next")
            }
        }
    }
}

@Composable
fun ConfirmationStep(viewModel: OptimizationWizardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedCommands = uiState.selectedCommands.toList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Confirm Selections",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text("The following commands will be executed:")
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(selectedCommands) { command ->
                Text("• ${command.name}")
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = { viewModel.previousStep() }) {
                Text("Previous")
            }
            Button(onClick = { viewModel.nextStep() }) {
                Text("Execute")
            }
        }
    }
}

@Composable
fun ExecutionStep(viewModel: OptimizationWizardViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.executionLog) {
        listState.animateScrollToItem(uiState.executionLog.size)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Executing Optimizations",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
            items(uiState.executionLog) { log ->
                Text(log)
            }
        }
        if (!uiState.isExecuting) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = { viewModel.nextStep() }) {
                    Text("Finished")
                }
            }
        }
    }
}

@Composable
fun FinishedStep(viewModel: OptimizationWizardViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "All Optimizations Applied!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { viewModel.resetWizard() }) {
            Text("Back to Home")
        }
    }
}
