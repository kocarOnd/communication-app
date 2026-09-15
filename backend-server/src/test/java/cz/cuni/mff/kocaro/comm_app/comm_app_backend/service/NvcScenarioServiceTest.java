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

import java.util.ArrayList;
import java.util.List;
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
        when(scenarioRepository.findRandomScenario()).thenReturn(Optional.of(mockScenario));

        NvcScenarioResponseDto result = scenarioService.getRandomScenario();

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Test Scenario", result.title());
        assertEquals(1, result.options().size());
        assertEquals("Test Option", result.options().get(0).text());
    }

    @Test
    void getRandomScenario_WhenDatabaseEmpty_ThrowsException() {
        when(scenarioRepository.findRandomScenario()).thenReturn(Optional.empty());

        assertThrows(ScenarioNotFoundException.class, () -> scenarioService.getRandomScenario());
    }

    @Test
    void processUserAttempt_WithValidData_SavesAttempt() {
        List<Long> ids = new ArrayList<>();
        ids.add(10L);

        NvcScenarioUserAttemptRequestDto request = new NvcScenarioUserAttemptRequestDto("device-123", 1L, ids);
        
        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(mockScenario));
        when(optionRepository.findById(10L)).thenReturn(Optional.of(mockOption));

        scenarioService.processUserAttempt(request);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<NvcScenarioUserAttempt>> captor = ArgumentCaptor.forClass(List.class);
        verify(attemptRepository, times(1)).saveAll(captor.capture());

        List<NvcScenarioUserAttempt> savedAttempts = captor.getValue();
        assertEquals(1, savedAttempts.size());

        NvcScenarioUserAttempt savedAttempt = savedAttempts.get(0);
        assertEquals("device-123", savedAttempt.getDeviceId());
        assertEquals(NvcPhase.OBSERVATION, savedAttempt.getPhase());
        assertTrue(savedAttempt.isWasCorrect());
    }

    @Test
    void processUserAttempt_WithOptionFromWrongScenario_ThrowsException() {
        NvcScenario sneakyScenario = new NvcScenario();
        sneakyScenario.setId(2L);
        
        NvcScenarioOption sneakyOption = new NvcScenarioOption();
        sneakyOption.setId(99L);
        sneakyScenario.addOption(sneakyOption);

        List<Long> ids = new ArrayList<>();
        ids.add(99L);

        NvcScenarioUserAttemptRequestDto request = new NvcScenarioUserAttemptRequestDto("device-123", 1L, ids);

        when(scenarioRepository.findById(1L)).thenReturn(Optional.of(mockScenario));
        when(optionRepository.findById(99L)).thenReturn(Optional.of(sneakyOption));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
                () -> scenarioService.processUserAttempt(request));
                
        assertEquals("The selected option does not belong to the provided scenario.", exception.getMessage());
        
        verify(attemptRepository, never()).save(any());
    }
}