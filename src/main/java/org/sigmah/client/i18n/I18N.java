package org.sigmah.client.i18n;

import com.google.gwt.core.client.GWT;

/**
 * Contains global instances of UIConstants and UIMessages.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public final class I18N {

	private I18N() {
	}

	public static final UIConstants CONSTANTS = (UIConstants) GWT.create(UIConstants.class);
	public static final UIMessages MESSAGES = (UIMessages) GWT.create(UIMessages.class);

}
