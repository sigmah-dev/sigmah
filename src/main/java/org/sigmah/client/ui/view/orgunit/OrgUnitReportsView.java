package org.sigmah.client.ui.view.orgunit;

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

import org.sigmah.client.ui.presenter.orgunit.OrgUnitReportsPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.view.base.ViewInterface;

import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */

@Singleton
public class OrgUnitReportsView extends AbstractView implements OrgUnitReportsPresenter.View {

	/**
	 * The reports presenter's view.
	 */
	// Provided before current view initialization.
	private ViewInterface view;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void provideReportsView(ViewInterface view) {
		this.view = view;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		add(view);
	}

}
