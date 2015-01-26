package org.sigmah.shared.dto.history;

import com.google.gwt.user.client.ui.Widget;

/**
 * Represents a element which can render history tokens.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface HistoryTokenManager {

	/**
	 * Gets the history manager name.
	 * 
	 * @return The history manager name.
	 */
	String getElementLabel();

	/**
	 * Renders a history token.
	 * 
	 * @param token
	 *          The token
	 * @return The rendered token (can be a {@link Widget}).
	 */
	Object renderHistoryToken(HistoryTokenListDTO token);

}
