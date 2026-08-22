package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioOptionDto
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.theme.calculateCardColor

@Composable
fun MultiSelectExercise(
    state: ScenarioUiState.Active,
    onOptionToggled: (Long) -> Unit,
    onSubmitClicked: () -> Unit,
    onNextClicked: () -> Unit
) {
    val currentOptions = state.scenario.options.filter { it.phase == state.currentPhase }

    // 1. The Parent Container
    Column(modifier = Modifier.fillMaxSize()) {

        // 2. The Scrolling Viewport (weight = 1f forces it to fill space above the button)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = state.scenario.contextDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            items(currentOptions) { option ->
                val isSelected = state.selectedOptionIds.contains(option.id)
                val cardColor = calculateCardColor(isSelected, option.isCorrect, state.isEvaluated)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = !state.isEvaluated) {
                            onOptionToggled(option.id)
                        },
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    border = BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = option.text, style = MaterialTheme.typography.bodyMedium)

                        if (state.isEvaluated) {
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
        }

        // 3. The Anchored Button Container
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp, // Elevates the button area visually above the scrolling list
            color = MaterialTheme.colorScheme.surface
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                if (!state.isEvaluated) {
                    Button(
                        onClick = onSubmitClicked,
                        modifier = Modifier.fillMaxWidth(),
                        // Disable submit if no options are selected to prevent accidental blank submissions
                        enabled = state.selectedOptionIds.isNotEmpty()
                    ) {
                        Text("Submit Answers")
                    }
                } else {
                    Button(
                        onClick = onNextClicked,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Next Phase")
                    }
                }
            }
        }
    }
}