package org.sigmah.client.ui.widget.map;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.BoundingBoxDTO;

/**
 * Simplify the creation of a WorldMap implementation by handling the
 * most commons user cases.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <P> Native implementation of the pins
 */
public abstract class AbstractWorldMap<P> implements WorldMap {
	
	private static final String LOADING_MASK_STYLENAME = "ext-el-mask";
	private static final String LOADING_MESSAGE_STYLENAME = "ext-el-mask-msg";
	
	private final FlowPanel root;
	private final FlowPanel loadingMask;
	
	private boolean initialized;
	private boolean displayed;
	private boolean loading;
	
	private final Map<Pin, P> pins;
	
	private boolean useCenterAndZoom;
	private BoundingBoxDTO bounds;
	private double centerLongitude;
	private double centerLatitude;
	private int zoom;

	public AbstractWorldMap() {
		root = new FlowPanel();
		root.getElement().getStyle().setPosition(Style.Position.RELATIVE);
		
		loadingMask = new FlowPanel();
		loadingMask.setStyleName(LOADING_MASK_STYLENAME);
		
		final Label loadingMessage = new Label(I18N.CONSTANTS.loading());
		loadingMessage.setStyleName(LOADING_MESSAGE_STYLENAME);
		loadingMask.add(loadingMessage);
		
		pins = new HashMap<Pin, P>();
		
		bounds = new BoundingBoxDTO();
		
		root.add(loadingMask);
	}
	
	protected abstract void displayBounds(BoundingBoxDTO bounds);
	
	protected abstract void displayCenterAndZoom(double latitude, double longitude, int zoom);
	
	protected abstract void displayPins(List<P> pins);
	
	protected abstract P createNativePin(Pin pin);
	
	protected abstract void movePin(P pin, double latitude, double longitude);
	
	protected abstract void addPinDragEndHandler(P pin, PinDragEndHandler dragEndHandler);
	
	protected abstract void init();

	@Override
	public void setSize(String width, String height) {
		root.setWidth(width);
		root.setHeight(height);
	}

	@Override
	public void setBounds(BoundingBoxDTO bounds) {
		this.bounds = bounds;
		this.useCenterAndZoom = false;
		updateBounds();
	}

	@Override
	public BoundingBoxDTO getBounds() {
		return new BoundingBoxDTO(this.bounds);
	}
	
	@Override
	public void setCenterAndZoom(double latitude, double longitude, int zoom) {
		this.centerLongitude = longitude;
		this.centerLatitude = latitude;
		this.zoom = zoom;
		this.useCenterAndZoom = true;
		updateBounds();
	}
	
	@Override
	public void addPin(Pin pin) {
		addPinPrivate(pin);
		updatePins();
	}
	
	private void addPinPrivate(Pin pin) {
		pin.setParent(this);
		
		final P nativePin;
		if(initialized) {
			nativePin = createNativePin(pin);
			
			for(final PinDragEndHandler dragEndHandler : pin.getPinDragEndHandlers()) {
				addPinDragEndHandler(nativePin, dragEndHandler);
			}
		} else {
			nativePin = null;
		}
		pins.put(pin, nativePin);
	}
	
	@Override
	public void setPins(List<Pin> pins) {
		this.pins.clear();
		
		final BoundingBoxDTO pinBounds = new BoundingBoxDTO(0, 0, 0, 0);
		if(!pins.isEmpty()) {
			final Pin pin = pins.get(0);
			pinBounds.setX1(pin.getLongitude());
			pinBounds.setX2(pin.getLongitude());
			pinBounds.setY1(pin.getLatitude());
			pinBounds.setY2(pin.getLatitude());
		}
		
		for(final Pin pin : pins) {
			addPinPrivate(pin);
			
			// Extends the bounding box to cover all the pins.
			pinBounds.grow(pin.getLongitude(), pin.getLatitude());
		}
		
		updatePins();
		
		// Center the view on the given pins.
		Scheduler.get().scheduleDeferred(new Command() {

			@Override
			public void execute() {
				setBounds(pinBounds);
			}
		});
	}

	@Override
	public void updatePinPosition(Pin pin) {
		final P nativePin = pins.get(pin);
		if(nativePin != null) {
			movePin(nativePin, pin.getLatitude(), pin.getLongitude());
		}
	}
	
	@Override
	public void removeAllPins() {
		pins.clear();
		updatePins();
	}

	@Override
	public void addPinDragEndHandler(Pin pin, PinDragEndHandler dragEndHandler) {
		final P nativePin = pins.get(pin);
		if(nativePin != null) {
			addPinDragEndHandler(nativePin, dragEndHandler);
		}
	}

	@Override
	public Widget asWidget() {
		return root;
	}
	
	@Override
	public void setLoading(boolean loading) {
		this.loading = loading;
		updateLoadingMask();
	}

	@Override
	public boolean isLoading() {
		return loading;
	}
	
	@Override
	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
		
		if(displayed && !initialized) {
			init();
		}
	}

	protected void setInitialized(boolean initialized) {
		this.initialized = initialized;
		createNativePins();
		updatePins();
		updateBounds();
		updateLoadingMask();
	}


	protected Panel getRoot() {
		return root;
	}
	
	private void updateBounds() {
		if(initialized) {
			if(useCenterAndZoom) {
				displayCenterAndZoom(centerLatitude, centerLongitude, zoom);
			} else {
				displayBounds(bounds);
			}
		}
	}
	
	private void updatePins() {
		if(initialized) {
			displayPins(new ArrayList<P>(pins.values()));
		}
	}
	
	private void createNativePins() {
		for(final Map.Entry<Pin, P> entry : pins.entrySet()) {
			if(entry.getValue() == null) {
				final P nativePin = createNativePin(entry.getKey());
				for(final PinDragEndHandler dragEndHandler : entry.getKey().getPinDragEndHandlers()) {
					addPinDragEndHandler(nativePin, dragEndHandler);
				}
				
				entry.setValue(nativePin);
			}
		}
	}
	
	private void updateLoadingMask() {
		if(!initialized || loading) {
			loadingMask.getElement().getStyle().clearDisplay();
		} else {
			loadingMask.getElement().getStyle().setDisplay(Style.Display.NONE);
		}
	}
	
}
