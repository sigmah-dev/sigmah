package org.sigmah.shared.dto.referential;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Defines the different email model types
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public enum EmailType implements IsSerializable {

	INVITATION,
	LOST_PASSWORD,
	OFFLINE_SYNC_CONFICT;

	/**
	 * Returns the current {@code EmailType} corresponding property name.
	 * 
	 * @return The current {@code EmailType} corresponding property name.
	 */
	public String getPropertyName() {
		return name().toLowerCase();
	}

}
