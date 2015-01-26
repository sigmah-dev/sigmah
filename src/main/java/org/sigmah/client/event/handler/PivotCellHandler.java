package org.sigmah.client.event.handler;

import com.google.gwt.event.shared.EventHandler;
import org.sigmah.client.event.PivotCellEvent;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface PivotCellHandler extends EventHandler {
	void handleEvent(PivotCellEvent event);
}
