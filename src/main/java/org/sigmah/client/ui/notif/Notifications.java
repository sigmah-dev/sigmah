package org.sigmah.client.ui.notif;

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

import org.sigmah.client.ui.widget.popup.Info;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;

/**
 * Displays messages into a notification tray.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
final class Notifications {

	private Notifications() {
		// Provides only static methods.
	}

	/**
	 * Shows the given message into a new notification widget. The notifications widget are graphically stacked.
	 * 
	 * @param title
	 *          The title.
	 * @param html
	 *          The message.
	 * @param type
	 *          The message's type.
	 */
	static void show(final String title, final String html, MessageType type) {

		final Info info = new Info(ClientUtils.isNotBlank(title) ? title : MessageType.getTitle(type), html);
		info.show(type);

	}

}
