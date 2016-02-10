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

import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.maps.client.event.MarkerDragEndHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.geom.Size;
import com.google.gwt.maps.client.overlay.Icon;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import java.util.List;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.map.MapApiLoader;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.shared.dto.BoundingBoxDTO;

/**
 * WorldMap implementation using Google Maps.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GoogleWorldMap extends AbstractWorldMap<Marker> {
	
	private MapWidget map;

	@Override
	protected void init() {
		MapApiLoader.load(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				getRoot().add(new Label(I18N.CONSTANTS.connectionProblem()));
				setInitialized(true);
			}

			@Override
			public void onSuccess(Void result) {
				if(Maps.isLoaded()) {
					map = new MapWidget();
					map.setHeight("100%");
					getRoot().add(map);

					setInitialized(true);
					
				} else {
					N10N.errorNotif(I18N.CONSTANTS.googleMaps(), I18N.CONSTANTS.cannotLoadMap());
				}
			}
		});
	}

	@Override
	public String getName() {
		return "Google Maps";
	}

	@Override
	protected void displayBounds(BoundingBoxDTO bounds) {
		final BoundingBoxDTO effectiveBounds = bounds != null ? bounds : new BoundingBoxDTO();
		
		final LatLngBounds latLngBounds = toLatLngBounds(effectiveBounds);
		final int zoomLevel = map.getBoundsZoomLevel(latLngBounds);

		map.setCenter(latLngBounds.getCenter());
		map.setZoomLevel(zoomLevel);
	}

	@Override
	protected void displayCenterAndZoom(double latitude, double longitude, int zoom) {
		map.setCenter(LatLng.newInstance(latitude, longitude));
		map.setZoomLevel(zoom);
	}

	@Override
	protected Marker createNativePin(Pin pin) {
		final MarkerOptions options = MarkerOptions.newInstance();
			
		if(pin.getTitle() != null) {
			options.setTitle(pin.getTitle());
		}
		
		options.setDraggable(pin.isDraggable());

		if(pin.getImageURL() != null) {
			final Icon icon = Icon.newInstance(pin.getImageURL());
			icon.setIconSize(Size.newInstance(pin.getImageWidth(), pin.getImageHeight()));
			icon.setIconAnchor(Point.newInstance(
				// Horizontal center
				pin.getImageWidth() / 2,
				// Bottom
				pin.getImageHeight()));
			options.setIcon(icon);
		}
		
		return new Marker(
			LatLng.newInstance(pin.getLatitude(), pin.getLongitude()), 
			options);
	}

	@Override
	protected void addPinDragEndHandler(Marker pin, final PinDragEndHandler dragEndHandler) {
		pin.addMarkerDragEndHandler(new MarkerDragEndHandler() {

			@Override
			public void onDragEnd(MarkerDragEndHandler.MarkerDragEndEvent event) {
				final LatLng latLng = event.getSender().getLatLng();
				dragEndHandler.onDragEnd(latLng.getLatitude(), latLng.getLongitude());
			}
		});
	}
	
	@Override
	protected void displayPins(List<Marker> markers) {
		map.clearOverlays();
		for(final Marker marker : markers) {
			map.addOverlay(marker);
		}
	}

	@Override
	public void panTo(double latitude, double longitude) {
		map.panTo(LatLng.newInstance(latitude, longitude));
	}

	@Override
	protected void movePin(Marker marker, double latitude, double longitude) {
		marker.setLatLng(LatLng.newInstance(latitude, longitude));
	}
	
	/**
	 * Create an instance of LatLngBounds with the bounds defined by this object.
	 * @return A new LatLngBounds instance.
	 */
	private LatLngBounds toLatLngBounds(BoundingBoxDTO boundingBox) {
		return LatLngBounds.newInstance(
                LatLng.newInstance(boundingBox.getY1(), boundingBox.getX1()),
                LatLng.newInstance(boundingBox.getY2(), boundingBox.getX2()));
	}
}
