package cz.cuni.mff.kocaro.comm_app.comm_app_backend.exception;

/**
 * Thrown when the database has no scenarios, or a requested scenario ID does not exist.
 */
public class ScenarioNotFoundException extends RuntimeException {
    public ScenarioNotFoundException(String message) {
        super(message);
    }
}