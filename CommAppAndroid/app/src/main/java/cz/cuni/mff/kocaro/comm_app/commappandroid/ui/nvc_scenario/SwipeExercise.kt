package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcOptionUiModel
import kotlinx.coroutines.launch

/**
 * Renders a list of swipeable cards with labels
 *
 * **Architectural Contract:**
 *  * **Spatial:** This component aggressively consumes all available space and should be placed
 *  inside a container.
 *  * **State:** Relies on [ScenarioUiState.Active] to evaluate swipe card rendering
 *  * **Delegation:** Card progressions are strictly delegated through [onSwipe].
 */
@Composable
fun SwipeExercise(
    state: ScenarioUiState.Active,
    onSwipe: (Long, Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.scenario.contextDescription,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // The physics container
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val options = state.remainingOptions

            if (options.isEmpty()) {
                Text("Evaluating results...")
            } else {
                // Reversing iteration to render the active card on top
                options.asReversed().forEachIndexed { reversedIndex, option ->
                    val isTopCard = reversedIndex == options.lastIndex

                    key(option.id) {
                        SwipeableCard(
                            option = option,
                            isTopCard = isTopCard,
                            onSwiped = { isSelected -> onSwipe(option.id, isSelected) }
                        )
                    }
                }
            }
        }

        // Footer with instructions
        Text(
            text = "Swipe Right to Select • Swipe Left to Ignore",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

/**
 * Helper function that renders a card with a given NVC option UI model
 */
@Composable
private fun SwipeableCard(
    option: NvcOptionUiModel,
    isTopCard: Boolean,
    onSwiped: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    // Making the top card slightly bigger
    val scale = if (isTopCard) 1f else 0.95f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .graphicsLayer {
                translationX = offset.value.x
                translationY = offset.value.y
                rotationZ = offset.value.x / 20f // Kinetic angular momentum
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (isTopCard) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                coroutineScope.launch {
                                    val escapeThreshold = 300f
                                    // remove the card upon reaching a threshold or snap back
                                    if (offset.value.x > escapeThreshold) {
                                        offset.animateTo(
                                            Offset(1500f, offset.value.y),
                                            tween(300)
                                        )
                                        onSwiped(true)
                                    } else if (offset.value.x < -escapeThreshold) {
                                        offset.animateTo(
                                            Offset(-1500f, offset.value.y),
                                            tween(300)
                                        )
                                        onSwiped(false)
                                    } else {
                                        offset.animateTo(
                                            Offset.Zero,
                                            tween(300)
                                        )
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                coroutineScope.launch {
                                    offset.snapTo(offset.value + dragAmount)
                                }
                            }
                        )
                    }
                } else Modifier
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isTopCard) 8.dp else 2.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Text(
                text = option.text,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}