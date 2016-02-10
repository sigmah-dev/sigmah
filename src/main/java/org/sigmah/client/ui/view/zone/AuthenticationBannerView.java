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


import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.zone.AuthenticationBannerPresenter;
import org.sigmah.client.ui.view.base.AbstractView;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Authentication banner view (not a real view, just a widgets set).
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class AuthenticationBannerView extends AbstractView implements AuthenticationBannerPresenter.View {

	private Panel usernamePanel;
	private HTML usernameLabel;
	private Panel logoutPanel;
	private Anchor logoutHandler;
	private InlineLabel changePasswordHandler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		usernamePanel = new FlowPanel();
		usernamePanel.getElement().setId("username");

		usernameLabel = new HTML();
		usernamePanel.add(usernameLabel);
		
		changePasswordHandler = new InlineLabel(I18N.CONSTANTS.changePassword());
		changePasswordHandler.setVisible(false);
		usernamePanel.add(changePasswordHandler);

		logoutPanel = new SimplePanel();
		logoutPanel.getElement().setId("userlogout");

		logoutHandler = new Anchor(I18N.CONSTANTS.logout());
		logoutPanel.add(logoutHandler);

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
	public Panel getNamePanel() {
		return usernamePanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasHTML getNameLabel() {
		return usernameLabel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel getLogoutPanel() {
		return logoutPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasClickHandlers getLogoutHandler() {
		return logoutHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InlineLabel getChangePasswordHandler() {
		return changePasswordHandler;
	}

}
