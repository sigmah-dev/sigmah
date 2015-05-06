package org.sigmah.offline.dispatch;

/**
 * Indicates that a command is unavailable in the current state
 * (usually when the user is offline).
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class UnavailableCommandException extends RuntimeException {

    public UnavailableCommandException() {
    }

    public UnavailableCommandException(String message) {
        super(message);
    }
}
