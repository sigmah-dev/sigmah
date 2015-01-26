package org.sigmah.client.ui.widget.map;

import java.util.ArrayList;
import java.util.List;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.SitePointDTO;

/**
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Pin {
	
	private String title;
	private double longitude;
	private double latitude;
	
	private String imageURL;
	private int imageWidth;
	private int imageHeight;
	
	private boolean draggable;
	
	private final List<PinDragEndHandler> dragEndHandlers;
	
	private WorldMap parent;

	public Pin() {
		dragEndHandlers = new ArrayList<PinDragEndHandler>();
	}

	public Pin(boolean draggable) {
		this();
		this.draggable = draggable;
	}
	
	public Pin(Pin source) {
		this();
		this.title = source.title;
		this.longitude = source.longitude;
		this.latitude = source.latitude;
		this.imageURL = source.imageURL;
		this.imageWidth = source.imageWidth;
		this.imageHeight = source.imageHeight;
		this.draggable = source.draggable;
	}

	public Pin(SitePointDTO sitePoint, boolean mainSite) {
		this();
		this.title = sitePoint.getName();
		this.longitude = sitePoint.getX();
		this.latitude = sitePoint.getY();
		if(mainSite) {
			this.title = sitePoint.getName() + " (" + I18N.CONSTANTS.mainSiteLabel() + ')';
			this.imageURL = "https://raw.githubusercontent.com/somzzz/Sigmah_resources/master/resources/markerStar.png";
			this.imageWidth = 20;
			this.imageHeight = 34;
		}
	}

	public String getTitle() {
		return title;
	}

	/**
	 * y coordinate.
	 * @return 
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * x coordinate.
	 * @return 
	 */
	public double getLongitude() {
		return longitude;
	}
	
	public void setPosition(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
		
		if(parent != null) {
			parent.updatePinPosition(this);
		}
	}

	public String getImageURL() {
		return imageURL;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public boolean isDraggable() {
		return draggable;
	}
	
	public void addPinDragEndHandler(PinDragEndHandler pinDragEndHandler) {
		dragEndHandlers.add(pinDragEndHandler);
		
		if(parent != null) {
			parent.addPinDragEndHandler(this, pinDragEndHandler);
		}
	}

	protected List<PinDragEndHandler> getPinDragEndHandlers() {
		return dragEndHandlers;
	}
	
	protected void setParent(WorldMap worldMap) {
		parent = worldMap;
	}
}
