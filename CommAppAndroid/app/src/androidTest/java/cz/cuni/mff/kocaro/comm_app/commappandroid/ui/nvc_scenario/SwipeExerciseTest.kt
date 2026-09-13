package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcPhase
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcOptionUiModel
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcScenarioUiModel
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SwipeExerciseTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun swipingCardRight_triggersSwipeCallback_withTrue() {
        val dummyOption = NvcOptionUiModel(202L, NvcPhase.REQUEST, "Swipe Me Right", true, null)
        val dummyScenario = NvcScenarioUiModel(1L, "Title", "Context", persistentListOf(dummyOption))
        val state = ScenarioUiState.Active(dummyScenario, NvcPhase.REQUEST)

        var swipedId: Long? = null
        var isSelected: Boolean? = null

        composeTestRule.setContent {
            SwipeExercise(
                state = state,
                onSwipe = { id, selected ->
                    swipedId = id
                    isSelected = selected
                }
            )
        }

        composeTestRule.onNodeWithText("Swipe Me Right").performTouchInput {
            swipe(
                start = center,
                end = center.copy(x = center.x + 1000f),
                durationMillis = 150
            )
        }

        composeTestRule.mainClock.advanceTimeBy(500L)
        composeTestRule.waitForIdle()

        assertEquals(202L, swipedId)
        assertEquals(true, isSelected)
    }

    @Test
    fun swipingCardLeft_triggersSwipeCallback_withFalse() {
        val dummyOption = NvcOptionUiModel(999L, NvcPhase.REQUEST, "Swipe Me Left", true, null)
        val dummyScenario = NvcScenarioUiModel(1L, "Title", "Context", persistentListOf(dummyOption))
        val state = ScenarioUiState.Active(dummyScenario, NvcPhase.REQUEST)

        var swipedId: Long? = null
        var isSelected: Boolean? = null

        composeTestRule.setContent {
            SwipeExercise(
                state = state,
                onSwipe = { id, selected ->
                    swipedId = id
                    isSelected = selected
                }
            )
        }

        composeTestRule.onNodeWithText("Swipe Me Left").performTouchInput {
            swipe(
                start = center,
                end = center.copy(x = center.x - 1000f),
                durationMillis = 150
            )
        }

        composeTestRule.mainClock.advanceTimeBy(500L)
        composeTestRule.waitForIdle()

        assertEquals(999L, swipedId)
        assertEquals(false, isSelected)
    }
}