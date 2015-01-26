package org.sigmah.shared.dto.element.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * {@link RequiredValueEvent} handler.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface RequiredValueHandler extends EventHandler {

	void onRequiredValueChange(RequiredValueEvent event);

}
