package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcPhase
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcOptionUiModel
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcScenarioUiModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ScenarioFullReportTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun reportRenders_andFinishButton_triggersCallback() {
        val dummyOption = NvcOptionUiModel(1L, NvcPhase.OBSERVATION, "Observation 1", true, null)
        val dummyScenario = NvcScenarioUiModel(1L, "Title", "Context", persistentListOf(dummyOption))

        val state = ScenarioUiState.Active(
            scenario = dummyScenario,
            sessionSelectedOptionIds = persistentSetOf(1L)
        )

        var finishClicked = false

        composeTestRule.setContent {
            ScenarioFullReport(
                state = state,
                onFinishClicked = { finishClicked = true }
            )
        }

        composeTestRule.onAllNodesWithText("Correctly Selected:").assertCountEquals(4)
        composeTestRule.onAllNodesWithText("Incorrectly Selected:").assertCountEquals(4)

        composeTestRule.onAllNodesWithText("1").assertCountEquals(1)
        composeTestRule.onAllNodesWithText("0").assertCountEquals(11)

        composeTestRule.onNodeWithText("Finish Exercise").performClick()
        assertTrue(finishClicked)
    }
}