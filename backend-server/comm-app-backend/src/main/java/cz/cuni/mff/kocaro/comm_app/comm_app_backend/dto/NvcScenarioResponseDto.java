package cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto;

import java.util.List;

/**
 * The JSON payload sent to the Android client when it requests a scenario.
 */
public record NvcScenarioResponseDto(
        Long id,
        String title,
        String contextDescription,
        List<NvcScenarioOptionDto> options
) {}
