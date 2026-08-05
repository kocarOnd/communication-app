package cz.cuni.mff.kocaro.comm_app.comm_app_backend.service;

import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcPhase;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcScenario;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcScenarioOption;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcScenarioUserAttempt;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto.NvcScenarioResponseDto;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto.NvcScenarioUserAttemptRequestDto;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.exception.ScenarioNotFoundException;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.repository.NvcScenarioOptionRepository;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.repository.NvcScenarioRepository;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.repository.NvcScenarioUserAttemptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NvcScenarioServiceTest {

    @Mock
    private NvcScenarioRepository scenarioRepository;

    @Mock
    private NvcScenarioOptionRepository optionRepository;

    @Mock
    private NvcScenarioUserAttemptRepository attemptRepository;

    @InjectMocks
    private NvcScenarioService scenarioService;

    private NvcScenario mockScenario;
    private NvcScenarioOption mockOption;

    @BeforeEach
    void setUp() {
        // Create some dummy data to use in our tests
        mockScenario = new NvcScenario();
        mockScenario.setId(1L);
        mockScenario.setTitle("Test Scenario");
        mockScenario.setContextDescription("Test Context");

        mockOption = new NvcScenarioOption();
        mockOption.setId(10L);
        mockOption.setPhase(NvcPhase.OBSERVATION);
        mockOption.setText("Test Option");
        mockOption.setCorrect(true);
        
        mockScenario.addOption(mockOption);
    }

    @Test
    void getRandomScenario_WhenScenarioExists_ReturnsDto() {
        // ARRANGE: Tell the fake repository what to return when called
        when(scenarioRepository.findRandomScenario()).thenReturn(Optional.of(mockScenario));

        // ACT: Call the method we are testing
        NvcScenarioResponseDto result = scenarioService.getRandomScenario();

        // ASSERT: Check that the result is exactly what we expect
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test Scenario", result.title());
        assertEquals(1, result.options().size());
        assertEquals("Test Option", result.options().get(0).text());
    }

    @Test
    void getRandomScenario_WhenDatabaseEmpty_ThrowsException() {
        // ARRANGE: Simulate an empty database
        when(scenarioRepository.findRandomScenario()).thenReturn(Optional.empty());

        // ACT & ASSERT: Verify that calling the method throws our custom exception
        assertThrows(ScenarioNotFoundException.class, () -> scenarioService.getRandomScenario());
    }

    @Test
    void processUserAttempt_WithValidData_SavesAttempt() {
        // ARRANGE
        NvcScenarioUserAttemptRequestDto request = new NvcScenarioUserAttemptRequestDto("device-123", 1L, 10L);
        
        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(mockScenario));
        when(optionRepository.findById(10L)).thenReturn(Optional.of(mockOption));

        // ACT
        scenarioService.processUserAttempt(request);

        // ASSERT: Verify that the attemptRepository.save() method was called exactly onc
        ArgumentCaptor<NvcScenarioUserAttempt> captor = ArgumentCaptor.forClass(NvcScenarioUserAttempt.class);
        verify(attemptRepository, times(1)).save(captor.capture());

        NvcScenarioUserAttempt savedAttempt = captor.getValue();
        assertEquals("device-123", savedAttempt.getDeviceId());
        assertEquals(NvcPhase.OBSERVATION, savedAttempt.getPhase());
        assertTrue(savedAttempt.isWasCorrect());
    }

    @Test
    void processUserAttempt_WithOptionFromWrongScenario_ThrowsException() {
        // ARRANGE: Create a sneaky option that belongs to Scenario 2, not Scenario 1
        NvcScenario sneakyScenario = new NvcScenario();
        sneakyScenario.setId(2L);
        
        NvcScenarioOption sneakyOption = new NvcScenarioOption();
        sneakyOption.setId(99L);
        sneakyScenario.addOption(sneakyOption);

        NvcScenarioUserAttemptRequestDto request = new NvcScenarioUserAttemptRequestDto("device-123", 1L, 99L);

        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(mockScenario));
        when(optionRepository.findById(99L)).thenReturn(Optional.of(sneakyOption));

        // ACT & ASSERT: The service should catch the mismatch and throw an exception
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> scenarioService.processUserAttempt(request));
                
        assertEquals("The selected option does not belong to the provided scenario.", exception.getMessage());
        
        verify(attemptRepository, never()).save(any());
    }
}