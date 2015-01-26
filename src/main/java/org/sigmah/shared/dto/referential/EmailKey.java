package org.sigmah.shared.dto.referential;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A key which is used by the mail service to replace strings before sending.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public interface EmailKey extends IsSerializable {

	/**
	 * Gets the key.
	 * 
	 * @return The key.
	 */
	String getKey();

	/**
	 * Returns if this parameter can be stored in the date layer or not.
	 * 
	 * @return If this parameter can be stored in the date layer or not.
	 */
	boolean isSafe();

}
