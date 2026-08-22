package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcPhase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScenarioFullReport(
    state: ScenarioUiState.Active,
    onFinishClicked: () -> Unit
) {
    // Isolate only the actionable phases to prevent rendering empty summary blocks
    val evaluablePhases = listOf(
        NvcPhase.OBSERVATION,
        NvcPhase.FEELING,
        NvcPhase.NEED,
        NvcPhase.REQUEST
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scenario Complete") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    Text(
                        text = "Final Performance Breakdown",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(evaluablePhases) { phase ->
                    val phaseOptions = state.scenario.options.filter { it.phase == phase }

                    // 1. Calculate the strict mathematical outcome
                    val correctSelected = phaseOptions.count {
                        state.sessionSelectedOptionIds.contains(it.id) && it.isCorrect
                    }
                    val incorrectSelected = phaseOptions.count {
                        state.sessionSelectedOptionIds.contains(it.id) && !it.isCorrect
                    }
                    val correctMissed = phaseOptions.count {
                        !state.sessionSelectedOptionIds.contains(it.id) && it.isCorrect
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = phase.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Divider(modifier = Modifier.padding(vertical = 8.dp))

                            // 2. Render the analytical matrix
                            MetricRow(label = "Correctly Selected:", count = correctSelected, color = Color(0xFF2E7D32)) // Green
                            MetricRow(label = "Incorrectly Selected:", count = incorrectSelected, color = Color(0xFFC62828)) // Red
                            MetricRow(label = "Correct Options Missed:", count = correctMissed, color = Color(0xFFEF6C00)) // Orange
                        }
                    }
                }
            }

            // 3. The Terminal Action
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = onFinishClicked,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Finish Exercise")
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, count: Int, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}