package cz.cuni.mff.kocaro.comm_app.comm_app_backend.dto;

import java.time.LocalDateTime;

/**
 * A uniform structure for every error the server throws.
 */
public record ApiErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {}