package projekt.controller.actions;

/**
 * An exception that is thrown when an action is illegal.
 * An action is illegal if it cannot be executed for any reason.
 */
public class IllegalActionException extends Exception {
    /**
     * Creates a new illegal action exception.
     *
     * @param message The message of the exception.
     */
    public IllegalActionException(final String message) {
        super(message);
    }

    /**
     * Creates a new illegal action exception.
     *
     * @param message The message of the exception.
     * @param cause   The cause of the exception.
     */
    public IllegalActionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
