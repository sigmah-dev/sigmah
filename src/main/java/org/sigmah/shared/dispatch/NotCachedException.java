package org.sigmah.shared.dispatch;

/**
 * Thrown when the user tries to access offline a resource that was not cached in
 * the local database.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class NotCachedException extends CommandException {

	public NotCachedException() {
	}

	public NotCachedException(String message) {
		super(message);
	}
	
}
