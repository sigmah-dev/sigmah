/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.event.EntityEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.map.AdminBoundsHelper;
import org.sigmah.client.map.GcIconFactory;
import org.sigmah.client.map.MapApiLoader;
import org.sigmah.client.map.MapTypeFactory;
import org.sigmah.client.page.common.Shutdownable;
import org.sigmah.shared.command.GetSitePoints;
import org.sigmah.shared.command.result.SitePointList;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.BoundingBoxDTO;
import org.sigmah.shared.dto.CountryDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.dto.SitePointDTO;

import com.ebessette.maps.core.client.overlay.MarkerManagerImpl;
import com.ebessette.maps.core.client.overlay.OverlayManagerOptions;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.maps.client.MapType;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.control.SmallMapControl;
import com.google.gwt.maps.client.event.MapClickHandler;
import com.google.gwt.maps.client.event.MapRightClickHandler;
import com.google.gwt.maps.client.geom.LatLng;
import com.google.gwt.maps.client.geom.LatLngBounds;
import com.google.gwt.maps.client.geom.Point;
import com.google.gwt.maps.client.overlay.Marker;
import com.google.gwt.maps.client.overlay.MarkerOptions;
import com.google.gwt.maps.client.overlay.Overlay;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

/**
 * A map panel that serves a counterpart to the SiteGrid, and
 * a drop target for <code>SiteDTO</code>.
 * <p/>
 *
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class SiteMap extends ContentPanel implements Shutdownable {

    private final EventBus eventBus;
    private final Dispatcher service;
    private Filter filter;

    private MapWidget map = null;
    private LatLngBounds pendingZoom = null;

    /**
     * Efficiently handles a large number of markers
     */
    private MarkerManagerImpl markerMgr;

    /**
     * Maps markers to site ids
     */
    private Map<Marker, Integer> markerIds;

    /**
     * Maps siteIds to markers
     */
    private Map<Integer, Marker> sites;

    private Listener<EntityEvent<SiteDTO>> siteListener; 

    private Marker currentHighlightedMarker;
    private Marker highlitMarker;

    private Menu contextMenu;
    
    private CountryDTO country;

    private Status status;
    
    @Inject
    public SiteMap(EventBus eventBus, Dispatcher service) {
        this.eventBus = eventBus;
        this.service = service;

        setHeaderVisible(false);
        
        status = new Status();
        setBottomComponent(status);
    }
    
    public void loadSites(ActivityDTO activity) {
    	loadSites(
    			activity.getDatabase().getCountry(),
    			Filter.filter().onActivity(activity.getId()));
    }
    
    /**
     * Loads the sites for the given Activity
     * 
     * @param activity
     */
    public void loadSites(CountryDTO country, Filter filter) {
    	this.country = country;
    	this.filter = filter;
    	if(map == null) {
    		loadMap();
    	} else {
    		doLoadSites();
    	}
    }

    private void onSiteChanged(SiteDTO site) {

    }

    private void onSiteCreated(SiteDTO site) {

    }

    private void onSiteSelected(EntityEvent<SiteDTO> se) {
        if (se.getSource() != this) {
            if (se.getEntity() != null && !se.getEntity().hasCoords()) {
                BoundingBoxDTO bounds = AdminBoundsHelper.calculate(country, se.getEntity());
                LatLngBounds llBounds = llBoundsForBounds(bounds);

                if (!llBounds.containsBounds(map.getBounds())) {
                    zoomToBounds(llBounds);
                }
            } else {
                highlightSite(se.getSiteId(), true);
            }
        }
    }
    
    private CountryDTO getCountry() {
    	return country;
    }


    public void shutdown() {
        eventBus.removeListener(EntityEvent.SELECTED, siteListener);
        eventBus.removeListener(EntityEvent.CREATED, siteListener);
        eventBus.removeListener(EntityEvent.UPDATED, siteListener);
    }

    public void onSiteDropped(Record record, double lat, double lng) {
        record.set("x", lng);
        record.set("y", lat);
        updateSiteCoords(((SiteDTO) record.getModel()).getId(), lat, lng);

    }

    public BoundingBoxDTO getSiteBounds(SiteDTO site) {
        return AdminBoundsHelper.calculate(getCountry(), site);
    }

	private void loadMap() {
		status.setBusy(I18N.CONSTANTS.loadingGoogleMaps());
		MapApiLoader.load(new MaskingAsyncMonitor(this, I18N.CONSTANTS.loadingComponent()), new AsyncCallback<Void>() {

            @Override
            public void onFailure(Throwable throwable) {
                removeAll();
                setLayout(new CenterLayout());
                add(new Html(I18N.CONSTANTS.connectionProblem()));
                layout();
            }

            @Override
            public void onSuccess(Void result) {
                removeAll();

                BoundingBoxDTO countryBounds = country.getBounds();
                LatLng boundsFromActivity = LatLng.newInstance(countryBounds.getCenterY(), countryBounds.getCenterX());
				map = new MapWidget(boundsFromActivity, 8);

                MapType adminMap = MapTypeFactory.createLocalisationMapType(country);
                map.addMapType(adminMap);
                map.setCurrentMapType(adminMap);
                map.addControl(new SmallMapControl());

                setLayout(new FitLayout());
                add(map);

                map.addMapClickHandler(new MapClickHandler() {
                    @Override
                    public void onClick(MapClickEvent event) {
                        if (event.getOverlay() != null) {
                            int siteId = siteIdFromOverlay(event.getOverlay());
                            highlightSite(siteId, false);
                        }
                    }
                });
                map.addMapRightClickHandler(new MapRightClickHandler() {
                    public void onRightClick(MapRightClickEvent event) {
                        if (event.getOverlay() != null) {
                            showContextMenu(event);
                        }
                    }
                });

                // Listen for when this component is resized/layed out
                // to assure that map widget is properly restated
                Listener<BaseEvent> resizeListener = new Listener<BaseEvent>() {

                    @Override
                    public void handleEvent(BaseEvent be) {

                        map.checkResizeAndCenter();

                        if (pendingZoom != null) {
                            zoomToBounds(pendingZoom);
                        }
                    }
                };

                addListener(Events.AfterLayout, resizeListener);
                addListener(Events.Resize, resizeListener);

                new MapDropTarget(SiteMap.this);

                layout();
                
                doLoadSites();
            }
        });
	}


	private void doLoadSites() {
		status.setBusy(I18N.CONSTANTS.loading());
		service.execute(new GetSitePoints(filter), null, new AsyncCallback<SitePointList>() {
            @Override
            public void onFailure(Throwable throwable) {
            	status.clearStatus(I18N.CONSTANTS.serverError());
            }

            @Override
            public void onSuccess(SitePointList points) {
                if(points.getPoints().isEmpty()) {
                	status.clearStatus("No sites to display");
                } else {
                	status.clearStatus(points.getPoints().size() + " site(s) loaded.");
                }
            	
            	addSitesToMap(points);

                siteListener = new Listener<EntityEvent<SiteDTO>>() {
                    public void handleEvent(EntityEvent<SiteDTO> be) {
                        if (be.getType() == EntityEvent.SELECTED) {
                            onSiteSelected(be);
                        } else if (be.getType() == EntityEvent.CREATED) {
                            onSiteCreated(be.getEntity());
                        } else if (be.getType() == EntityEvent.UPDATED) {
                            onSiteChanged(be.getEntity());
                        }
                    }
                };

                eventBus.addListener(EntityEvent.SELECTED, siteListener);
                eventBus.addListener(EntityEvent.CREATED, siteListener);
                eventBus.addListener(EntityEvent.UPDATED, siteListener);
            }
        });
	}



    private int siteIdFromOverlay(Overlay overlay) {
        if (overlay == highlitMarker) {
            return markerIds.get(currentHighlightedMarker);
        } else {
            return markerIds.get(overlay);
        }
    }

    /**
     * Attempts to pan to the center of the bounds and
     * zoom to the necessary zoom level. If the map widget is not
     * rendered or is in a funk because of resizing, the zoom
     * is deferred until a Resize or AfterLayout event is received.
     *
     * @param bounds
     */
    private void zoomToBounds(LatLngBounds bounds) {

        int zoomLevel = map.getBoundsZoomLevel(bounds);
        if (zoomLevel == 0) {
            pendingZoom = bounds;
        } else {
            map.setCenter(bounds.getCenter(), zoomLevel);
            pendingZoom = null;
        }
    }

    public void addSitesToMap(SitePointList points) {

        if (markerMgr == null) {
            OverlayManagerOptions options = new OverlayManagerOptions();
            options.setMaxZoom(map.getCurrentMapType().getMaximumResolution());

            markerMgr = new MarkerManagerImpl(map, options);
        } else {
            for (Marker marker : markerIds.keySet()) {
                markerMgr.removeMarker(marker);
            }
        }
        markerIds = new HashMap<Marker, Integer>();
        sites = new HashMap<Integer, Marker>();

        zoomToBounds(llBoundsForBounds(points.getBounds()));

        List<Marker> markers = new ArrayList<Marker>(points.getPoints().size());

        for (SitePointDTO point : points.getPoints()) {

            Marker marker = new Marker(LatLng.newInstance(point.getY(), point.getX()));
            markerIds.put(marker, point.getSiteId());
            sites.put(point.getSiteId(), marker);
            markers.add(marker);
        }

        markerMgr.addOverlays(markers, 0);
        DeferredCommand.addCommand(new Command() {
			
			@Override
			public void execute() {
				markerMgr.refresh();
			}
		});

    }

    private LatLngBounds llBoundsForBounds(BoundingBoxDTO bounds) {
        LatLngBounds llbounds = LatLngBounds.newInstance(
                LatLng.newInstance(bounds.getY2(), bounds.getX1()),
                LatLng.newInstance(bounds.getY1(), bounds.getX2()));
        return llbounds;
    }

    public void updateSiteCoords(int siteId, double lat, double lng) {
        Marker marker = sites.get(siteId);
        if (marker != null) {
            // update existing site
            marker.setLatLng(LatLng.newInstance(lat, lng));
        } else {
            // create new marker
            marker = new Marker(LatLng.newInstance(lat, lng));
            markerIds.put(marker, siteId);
            sites.put(siteId, marker);
            markerMgr.addOverlay(marker, 0);
        }
    }

    public void highlightSite(int siteId, boolean panTo) {
        Marker marker = sites.get(siteId);
        if (marker != null) {

            // we can't change the icon ( I don't think )
            // so we'll bring in a ringer for the selected site

            if (highlitMarker == null) {
                GcIconFactory iconFactory = new GcIconFactory();
                iconFactory.primaryColor = "#0000FF";
                MarkerOptions opts = MarkerOptions.newInstance();
                opts.setIcon(iconFactory.createMarkerIcon());
                highlitMarker = new Marker(marker.getLatLng(), opts);
                map.addOverlay(highlitMarker);
            } else {
                // make sure this marker is on top
                map.removeOverlay(highlitMarker);
                highlitMarker.setLatLng(marker.getLatLng());
                map.addOverlay(highlitMarker);
            }

            if (currentHighlightedMarker != null) {
                currentHighlightedMarker.setVisible(true);
            }
            currentHighlightedMarker = marker;
            currentHighlightedMarker.setVisible(false);

            if (!map.getBounds().containsLatLng(marker.getLatLng())) {
                map.panTo(marker.getLatLng());
            }
        } else {
            // no coords, un highlight existing marker
            if (currentHighlightedMarker != null) {
                currentHighlightedMarker.setVisible(true);
            }
            if (highlitMarker != null) {
                map.removeOverlay(highlitMarker);
                highlitMarker = null;
            }
        }
    }

    private void showContextMenu(MapRightClickHandler.MapRightClickEvent event) {
        if (contextMenu == null) {

            contextMenu = new Menu();
            contextMenu.add(new MenuItem(I18N.CONSTANTS.showInGrid(),
                    IconImageBundle.ICONS.table(), new SelectionListener<MenuEvent>() {
                        @Override
                        public void componentSelected(MenuEvent ce) {


                        }
                    }));
        }
        Marker marker = (Marker) event.getOverlay();
        contextMenu.show(event.getElement(), "tr");

    }

    private class MapDropTarget extends DropTarget {

        BoundingBoxDTO bounds;
        String boundsName;

        private MapDropTarget(Component target) {
            super(target);
        }

        @Override
        protected void onDragEnter(DNDEvent event) {

            SiteDTO site = getSite(event);
            if (site == null) {
                bounds = null;
            } else {
                bounds = AdminBoundsHelper.calculate(getCountry(), site);
                boundsName = AdminBoundsHelper.name(getCountry(), bounds, site);
            }
            updateDragStatus(event);
        }

        @Override
        protected void onDragMove(DNDEvent event) {
            if (bounds != null) {
                updateDragStatus(event);
            }
        }

        private void updateDragStatus(DNDEvent event) {

            if (bounds == null) {
                // not a site that's being dragged
                event.setCancelled(true);
                event.getStatus().setStatus(false);
            } else {
                LatLng latlng = getLatLng(event);
                if (bounds.contains(latlng.getLongitude(), latlng.getLatitude())) {
                    // a site that's within its bounds
                    event.setCancelled(false);
                    event.getStatus().setStatus(true);
                    event.getStatus().update(GXT.MESSAGES.messageBox_ok());
                } else {
                    // a site that's outside it's bounds
                    event.setCancelled(true);
                    event.getStatus().setStatus(false);
                    event.getStatus().update(I18N.MESSAGES.coordOutsideBounds(boundsName));
                }
            }
        }

        @Override
        protected void onDragDrop(DNDEvent event) {

            if (bounds != null) {
                LatLng latlng = getLatLng(event);
                if (bounds.contains(latlng.getLongitude(), latlng.getLatitude())) {
                    event.setCancelled(false);
                    onSiteDropped((Record) event.getData(), latlng.getLatitude(), latlng.getLongitude());
                }
            }
        }

        private LatLng getLatLng(DNDEvent event) {
            int x = event.getClientX() - map.getElement().getAbsoluteLeft();
            int y = event.getClientY() - map.getElement().getAbsoluteTop();
            return map.convertContainerPixelToLatLng(Point.newInstance(x, y));
        }

        private SiteDTO getSite(DNDEvent event) {
            if (event.getData() instanceof Record) {
                Record record = (Record) event.getData();
                if (record.getModel() instanceof SiteDTO) {
                    return (SiteDTO) record.getModel();
                }
            }
            return null;
        }

    }

}
