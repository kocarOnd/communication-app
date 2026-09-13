package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcPhase
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcOptionUiModel
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcScenarioUiModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MultiSelectExerciseTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun submitButton_isDisabled_whenNoOptionsSelected() {
        val dummyOption = NvcOptionUiModel(1L, NvcPhase.OBSERVATION, "Look at this", true, null)
        val dummyScenario = NvcScenarioUiModel(1L, "Title", "Context", persistentListOf(dummyOption))
        val emptyState = ScenarioUiState.Active(
            scenario = dummyScenario,
            currentPhase = NvcPhase.OBSERVATION,
            selectedOptionIds = persistentSetOf()
        )

        composeTestRule.setContent {
            MultiSelectExercise(
                state = emptyState,
                onOptionToggled = {},
                onSubmitClicked = {},
                onNextClicked = {}
            )
        }

        composeTestRule.onNodeWithText("Submit Answers").assertIsDisplayed().assertIsNotEnabled()
    }

    @Test
    fun clickingCard_triggersToggledCallback() {
        val dummyOption = NvcOptionUiModel(101L, NvcPhase.OBSERVATION, "Tap Me", true, null)
        val dummyScenario = NvcScenarioUiModel(1L, "Title", "Context", persistentListOf(dummyOption))
        val state = ScenarioUiState.Active(dummyScenario, NvcPhase.OBSERVATION)

        var toggledId: Long? = null

        composeTestRule.setContent {
            MultiSelectExercise(
                state = state,
                onOptionToggled = { toggledId = it },
                onSubmitClicked = {},
                onNextClicked = {}
            )
        }

        composeTestRule.onNodeWithText("Tap Me").performClick()

        assertEquals(101L, toggledId)
    }
}