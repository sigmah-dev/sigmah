package org.sigmah.client.ui.presenter.base;

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
