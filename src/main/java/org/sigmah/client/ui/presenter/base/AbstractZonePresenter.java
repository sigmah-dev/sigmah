package org.sigmah.client.ui.presenter.base;

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
import org.sigmah.client.ui.presenter.base.Presenter.ZonePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.event.ZoneRequestEvent;
import org.sigmah.client.ui.zone.handler.ZoneRequestHandler;

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.Inject;

/**
 * Abstract zone presenter.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 * @param <V>
 *          view interface extending the {@link ViewInterface} interface
 */
public abstract class AbstractZonePresenter<V extends ViewInterface> extends AbstractPresenter<V> implements ZonePresenter<V> {

	/**
	 * Default abstract zone presenter constructor.<br>
	 * Executes {@link #bind()} method in order to register the {@link ZoneRequestEvent} to the current presenter.
	 * 
	 * @param view
	 *          View interface associated to the zone presenter.
	 * @param injector
	 *          Application injector.
	 */
	@Inject
	public AbstractZonePresenter(final V view, final Injector injector) {
		super(view, injector); // Executes 'bind()' method.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final public void bind() {

		view.initialize();
		onBind();

		final Zone zone = getZone();

		if (zone == null) {
			return;
		}

		// Registers ZoneRequestEvent listener.
		registerHandler(eventBus.addHandler(ZoneRequestEvent.getType(), new ZoneRequestHandler() {

			@Override
			public void onZoneRequest(final ZoneRequestEvent event) {

				if (!event.concern(zone)) {
					return;
				}

				if (Log.isTraceEnabled()) {
					Log.trace("Executing '" + zone + "' onZoneRequest() method.");
				}

				AbstractZonePresenter.this.onZoneRequest(event.getZoneRequest());
			}
		}));
	}

}
