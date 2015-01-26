package org.sigmah.client.ui.view.base;

import org.sigmah.client.util.MessageType;

/**
 * Defines a widget which manages a page message.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public interface HasPageMessage {

	/**
	 * Sets the popup message visible state.
	 * 
	 * @param visible
	 *          <code>true</code> to display the message, <code>false</code> otherwise.
	 */
	void setPageMessageVisible(boolean visible);

	/**
	 * Sets the popup page message. If the message id <code>null</code> or empty, it will be hidden. Otherwise it will be
	 * displayed.
	 * 
	 * @param html
	 *          The message content.
	 */
	void setPageMessage(String html);

	/**
	 * Sets the popup page message. If the message id <code>null</code> or empty, it will be hidden. Otherwise it will be
	 * displayed.
	 * 
	 * @param html
	 *          The message content.
	 * @param type
	 *          The message type.
	 */
	void setPageMessage(String html, MessageType type);

	/**
	 * Sets the popup page message type.
	 * 
	 * @param type
	 *          The message type.
	 */
	void setPageMessageType(MessageType type);

}
