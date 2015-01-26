package org.sigmah.offline.dispatch;

import org.sigmah.shared.command.result.Authentication;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OfflineExecutionContext {
	private final Authentication authentication;

	public OfflineExecutionContext(Authentication authentication) {
		this.authentication = authentication;
	}

	public Authentication getAuthentication() {
		return authentication;
	}
}
