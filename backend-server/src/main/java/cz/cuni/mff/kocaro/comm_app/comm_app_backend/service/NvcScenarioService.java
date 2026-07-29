package cz.cuni.mff.kocaro.comm_app.comm_app_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcScenario;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcScenarioOption;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcScenarioUserAttempt;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto.NvcScenarioOptionDto;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto.NvcScenarioResponseDto;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto.NvcScenarioUserAttemptRequestDto;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.exception.ScenarioNotFoundException;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.exception.ScenarioOptionNotFoundException;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.repository.NvcScenarioOptionRepository;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.repository.NvcScenarioRepository;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.repository.NvcScenarioUserAttemptRepository;

@Service
public class NvcScenarioService {

    private final NvcScenarioRepository scenarioRepository;
    private final NvcScenarioOptionRepository optionRepository;
    private final NvcScenarioUserAttemptRepository attemptRepository;

    public NvcScenarioService(NvcScenarioRepository scenarioRepository,
                              NvcScenarioOptionRepository optionRepository,
                              NvcScenarioUserAttemptRepository attemptRepository) {
        this.scenarioRepository = scenarioRepository;
        this.optionRepository = optionRepository;
        this.attemptRepository = attemptRepository;
    }

    /**
     * Retrieves a random scenario and safely packages it into a DTO.
     */
    @Transactional(readOnly = true)
    public NvcScenarioResponseDto getRandomScenario() {
        NvcScenario scenario = scenarioRepository.findRandomScenario()
                .orElseThrow(() -> new ScenarioNotFoundException("No scenarios currently available in the database."));

        // Map the child options into DTOs
        List<NvcScenarioOptionDto> optionDtos = scenario.getOptions().stream()
                .map(opt -> new NvcScenarioOptionDto(
                        opt.getId(),
                        opt.getPhase(),
                        opt.getText(),
                        opt.isCorrect(),
                        opt.getFeedback()
                ))
                .collect(Collectors.toList());

        return new NvcScenarioResponseDto(
                scenario.getId(),
                scenario.getTitle(),
                scenario.getContextDescription(),
                optionDtos
        );
    }

    /**
     * Records a user's choice in the append-only event log.
     */
    @Transactional
    public void processUserAttempt(NvcScenarioUserAttemptRequestDto requestDto) {
        NvcScenario scenario = scenarioRepository.findById(requestDto.scenarioId())
                .orElseThrow(() -> new ScenarioNotFoundException("Scenario ID " + requestDto.scenarioId() + " not found."));

        NvcScenarioOption selectedOption = optionRepository.findById(requestDto.selectedOptionId())
                .orElseThrow(() -> new ScenarioOptionNotFoundException("Option ID " + requestDto.selectedOptionId() + " not found."));

        // Ensure selected option actually belongs to this scenario
        if (!selectedOption.getScenario().getId().equals(scenario.getId())) {
            throw new IllegalArgumentException("The selected option does not belong to the provided scenario.");
        }

        NvcScenarioUserAttempt attempt = new NvcScenarioUserAttempt();
        attempt.setDeviceId(requestDto.deviceId());
        attempt.setScenario(scenario);
        attempt.setSelectedOption(selectedOption);
        
        attempt.setPhase(selectedOption.getPhase());
        attempt.setWasCorrect(selectedOption.isCorrect());
        attempt.setAttemptedAt(LocalDateTime.now());

        attemptRepository.save(attempt);
    }
}