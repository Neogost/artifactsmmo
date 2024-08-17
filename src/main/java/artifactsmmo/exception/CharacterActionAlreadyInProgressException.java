package artifactsmmo.exception;

public class CharacterActionAlreadyInProgressException extends RuntimeException {

    /**
     * Constructs a new {@code CharacterNotFoundException} with {@code null} as its detail message.
     */
    public CharacterActionAlreadyInProgressException() {
        super();
    }

    /**
     * Constructs a new {@code CharacterNotFoundException} with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval
     *                by the {@link #getMessage()} method.
     */
    public CharacterActionAlreadyInProgressException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code CharacterNotFoundException} with the specified detail message
     * and cause.
     *
     * @param message the detail message. The detail message is saved for later retrieval
     *                by the {@link #getMessage()} method.
     * @param cause   the cause of the exception. The cause is saved for later retrieval
     *                by the {@link #getCause()} method.
     */
    public CharacterActionAlreadyInProgressException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new {@code CharacterNotFoundException} with the specified cause.
     *
     * @param cause the cause of the exception. The cause is saved for later retrieval
     *              by the {@link #getCause()} method.
     */
    public CharacterActionAlreadyInProgressException(Throwable cause) {
        super(cause);
    }
}
