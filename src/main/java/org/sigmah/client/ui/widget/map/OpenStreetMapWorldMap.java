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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.LonLat;
import org.gwtopenmaps.openlayers.client.MapOptions;
import org.gwtopenmaps.openlayers.client.MapWidget;
import org.gwtopenmaps.openlayers.client.Pixel;
import org.gwtopenmaps.openlayers.client.Style;
import org.gwtopenmaps.openlayers.client.control.DragFeature;
import org.gwtopenmaps.openlayers.client.control.DragFeatureOptions;
import org.gwtopenmaps.openlayers.client.control.LayerSwitcher;
import org.gwtopenmaps.openlayers.client.feature.VectorFeature;
import org.gwtopenmaps.openlayers.client.geometry.Point;
import org.gwtopenmaps.openlayers.client.layer.OSM;
import org.gwtopenmaps.openlayers.client.layer.OSMOptions;
import org.gwtopenmaps.openlayers.client.layer.Vector;
import org.gwtopenmaps.openlayers.client.layer.XYZ;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.BoundingBoxDTO;

/**
 * OpenStreetMap implementation of WorldMap.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OpenStreetMapWorldMap extends AbstractWorldMap<VectorFeature> {
	
	private static final String TRANSFORM_SOURCE = "EPSG:4326";
	
	private MapWidget mapWidget;
	private Vector vectorLayer;
	private DragFeature dragFeature;
	private boolean draggable;

	private final List<VectorFeature> displayedFeatures = new ArrayList<VectorFeature>();
	private final Map<String, List<PinDragEndHandler>> dragEndHandlers = new HashMap<String, List<PinDragEndHandler>>();
	
	private static int featureIdSequence;
	
	@Override
	protected void init() {
		mapWidget = new MapWidget("100%", "100%", new MapOptions());
		
		// Humanitarian layer
		final OSMOptions hotOption = new OSMOptions();
		hotOption.setIsBaseLayer(true);
		hotOption.crossOriginFix();
		hotOption.setSphericalMercator(true);

		XYZ HOTLayer = new XYZ(I18N.CONSTANTS.humanitarianOSMLayer(), "http://a.tile.openstreetmap.fr/hot/${z}/${x}/${y}.png", hotOption);
				
		// Basic layer
		OSM osmMapnik = new OSM();
		osmMapnik.setName(I18N.CONSTANTS.basicOSMLayer());
		osmMapnik.setIsBaseLayer(true);
		
		// Pin layer
		vectorLayer = new Vector(I18N.CONSTANTS.projectSitesLayer());
		
		// Drag & drop listeners
		dragFeature = createDraggableFeature(vectorLayer);
		
		// Center & zoom
		LonLat lonLat = new LonLat(0, 0);
		lonLat.transform(TRANSFORM_SOURCE, mapWidget.getMap().getProjection());
		
		mapWidget.getMap().addLayer(HOTLayer);
		mapWidget.getMap().addLayer(osmMapnik);
		mapWidget.getMap().addLayer(vectorLayer);
		mapWidget.getMap().addControl(new LayerSwitcher());
		mapWidget.getMap().setCenter(lonLat, 1);
		mapWidget.getMap().updateSize();
		
		getRoot().add(mapWidget);
		setInitialized(true);
	}

	@Override
	public String getName() {
		return "OpenStreetMap";
	}

	@Override
	protected void displayBounds(BoundingBoxDTO bounds) {
		final LonLat lower = new LonLat(bounds.getX1(), bounds.getY1());
    	lower.transform(TRANSFORM_SOURCE, mapWidget.getMap().getProjection());
		
    	final LonLat upper = new LonLat(bounds.getX2(), bounds.getY2());
    	upper.transform(TRANSFORM_SOURCE, mapWidget.getMap().getProjection());
    	
        final Bounds b = new Bounds(lower.lon(), lower.lat(), upper.lon(), upper.lat());
        mapWidget.getMap().setCenter(b.getCenterLonLat(), 
        		mapWidget.getMap().getZoomForExtent(b, false));
        
        mapWidget.getMap().updateSize();
	}

	@Override
	protected void displayCenterAndZoom(double latitude, double longitude, int zoom) {
		final LonLat lonLat = new LonLat(longitude, latitude);
    	lonLat.transform(TRANSFORM_SOURCE, mapWidget.getMap().getProjection());
		
		mapWidget.getMap().setCenter(lonLat, zoom);
	}

	@Override
	protected void displayPins(List<VectorFeature> vectorFeatures) {
		for(final VectorFeature displayedFeature : displayedFeatures) {
			vectorLayer.removeFeature(displayedFeature);
			dragEndHandlers.remove(displayedFeature.getFeatureId());
		}
		displayedFeatures.clear();
		
		for(final VectorFeature vectorFeature : vectorFeatures) {
			vectorLayer.addFeature(vectorFeature);
			displayedFeatures.add(vectorFeature);
		}
	}

	@Override
	protected VectorFeature createNativePin(Pin pin) {
		final Style style = new Style();
		
		if(pin.getTitle() != null) {
			style.setLabel(pin.getTitle());
		}
		
		if(pin.getImageURL() != null) {
			style.setExternalGraphic(pin.getImageURL());
			style.setGraphicSize(pin.getImageWidth(), pin.getImageHeight());
			style.setGraphicOffset(
				// Horizontal center
				-pin.getImageWidth() / 2, 
				// Bottom
				-pin.getImageHeight());
		} else {
			style.setExternalGraphic("http://www.google.com/mapfiles/marker.png");
			style.setGraphicSize(20, 34);
			style.setGraphicOffset(-10, -34);
		}
		style.setFillOpacity(1.0);
		
		if(pin.isDraggable() && !draggable) {
			draggable = true;
			mapWidget.getMap().addControl(dragFeature);
			dragFeature.activate();
		}

		final LonLat lonLat = new LonLat(pin.getLongitude(), pin.getLatitude());
		lonLat.transform(TRANSFORM_SOURCE, mapWidget.getMap().getProjection());

		final Point point = new Point(lonLat.lon(), lonLat.lat());
		
		final VectorFeature vectorFeature = new VectorFeature(point, style);
		vectorFeature.setFeatureId("P" + (++featureIdSequence));
		return vectorFeature;
	}

	@Override
	protected void movePin(VectorFeature pin, double latitude, double longitude) {
		final LonLat lonLat = new LonLat(longitude, latitude);
		lonLat.transform(TRANSFORM_SOURCE, mapWidget.getMap().getProjection());
		
		pin.move(lonLat);
	}

	@Override
	protected void addPinDragEndHandler(VectorFeature vectorFeature, PinDragEndHandler dragEndHandler) {
		List<PinDragEndHandler> handlers = dragEndHandlers.get(vectorFeature);
		if(handlers == null) {
			handlers = new ArrayList<PinDragEndHandler>();
			dragEndHandlers.put(vectorFeature.getFeatureId(), handlers);
		}
		handlers.add(dragEndHandler);
	}

	@Override
	public void panTo(double latitude, double longitude) {
		final LonLat lonLat = new LonLat(longitude, latitude);
		lonLat.transform(TRANSFORM_SOURCE, mapWidget.getMap().getProjection());
		
		mapWidget.getMap().setCenter(lonLat);
	}
	
	private DragFeature createDraggableFeature(Vector vector) {
		final DragFeatureOptions dragFeatureOptions = new DragFeatureOptions();
		
		final DragFeature.DragFeatureListener emptyListener = new DragFeature.DragFeatureListener() {

			@Override
			public void onDragEvent(VectorFeature vectorFeature, Pixel pixel) {
			}
		};
		
		final DragFeature.DragFeatureListener dragListener = new DragFeature.DragFeatureListener() {

			@Override
			public void onDragEvent(VectorFeature vectorFeature, Pixel pixel) {
				final LonLat lonLat = mapWidget.getMap().getLonLatFromPixel(pixel);
				lonLat.transform(mapWidget.getMap().getProjection(), TRANSFORM_SOURCE);
				
				final List<PinDragEndHandler> handlers = dragEndHandlers.get(vectorFeature.getFeatureId());
				if(handlers != null) {
					for(final PinDragEndHandler handler : handlers) {
						handler.onDragEnd(lonLat.lat(), lonLat.lon());
					}
				}
			}
		};
		
		dragFeatureOptions.onStart(emptyListener);
		dragFeatureOptions.onDrag(emptyListener);
		dragFeatureOptions.onComplete(dragListener);
		return new DragFeature(vector, dragFeatureOptions);
	}
	
}
