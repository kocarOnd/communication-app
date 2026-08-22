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
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioOptionDto
import kotlinx.coroutines.launch

@Composable
fun SwipeExercise(
    state: ScenarioUiState.Active,
    onSwipe: (Long, Boolean) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The static context header
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
                // Reverse iteration to render the active card on top
                options.asReversed().forEachIndexed { reversedIndex, option ->
                    // The actual index relative to the top of the deck
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

        // Instructional footer
        Text(
            text = "Swipe Right to Select • Swipe Left to Ignore",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
private fun SwipeableCard(
    option: NvcScenarioOptionDto,
    isTopCard: Boolean,
    onSwiped: (Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    // Aesthetic structural values based on position in the stack
    val scale = if (isTopCard) 1f else 0.95f
    val alpha = if (isTopCard) 1f else 0.8f

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
                this.alpha = alpha
            }
            .then(
                if (isTopCard) {
                    Modifier.pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                coroutineScope.launch {
                                    val escapeThreshold = 300f
                                    if (offset.value.x > escapeThreshold) {
                                        // Eject Right
                                        offset.animateTo(Offset(1500f, offset.value.y), tween(300))
                                        onSwiped(true)
                                    } else if (offset.value.x < -escapeThreshold) {
                                        // Eject Left
                                        offset.animateTo(Offset(-1500f, offset.value.y), tween(300))
                                        onSwiped(false)
                                    } else {
                                        // Snap back to center
                                        offset.animateTo(Offset.Zero, tween(300))
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