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

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;

/**
 * Displays confirmation message (with yes/no buttons) in a modal popup.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
final class Confirm {

	private Confirm() {
		// Provides only static methods.
	}

	// CSS.
	private static final String CSS_POPUP = "notification";
	private static final String CSS_POPUP2 = "confirmation";
	private static final String CSS_FORM = "form-panel";

	// Initialize the popup widget.
	private static final PopupWidget popup;
	private static boolean visible;
	private static ConfirmCallback yesCallback;
	private static ConfirmCallback noCallback;

	/**
	 * html id of yes button.
	 */
	private final static String YES_BUTTON_ID = "yesButtonId";
	
	static {

		// Buttons.
		final Button yes = new Button(I18N.CONSTANTS.yes());
		yes.setId(YES_BUTTON_ID);
		yes.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				if (yesCallback != null) {
					yesCallback.onAction();
				}
				popup.hide();
				visible = false;
			}

		});
		final Button no = new Button(I18N.CONSTANTS.no());
		no.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				if (noCallback != null) {
					noCallback.onAction();
				}
				popup.hide();
				visible = false;
			}

		});

		// Form panel.
		final FormPanel form = Forms.panel(CSS_FORM);
		form.setPadding(0);
		form.addButton(yes);
		form.addButton(no);

		// Popup.
		popup = new PopupWidget(true, false);
		popup.setContent(form);
		popup.addStyleName(CSS_POPUP);
		popup.addStyleName(CSS_POPUP2);

		visible = false;

	}

	/**
	 * Clears the current message.
	 */
	private static void clear() {
		popup.setPageMessage(null);
	}

	/**
	 * Shows the given message into the popup.
	 *
	 * There is only one instance of the popup, the previous message may be erased.
	 * 
	 * @param title
	 *          The title.
	 * @param html
	 *          The message.
	 * @param yesCallback
	 *          The callback for the yes action.
	 * @param noCallback
	 *          The callback for the no action.
	 */
	static void show(final String title, final String html, ConfirmCallback yesCallback, ConfirmCallback noCallback) {

		clear();

		Confirm.yesCallback = yesCallback;
		Confirm.noCallback = noCallback;

		popup.setTitle(ClientUtils.isNotBlank(title) ? title : MessageType.getTitle(MessageType.QUESTION));
		popup.setPageMessage(html, MessageType.QUESTION);
		
		

		if (!visible) {
			popup.center();
			visible = true;
		}

	}

}
