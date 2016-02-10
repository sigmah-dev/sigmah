package org.sigmah.client.ui.view.zone;

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

import org.sigmah.client.ui.presenter.zone.MessageBannerPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * MEssage banner view (not a real view, just a widgets set).
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class MessageBannerView extends AbstractView implements MessageBannerPresenter.View {

	// CSS.
	public static final String CSS_PANEL = "message-panel";
	public static final String CSS_MESSAGE = "message";

	private Panel messagePanel;
	private HTML messageLabel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		messagePanel = new SimplePanel();
		messagePanel.addStyleName(CSS_PANEL);
		messagePanel.setVisible(false);

		messageLabel = new HTML();
		messageLabel.addStyleName(CSS_MESSAGE);
		messagePanel.add(messageLabel);

		// initWidget(); Useless.

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onViewRevealed() {
		// Nothing to do here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel getMessagePanel() {
		return messagePanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMessage(String html, MessageType type) {

		messagePanel.setVisible(ClientUtils.isNotBlank(html));
		messageLabel.setHTML(html);

		MessageType.applyStyleName(messagePanel, type);

	}

}
