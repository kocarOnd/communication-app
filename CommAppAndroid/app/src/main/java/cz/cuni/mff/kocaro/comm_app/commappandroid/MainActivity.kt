package cz.cuni.mff.kocaro.comm_app.commappandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.MultiSelectExercise
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.NvcScenarioViewModel
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.NvcUiEvent
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.ScenarioUiState
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.navigation.NvcScenarioExerciseRoute
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.ScenarioFullReport
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.SwipeExercise
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.SwipeExerciseSummary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val viewModel: NvcScenarioViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.uiEvent.collect { event ->
                    when (event) {
                        is NvcUiEvent.Navigate -> navController.navigate(event.route) {
                            popUpTo(NvcScenarioExerciseRoute.MultiSelectPhase.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            }

            // The Root Routing Engine
            NavHost(
                navController = navController,
                startDestination = NvcScenarioExerciseRoute.Loading.route
            ) {
                // --- EXERCISE DOMAIN (Wrapped in Scaffold) ---
                composable(NvcScenarioExerciseRoute.Loading.route) {
                    ExerciseScaffold(uiState = uiState) { innerPadding ->
                        Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                            Text("Loading Scenario from Spring Boot...")
                        }
                    }
                }

                composable(NvcScenarioExerciseRoute.MultiSelectPhase.route) {
                    ExerciseScaffold(uiState = uiState) { innerPadding ->
                        if (uiState is ScenarioUiState.Active) {
                            Box(modifier = Modifier.padding(innerPadding)) {
                                MultiSelectExercise(
                                    state = uiState as ScenarioUiState.Active,
                                    onOptionToggled = { viewModel.toggleOptionSelection(it) },
                                    onSubmitClicked = { viewModel.submitPhase() },
                                    onNextClicked = { viewModel.advanceToNextPhase() }
                                )
                            }
                        }
                    }
                }

                composable(NvcScenarioExerciseRoute.SwipePhase.route) {
                    ExerciseScaffold(uiState = uiState) { innerPadding ->
                        if (uiState is ScenarioUiState.Active) {
                            Box(modifier = Modifier.padding(innerPadding)) {
                                SwipeExercise(
                                    state = uiState as ScenarioUiState.Active,
                                    onSwipe = { optionId, isSelected -> viewModel.recordSwipe(optionId, isSelected) }
                                )
                            }
                        }
                    }
                }

                composable(NvcScenarioExerciseRoute.SwipeSummary.route) {
                    ExerciseScaffold(uiState = uiState) { innerPadding ->
                        Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                            // Temporary placeholder for the Swipe Evaluation UI
                            SwipeExerciseSummary(
                                state = uiState as ScenarioUiState.Active,
                                onNextClicked = { viewModel.advanceToNextPhase() }
                            )
                        }
                    }
                }

                composable(NvcScenarioExerciseRoute.FullReport.route) {
                    if (uiState is ScenarioUiState.Active) {
                        val activeState = uiState as ScenarioUiState.Active
                        ScenarioFullReport (
                            state = activeState,
                            onFinishClicked = {
                                // TODO: Define the exit strategy
                            }
                        )
                    }
                }
            }
        }
    }
}

// Reusable structural wrapper that enforces the visual boundary of the exercise
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseScaffold(
    uiState: ScenarioUiState,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val titleText = if (uiState is ScenarioUiState.Active) {
                        "Phase: ${uiState.currentPhase.name}"
                    } else {
                        "NVC Exercise"
                    }
                    Text(titleText)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        content(padding)
    }
}