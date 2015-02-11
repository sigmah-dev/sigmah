package org.sigmah.client.event.handler;

import com.google.gwt.event.shared.EventHandler;
import org.sigmah.client.event.OfflineEvent;

/**
 * Handler of {@link org.sigmah.client.event.handler.OfflineEvent}.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface OfflineHandler extends EventHandler {
	void handleEvent(OfflineEvent event);
}
