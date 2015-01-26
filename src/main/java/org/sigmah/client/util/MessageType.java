package org.sigmah.client.util;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.conf.PropertyName;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.UIObject;

/**
 * Message types.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public enum MessageType {

	ERROR,
	WARNING,
	INFO,
	VALID,
	QUESTION,
	OFFLINE;

	/**
	 * The default type.
	 */
	public static final MessageType DEFAULT = INFO;

	private String asStyleName() {
		return ClientUtils.toLowerCase(name());
	}

	/**
	 * Does the same work than the {@link #valueOf(String)} method but handles <code>null</code> and
	 * {@link IllegalArgumentException}.
	 * 
	 * @param type
	 *          The string.
	 * @return The {@link MessageType} or <code>null</code>.
	 */
	public static MessageType fromString(final String type) {

		try {

			return valueOf(ClientUtils.toUpperCase(type));

		} catch (NullPointerException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		}

	}

	/**
	 * Applies the style name for the given type (and remove old previous style names).
	 * 
	 * @param ui
	 *          The UI object.
	 * @param type
	 *          The type.
	 */
	public static void applyStyleName(final UIObject ui, final MessageType type) {

		if (!GWT.isClient()) {
			return;
		}

		for (final MessageType t : MessageType.values()) {
			ui.removeStyleName(t.asStyleName());
		}

		if (type != null) {
			ui.addStyleName(type.asStyleName());
		}

	}

	/**
	 * Returns the given {@code messageType} corresponding i18n title.<br/>
	 * This method should be executed from client-side. If executed from server-side, it returns an error value.
	 * 
	 * @param messageType
	 *          The message type.
	 * @return the given {@code messageType} corresponding i18n title, or {@code empty} if {@code messageType} is
	 *         {@code null}.
	 */
	public static final String getTitle(final MessageType messageType) {

		if (messageType == null) {
			return "";
		}

		if (!GWT.isClient()) {
			return PropertyName.error(messageType.name());
		}

		switch (messageType) {
			case ERROR:
				return I18N.CONSTANTS.message_error_defaultTitle();
			case WARNING:
				return I18N.CONSTANTS.message_warning_defaultTitle();
			case INFO:
				return I18N.CONSTANTS.message_info_defaultTitle();
			case VALID:
				return I18N.CONSTANTS.message_valid_defaultTitle();
			case QUESTION:
				return I18N.CONSTANTS.message_question_defaultTitle();
			case OFFLINE:
				return I18N.CONSTANTS.offlineModeHeader();
			default:
				return PropertyName.error(messageType.name());
		}

	}

}
