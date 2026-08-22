package cz.cuni.mff.kocaro.comm_app.commappandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val viewModel: NvcScenarioViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            // 1. The Channel Observer (Securely scoped outside the UI render logic)
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

            // 2. The Scaffold Container
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            // Dynamically read the state machine's active phase
                            val titleText = if (uiState is ScenarioUiState.Active) {
                                "Phase: ${(uiState as ScenarioUiState.Active).currentPhase.name}"
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
            ) { innerPadding ->

                // 3. The Navigation Engine
                NavHost(
                    navController = navController,
                    startDestination = NvcScenarioExerciseRoute.Loading.route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(NvcScenarioExerciseRoute.Loading.route) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Loading Scenario from Spring Boot...")
                        }
                    }

                    composable(NvcScenarioExerciseRoute.MultiSelectPhase.route) {
                        // 4. Instantiate your actual MultiSelect UI and map the callbacks
                        if (uiState is ScenarioUiState.Active) {
                            val activeState = uiState as ScenarioUiState.Active
                            MultiSelectExercise(
                                state = activeState,
                                onOptionToggled = { optionId -> viewModel.toggleOptionSelection(optionId) },
                                onSubmitClicked = { viewModel.submitPhase() },
                                onNextClicked = { viewModel.advanceToNextPhase() }
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Error: State is not active.")
                            }
                        }
                    }

                    composable(NvcScenarioExerciseRoute.SwipePhase.route) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("The Physics Swipe UI will render here.")
                        }
                    }

                    composable(NvcScenarioExerciseRoute.FeedbackSummary.route) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("The Final Evaluation will render here.")
                        }
                    }
                }
            }
        }
    }
}