package org.sigmah.client.ui.notif;

import org.sigmah.client.event.ClosePopupEvent;
import org.sigmah.client.event.handler.ClosePopupHandler;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;

import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Displays messages into a modal popup.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
final class Messages {

	private Messages() {
		// Provides only static methods.
	}

	// CSS.
	private static final String CSS_POPUP = "notification";

	// Initialize the popup widget.
	private static final PopupWidget popup;
	private static boolean visible;
	static {

		popup = new PopupWidget(true);
		popup.setContent(new SimplePanel()); // not used.
		popup.addStyleName(CSS_POPUP);

		visible = false;
		popup.setClosePopupHandler(new ClosePopupHandler() {

			@Override
			public void onClosePopup(ClosePopupEvent event) {
				visible = false;
			}

		});

	}

	/**
	 * Clears the current message.
	 */
	private static void clear() {
		popup.setPageMessage(null);
	}

	/**
	 * Shows the given message into the popup.<br/>
	 * <br/>
	 * There is only one instance of the popup, the previous message may be erased.
	 * 
	 * @param title
	 *          The title.
	 * @param html
	 *          The message.
	 * @param type
	 *          The message's type.
	 */
	static void show(final String title, final String html, MessageType type) {

		clear();

		popup.setTitle(ClientUtils.isNotBlank(title) ? title : MessageType.getTitle(type));
		popup.setPageMessage(html, type);

		if (!visible) {
			popup.center();
			visible = true;
		}

	}

}
