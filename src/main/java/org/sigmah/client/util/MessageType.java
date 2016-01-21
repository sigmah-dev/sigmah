package org.sigmah.client.util;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
