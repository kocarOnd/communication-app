package cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto;

import cz.cuni.mff.kocaro.comm_app.comm_app_backend.domain.NvcPhase;

/**
 * Represents a single choice the user can make.
 */
public record NvcScenarioOptionDto(
        Long id,
        NvcPhase phase,
        String text,
        boolean isCorrect,
        String feedback
) {}
