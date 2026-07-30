package cz.cuni.mff.kocaro.comm_app.comm_app_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto.NvcScenarioResponseDto;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto.NvcScenarioUserAttemptRequestDto;
import cz.cuni.mff.kocaro.comm_app.comm_app_backend.service.NvcScenarioService;
import jakarta.validation.Valid;

/**
 * The HTTP routing layer.
 * This class exposes our internal service logic to the outside world via RESTful endpoints.
 */
@RestController
@RequestMapping("/api/nvc/scenarios")
public class NvcScenarioController {

    private final NvcScenarioService scenarioService;

    public NvcScenarioController(NvcScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    /**
     * Endpoint: GET /api/nvc/scenarios/random
     * Fetches a random NVC scenario and its options.
     */
    @GetMapping("/random")
    public ResponseEntity<NvcScenarioResponseDto> getRandomScenario() {
        NvcScenarioResponseDto response = scenarioService.getRandomScenario();
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint: POST /api/nvc/scenarios/attempt
     * Records a user's choice. 
     */
    @PostMapping("/attempt")
    public ResponseEntity<Void> submitAttempt(@Valid @RequestBody NvcScenarioUserAttemptRequestDto requestDto) {
        scenarioService.processUserAttempt(requestDto);
        
        return ResponseEntity.ok().build();
    }
}
