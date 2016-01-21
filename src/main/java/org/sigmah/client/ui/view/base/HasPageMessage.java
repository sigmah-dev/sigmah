package org.sigmah.client.ui.view.base;

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
