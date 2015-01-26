package org.sigmah.shared.dto.element.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * A GWT event that cannot be killed.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <H>
 *          Event handler type.
 */
abstract class GWTImmortalEvent<H extends EventHandler> extends GwtEvent<H> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void kill() {
		// nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void revive() {
		// nothing.
	}

}
