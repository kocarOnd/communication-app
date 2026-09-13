package cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario

import android.app.Application
import android.provider.Settings
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.NvcScenarioApiService
import cz.cuni.mff.kocaro.comm_app.commappandroid.network.dto.NvcPhase
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcOptionUiModel
import cz.cuni.mff.kocaro.comm_app.commappandroid.ui.nvc_scenario.models.NvcScenarioUiModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class NvcScenarioViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: NvcScenarioViewModel
    private lateinit var mockedSettings: MockedStatic<Settings.Secure>
    private lateinit var mockApiService: NvcScenarioApiService

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockedSettings = mockStatic(Settings.Secure::class.java)
        mockedSettings.`when`<String> {
            Settings.Secure.getString(any(), eq(Settings.Secure.ANDROID_ID))
        }.thenReturn("test_dummy_device_id")

        val mockApplication = mock(Application::class.java)
        mockApiService = mock(NvcScenarioApiService::class.java)

        viewModel = NvcScenarioViewModel(mockApplication)
        viewModel.apiService = mockApiService
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mockedSettings.close()
    }

    @Test
    fun `recordSwipe appends to evaluated list and conditionally adds to selected list`() {
        val dummyOption1 = NvcOptionUiModel(
            id = 202L, phase = NvcPhase.REQUEST, text = "Test 1", isCorrect = true, feedback = null
        )
        val dummyOption2 = NvcOptionUiModel(
            id = 999L, phase = NvcPhase.REQUEST, text = "Test 2", isCorrect = false, feedback = null
        )

        val dummyScenario = NvcScenarioUiModel(
            1L,
            "Title",
            "Context",
            persistentListOf(dummyOption1, dummyOption2)
        )
        setViewModelActiveState(
            viewModel,
            ScenarioUiState.Active(scenario = dummyScenario, currentPhase = NvcPhase.REQUEST)
        )

        viewModel.recordSwipe(optionId = 202L, isSelected = true)

        val stateAfterRightSwipe = viewModel.uiState.value as ScenarioUiState.Active
        assertTrue(stateAfterRightSwipe.evaluatedOptionIds.contains(202L))
        assertTrue(stateAfterRightSwipe.selectedOptionIds.contains(202L))

        viewModel.recordSwipe(optionId = 999L, isSelected = false)

        val stateAfterLeftSwipe = viewModel.uiState.value as ScenarioUiState.Active
        assertTrue(!stateAfterLeftSwipe.selectedOptionIds.contains(999L))
    }

    @Test
    fun `finishExerciseAndExit emits ReturnToMainMenu event`() = runTest {
        val events = mutableListOf<NvcUiEvent>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiEvent.collect { events.add(it) }
        }

        viewModel.finishExerciseAndExit()

        advanceUntilIdle()

        assertEquals(1, events.size)
        assertEquals(NvcUiEvent.ReturnToMainMenu, events.first())

        job.cancel()
    }

    @Test
    fun `toggleOptionSelection adds and removes ID from transient state`() {
        val dummyScenario = NvcScenarioUiModel(1L, "Title", "Context", persistentListOf())

        setViewModelActiveState(viewModel, ScenarioUiState.Active(scenario = dummyScenario))

        viewModel.toggleOptionSelection(101L)
        var currentState = viewModel.uiState.value as ScenarioUiState.Active
        assertTrue(currentState.selectedOptionIds.contains(101L))

        viewModel.toggleOptionSelection(101L)
        currentState = viewModel.uiState.value as ScenarioUiState.Active
        assertTrue(!currentState.selectedOptionIds.contains(101L))
    }

    private fun setViewModelActiveState(viewModel: NvcScenarioViewModel, state: ScenarioUiState.Active) {
        val field = NvcScenarioViewModel::class.java.getDeclaredField("_uiState")
        field.isAccessible = true
        val stateFlow = field.get(viewModel) as kotlinx.coroutines.flow.MutableStateFlow<ScenarioUiState>
        stateFlow.value = state
    }
}