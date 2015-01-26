package org.sigmah.server.domain.util;

import org.sigmah.server.domain.base.EntityId;

/**
 * Determines if an element can history of its values.
 * 
 * @author tmi
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public interface Historable {

	/**
	 * If the element manages an history.
	 * 
	 * @return If the element manages an history.
	 */
	boolean isHistorable();

	/**
	 * Transforms a input value as an historable value.
	 * 
	 * @param value
	 *          The actual value.
	 * @return The historable value.
	 */
	String asHistoryToken(String value);

	/**
	 * Transforms a input value as an historable value.
	 * 
	 * @param value
	 *          The actual value.
	 * @return The historable value.
	 */
	String asHistoryToken(EntityId<?> value);

}
