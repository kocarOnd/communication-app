package cz.cuni.mff.kocaro.comm_app.comm_app_backend.exception;

/**
 * Thrown when the client submits an attempt with an option ID that is missing from the database.
 */
public class ScenarioOptionNotFoundException extends RuntimeException {
    public ScenarioOptionNotFoundException(String message) {
        super(message);
    }
}