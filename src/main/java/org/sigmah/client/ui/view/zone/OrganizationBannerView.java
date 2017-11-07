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

import org.sigmah.client.ui.presenter.zone.OrganizationBannerPresenter;
import org.sigmah.client.ui.view.base.AbstractView;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Organization banner view (not a real view, just a widgets set).
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class OrganizationBannerView extends AbstractView implements OrganizationBannerPresenter.View {

	private Panel logoPanel;
	private Panel namePanel;
	private HTML nameLabel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		logoPanel = new SimplePanel();
		logoPanel.getElement().setId("orglogo");

		namePanel = new HorizontalPanel();
		namePanel.getElement().setId("orgname");

		nameLabel = new HTML();
		namePanel.add(nameLabel);

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
	public Panel getLogoPanel() {
		return logoPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel getNamePanel() {
		return namePanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasHTML getNameLabel() {
		return nameLabel;
	}

}
