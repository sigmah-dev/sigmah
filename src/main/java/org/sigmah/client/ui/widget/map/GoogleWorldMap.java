package org.sigmah.client.ui.widget.map;

import com.google.gwt.maps.client.MapWidget;
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
import org.sigmah.shared.dto.BoundingBoxDTO;

/**
 * WorldMap implementation using Google Maps.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GoogleWorldMap extends AbstractWorldMap<Marker> {
	
	private MapWidget map;

	public GoogleWorldMap() {
		MapApiLoader.load(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				getRoot().add(new Label(I18N.CONSTANTS.connectionProblem()));
				setInitialized(true);
			}

			@Override
			public void onSuccess(Void result) {
				map = new MapWidget();
				map.setHeight("100%");
				getRoot().add(map);
				
				setInitialized(true);
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
