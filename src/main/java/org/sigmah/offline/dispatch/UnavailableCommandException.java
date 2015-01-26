package org.sigmah.offline.dispatch;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class UnavailableCommandException extends Exception {

    public UnavailableCommandException() {
    }

    public UnavailableCommandException(String message) {
        super(message);
    }
}
