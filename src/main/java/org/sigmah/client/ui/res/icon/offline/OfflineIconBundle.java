package org.sigmah.client.ui.res.icon.offline;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface OfflineIconBundle extends ClientBundle {
	
	public static final OfflineIconBundle INSTANCE = GWT.create(OfflineIconBundle.class);
	
	ImageResource signalOff();
	ImageResource signalOn();
	
	ImageResource connect();
	ImageResource disconnect();
	
	ImageResource error();
}
