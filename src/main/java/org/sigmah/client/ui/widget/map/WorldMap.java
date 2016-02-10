package org.sigmah.client.ui.widget.map;

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
