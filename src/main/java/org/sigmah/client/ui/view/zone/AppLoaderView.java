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

import org.sigmah.client.ui.presenter.zone.AppLoaderPresenter;
import org.sigmah.client.ui.view.base.AbstractView;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Application loader view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class AppLoaderView extends AbstractView implements AppLoaderPresenter.View {

	// CSS.
	private static final String CSS_LOADING = "loading";

	private Panel loaderPanel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		loaderPanel = new SimplePanel();
		loaderPanel.getElement().setId("app-loader");

	}

	@Override
	public void onViewRevealed() {
		// Nothing to do here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLoading(boolean loading) {
		if (loading) {
			loaderPanel.addStyleName(CSS_LOADING);
		} else {
			loaderPanel.removeStyleName(CSS_LOADING);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel getLoaderPanel() {
		return loaderPanel;
	}

}
