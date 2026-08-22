package cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * The JSON payload the client sends to the server when a user makes a choice.
 */
public record NvcScenarioUserAttemptRequestDto(
        @NotBlank(message = "Device ID is required to track the attempt")
        String deviceId,

        @NotNull(message = "Scenario ID is required")
        Long scenarioId,

        @NotEmpty(message = "At least one Selected Option ID is required")
        List<Long> selectedOptionIds
) {}