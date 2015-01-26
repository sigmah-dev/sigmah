package org.sigmah.shared.dto.element.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * {@link ValueEvent} handler.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ValueHandler extends EventHandler {

	void onValueChange(ValueEvent event);

}
