package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario

import androidx.compose.foundation.BorderStroke
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
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.theme.calculateCardColor

@Composable
fun SwipeExerciseSummary(
    state: ScenarioUiState.Active,
    onNextClicked: () -> Unit
) {
    // Filter specifically for the REQUEST options that were swiped
    val requestOptions = state.scenario.options.filter { it.phase == NvcPhase.REQUEST }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Review Your Requests",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(requestOptions) { option ->
                val isSelected = state.selectedOptionIds.contains(option.id)
                // Assuming calculateCardColor is now a globally accessible top-level function
                val cardColor = calculateCardColor(
                    isSelected = isSelected,
                    isCorrect = option.isCorrect,
                    isEvaluated = true
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Explicit kinetic label
                        Text(
                            text = if (isSelected) "You Swiped Right (Selected)" else "You Swiped Left (Ignored)",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(text = option.text, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))

                        option.feedback?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = onNextClicked,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Full Report")
                }
            }
        }
    }
}