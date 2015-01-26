package org.sigmah.server.endpoint.export.sigmah;

/**
 * An error during an export.
 * 
 * @author tmi
 */
public class ExportException extends Exception {

    private static final long serialVersionUID = -1642108069751807844L;

    public ExportException() {
        super();
    }

    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportException(String message) {
        super(message);
    }

    public ExportException(Throwable cause) {
        super(cause);
    }
}
