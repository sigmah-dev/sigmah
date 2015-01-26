package org.sigmah.shared.command.result;

import org.sigmah.shared.command.SecureNavigationCommand;

/**
 * Result returned by {@link SecureNavigationCommand}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SecureNavigationResult implements Result {

	private boolean granted;
	private Authentication authentication;

	public SecureNavigationResult() {
		// Serialization.
	}

	public SecureNavigationResult(final boolean granted, final Authentication authentication) {
		this.granted = granted;
		this.authentication = authentication;
	}

	public boolean isGranted() {
		return granted;
	}

	public void setGranted(boolean granted) {
		this.granted = granted;
	}
	
	public Authentication getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}
	
}
