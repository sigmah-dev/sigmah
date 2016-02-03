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


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.zone.Zone;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;

/**
 * Displays messages, notifications.<br/>
 * <br/>
 * N10N: Notifications.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public final class N10N {

	private N10N() {
		// Provides only static methods.
	}

	// CSS styles names
	private static final String CSS_MESSAGE = "notif-message";
	private static final String CSS_FIRST = "notif-first";
	public static final String CSS_LIST = "notif-list";
	
	/**
	 * Utility method to create a map with the given args.
	 * 
	 * @param html
	 *          the key.
	 * @param items
	 *          the value.
	 * @return the map.
	 */
	private static <C extends Collection<String>> Map<String, C> map(final String html, final C items) {
		final HashMap<String, C> map = new HashMap<String, C>();
		map.put(html, items);
		return map;
	}

	/**
	 * Utility method to build a HTML message from the given map.
	 * 
	 * @param items
	 *          The message map.
	 * @return The HTML message.
	 */
	private static <C extends Collection<String>> String buildHTML(final Map<String, C> items) {

		final StringBuilder sb = new StringBuilder();

		if (items != null && !items.isEmpty()) {
			boolean first = true;
			for (final Map.Entry<String, C> item : items.entrySet()) {

				sb.append("<div class=\"").append(CSS_MESSAGE);
				if (first) {
					sb.append(" ").append(CSS_FIRST);
				}
				sb.append("\">");

				final boolean hasItems = ClientUtils.isNotEmpty(item.getValue());
				if (hasItems) {
					sb.append("<span class=\"").append(CSS_LIST).append("\">");
				} else {
					sb.append("<span>");
				}
				sb.append(item.getKey());
				sb.append("</span>");

				if (hasItems) {
					sb.append("<ul class=\"").append(CSS_LIST).append("\">");

					for (final String s : item.getValue()) {
						if (item == null) {
							continue;
						}
						sb.append("<li>");
						sb.append(s);
						sb.append("</li>");
					}

					sb.append("</ul>");
				}

				sb.append("</div>");

				first = false;

			}
		}

		return sb.toString();

	}

	// --
	// -- MESSAGES.
	// --

	// Error.

	public static void error(final String html) {
		error(null, html);
	}

	public static void error(final String title, final String html) {
		error(title, html, null);
	}

	public static void error(final String html, final Collection<String> items) {
		error(null, html, items);
	}

	public static void error(final String title, final String html, final Collection<String> items) {
		error(title, map(html, items));
	}

	public static <C extends Collection<String>> void error(final Map<String, C> items) {
		error(null, items);
	}

	public static <C extends Collection<String>> void error(final String title, final Map<String, C> items) {
		message(title, items, MessageType.ERROR);
	}

	// Warn.

	public static void warn(final String html) {
		warn(null, html);
	}

	public static void warn(final String title, final String html) {
		warn(title, html, null);
	}

	public static void warn(final String html, final Collection<String> items) {
		warn(null, html, items);
	}

	public static void warn(final String title, final String html, final Collection<String> items) {
		warn(title, map(html, items));
	}

	public static <C extends Collection<String>> void warn(final Map<String, C> items) {
		warn(null, items);
	}

	public static <C extends Collection<String>> void warn(final String title, final Map<String, C> items) {
		message(title, items, MessageType.WARNING);
	}

	// Info.

	public static void info(final String html) {
		info(null, html);
	}

	public static void info(final String title, final String html) {
		info(title, html, null);
	}

	public static void info(final String html, final Collection<String> items) {
		info(null, html, items);
	}

	public static void info(final String title, final String html, final Collection<String> items) {
		info(title, map(html, items));
	}

	public static <C extends Collection<String>> void info(final Map<String, C> items) {
		info(null, items);
	}

	public static <C extends Collection<String>> void info(final String title, final Map<String, C> items) {
		message(title, items, MessageType.INFO);
	}

	// Valid.

	public static void valid(final String html) {
		valid(null, html);
	}

	public static void valid(final String title, final String html) {
		valid(title, html, null);
	}

	public static void valid(final String html, final Collection<String> items) {
		valid(null, html, items);
	}

	public static void valid(final String title, final String html, final Collection<String> items) {
		valid(title, map(html, items));
	}

	public static <C extends Collection<String>> void valid(final Map<String, C> items) {
		valid(null, items);
	}

	public static <C extends Collection<String>> void valid(final String title, final Map<String, C> items) {
		message(title, items, MessageType.VALID);
	}

	// Generic.

	public static void message(final String html, final MessageType type) {
		message(null, html, type);
	}

	public static void message(final String title, final String html, final MessageType type) {
		message(title, html, null, type);
	}

	public static void message(final String html, final Collection<String> items, final MessageType type) {
		message(null, html, items, type);
	}

	public static void message(final String title, final String html, final Collection<String> items, final MessageType type) {
		message(title, map(html, items), type);
	}

	public static <C extends Collection<String>> void message(final Map<String, C> items, final MessageType type) {
		message(null, items, type);
	}

	public static <C extends Collection<String>> void message(final String title, final Map<String, C> items, final MessageType type) {
		Messages.show(title, buildHTML(items), (type != null ? type : MessageType.DEFAULT));
	}

	// --
	// -- NOTIFICATIONS.
	// --

	// Error.

	public static void errorNotif(final String html) {
		errorNotif(null, html);
	}

	public static void errorNotif(final String title, final String html) {
		errorNotif(title, html, null);
	}

	public static void errorNotif(final String html, final Collection<String> items) {
		errorNotif(null, html, items);
	}

	public static void errorNotif(final String title, final String html, final Collection<String> items) {
		errorNotif(title, map(html, items));
	}

	public static <C extends Collection<String>> void errorNotif(final Map<String, C> items) {
		errorNotif(null, items);
	}

	public static <C extends Collection<String>> void errorNotif(final String title, final Map<String, C> items) {
		notification(title, items, MessageType.ERROR);
	}

	// Warn.

	public static void warnNotif(final String html) {
		warnNotif(null, html);
	}

	public static void warnNotif(final String title, final String html) {
		warnNotif(title, html, null);
	}

	public static void warnNotif(final String html, final Collection<String> items) {
		warnNotif(null, html, items);
	}

	public static void warnNotif(final String title, final String html, final Collection<String> items) {
		warnNotif(title, map(html, items));
	}

	public static <C extends Collection<String>> void warnNotif(final Map<String, C> items) {
		warnNotif(null, items);
	}

	public static <C extends Collection<String>> void warnNotif(final String title, final Map<String, C> items) {
		notification(title, items, MessageType.WARNING);
	}

	// Info.

	public static void infoNotif(final String html) {
		infoNotif(null, html);
	}

	public static void infoNotif(final String title, final String html) {
		infoNotif(title, html, null);
	}

	public static void infoNotif(final String html, final Collection<String> items) {
		infoNotif(null, html, items);
	}

	public static void infoNotif(final String title, final String html, final Collection<String> items) {
		infoNotif(title, map(html, items));
	}

	public static <C extends Collection<String>> void infoNotif(final Map<String, C> items) {
		infoNotif(null, items);
	}

	public static <C extends Collection<String>> void infoNotif(final String title, final Map<String, C> items) {
		notification(title, items, MessageType.INFO);
	}

	// Valid.

	public static void validNotif(final String html) {
		validNotif(null, html);
	}

	public static void validNotif(final String title, final String html) {
		validNotif(title, html, null);
	}

	public static void validNotif(final String html, final Collection<String> items) {
		validNotif(null, html, items);
	}

	public static void validNotif(final String title, final String html, final Collection<String> items) {
		validNotif(title, map(html, items));
	}

	public static <C extends Collection<String>> void validNotif(final Map<String, C> items) {
		validNotif(null, items);
	}

	public static <C extends Collection<String>> void validNotif(final String title, final Map<String, C> items) {
		notification(title, items, MessageType.VALID);
	}
	
	// Offline.
	
	public static void offlineNotif(final String title, final String html) {
		offlineNotif(title, html, null, null);
	}
	
	public static void offlineNotif(final String title, final String html, EventBus eventBus) {
		offlineNotif(title, html, null, eventBus);
	}
	
	public static void offlineNotif(final String title, final String html, final Collection<String> items, EventBus eventBus) {
		offlineNotif(title, map(html, items), eventBus);
	}
	
	public static <C extends Collection<String>> void offlineNotif(final String title, final Map<String, C> items, EventBus eventBus) {
		notification(title, items, MessageType.OFFLINE);
		if(eventBus != null) {
			eventBus.updateZoneRequest(Zone.OFFLINE_BANNER.requestWith(RequestParameter.SHOW_BRIEFLY, true));
		}
	}

	// Generic.

	public static void notification(final String html, final MessageType type) {
		notification(null, html, type);
	}

	public static void notification(final String title, final String html, final MessageType type) {
		notification(title, html, null, type);
	}

	public static void notification(final String html, final Collection<String> items, final MessageType type) {
		notification(null, html, items, type);
	}

	public static void notification(final String title, final String html, final Collection<String> items, final MessageType type) {
		notification(title, map(html, items), type);
	}

	public static <C extends Collection<String>> void notification(final Map<String, C> items, final MessageType type) {
		notification(null, items, type);
	}

	public static <C extends Collection<String>> void notification(final String title, final Map<String, C> items, final MessageType type) {
		Notifications.show(title, buildHTML(items), (type != null ? type : MessageType.DEFAULT));
	}

	// --
	// -- CONFIRMATIONS.
	// --

	// Question.

	public static void confirmation(final String html, ConfirmCallback yesCallback) {
		confirmation(null, html, yesCallback);
	}

	public static void confirmation(final String title, final String html, ConfirmCallback yesCallback) {
		confirmation(title, html, yesCallback, null);
	}

	public static void confirmation(final String html, final ConfirmCallback yesCallback, ConfirmCallback noCallback) {
		confirmation(null, html, yesCallback, noCallback);
	}

	public static void confirmation(final String title, final String html, ConfirmCallback yesCallback, ConfirmCallback noCallback) {
		confirmation(title, html, null, yesCallback, noCallback);
	}

	public static void confirmation(final String html, final Collection<String> items, ConfirmCallback yesCallback) {
		confirmation(null, html, items, yesCallback);
	}

	public static void confirmation(final String title, final String html, final Collection<String> items, ConfirmCallback yesCallback) {
		confirmation(title, html, items, yesCallback, null);
	}

	public static void confirmation(final String html, final Collection<String> items, ConfirmCallback yesCallback, ConfirmCallback noCallback) {
		confirmation(null, html, items, yesCallback, noCallback);
	}

	public static void confirmation(final String title, final String html, final Collection<String> items, ConfirmCallback yesCallback, ConfirmCallback noCallback) {
		confirmation(title, map(html, items), yesCallback, noCallback);
	}

	public static <C extends Collection<String>> void confirmation(final Map<String, C> items, ConfirmCallback yesCallback) {
		confirmation(null, items, yesCallback);
	}

	public static <C extends Collection<String>> void confirmation(final String title, final Map<String, C> items, ConfirmCallback yesCallback) {
		confirmation(title, items, yesCallback, null);
	}

	public static <C extends Collection<String>> void confirmation(final Map<String, C> items, ConfirmCallback yesCallback, ConfirmCallback noCallback) {
		confirmation(null, items, yesCallback, noCallback);
	}

	public static <C extends Collection<String>> void confirmation(final String title, final Map<String, C> items, ConfirmCallback yesCallback,
			ConfirmCallback noCallback) {
		Confirm.show(title, buildHTML(items), yesCallback, noCallback);
	}

}
