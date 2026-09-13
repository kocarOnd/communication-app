package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.mapper

import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcPhase
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioOptionDto
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcScenarioResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NvcScenarioMapperTest {

    @Test
    fun `toUiModel correctly maps raw DTO to stable UI Model`() {
        val mockOptionDto = NvcScenarioOptionDto(
            id = 1L,
            phase = NvcPhase.OBSERVATION,
            text = "Test observation",
            isCorrect = true,
            feedback = "Good"
        )
        val mockScenarioDto = NvcScenarioResponseDto(
            id = 99L,
            title = "Test Scenario",
            contextDescription = "A specific context",
            options = listOf(mockOptionDto)
        )

        val uiModel = mockScenarioDto.toUiModel()

        assertEquals(99L, uiModel.id)
        assertEquals("Test Scenario", uiModel.title)
        assertEquals(1, uiModel.options.size)

        val optionUiModel = uiModel.options.first()
        assertEquals(1L, optionUiModel.id)
        assertEquals(NvcPhase.OBSERVATION, optionUiModel.phase)
        assertTrue(optionUiModel.isCorrect)
    }
}