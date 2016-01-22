package org.sigmah.client.ui.presenter.zone;

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

import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractZonePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.zone.AppLoaderView;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;
import org.sigmah.client.util.ClientUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Application loader presenter displaying loading animation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class AppLoaderPresenter extends AbstractZonePresenter<AppLoaderPresenter.View> {

	/**
	 * View interface.
	 */
	@ImplementedBy(AppLoaderView.class)
	public static interface View extends ViewInterface {

		Panel getLoaderPanel();

		/**
		 * Sets the application loader state.
		 * 
		 * @param loading
		 *          {@code true} to enable application loader.
		 */
		void setLoading(boolean loading);

	}

	@Inject
	public AppLoaderPresenter(View view, Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Zone getZone() {
		return Zone.APP_LOADER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onZoneRequest(ZoneRequest zoneRequest) {

		final Boolean loading = zoneRequest.getData(RequestParameter.CONTENT);

		if (Log.isTraceEnabled()) {
			Log.trace("Application loader update request with loading value '" + loading + "'.");
		}

		view.setLoading(ClientUtils.isTrue(loading));

	}

}
