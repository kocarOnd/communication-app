package cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * The JSON payload the Android client sends to the server when a user makes a choice.
 */
public record NvcScenarioUserAttemptRequestDto(
        @NotBlank(message = "Device ID is required to track the attempt")
        String deviceId,

        @NotNull(message = "Scenario ID is required")
        Long scenarioId,

        @NotNull(message = "Selected Option ID is required")
        Long selectedOptionId
) {}