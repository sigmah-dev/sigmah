package org.sigmah.client.ui.notif;

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
