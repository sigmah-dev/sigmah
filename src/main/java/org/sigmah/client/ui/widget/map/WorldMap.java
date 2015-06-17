package org.sigmah.client.ui.widget.map;

import com.google.gwt.user.client.ui.IsWidget;
import java.util.List;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.shared.dto.BoundingBoxDTO;

/**
 * Defines a world map widget.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface WorldMap extends IsWidget, Loadable {
	String getName();
	
	void addPin(Pin pin);
	void setPins(List<Pin> pins);
	void updatePinPosition(Pin pin);
	void removeAllPins();
	
	void addPinDragEndHandler(Pin pin, PinDragEndHandler dragEndHandler);
	
	void setBounds(BoundingBoxDTO bounds);
	BoundingBoxDTO getBounds();
	void setCenterAndZoom(double latitude, double longitude, int zoom);
	void panTo(double latitude, double longitude);
	
	void setSize(String width, String height);
	
	void setDisplayed(boolean displayed);
}
