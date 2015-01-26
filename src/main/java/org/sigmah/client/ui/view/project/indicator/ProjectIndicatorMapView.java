package org.sigmah.client.ui.view.project.indicator;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.inject.Inject;
import org.sigmah.client.ui.view.base.AbstractView;

import com.google.inject.Singleton;
import java.util.List;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.project.indicator.ProjectIndicatorMapPresenter;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.map.GoogleWorldMap;
import org.sigmah.client.ui.widget.map.MultipleWorldMap;
import org.sigmah.client.ui.widget.map.OpenStreetMapWorldMap;
import org.sigmah.client.ui.widget.map.Pin;
import org.sigmah.client.ui.widget.map.WorldMap;
import org.sigmah.client.ui.widget.panel.Panels;

/**
 * {@link ProjectIndicatorMapPresenter} view.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ProjectIndicatorMapView extends AbstractView implements ProjectIndicatorMapPresenter.View {

	@Inject
	private SiteGridPanel siteGridPanel;
	
	private ContentPanel mapDisplayPanel;
	
	private WorldMap worldMap;
	
	private Status mapStatusBar;
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		add(createSitesManagementPanel(), Layouts.borderLayoutData(Style.LayoutRegion.CENTER, Layouts.Margin.RIGHT));
		add(createMapDisplayPanel(), Layouts.borderLayoutData(Style.LayoutRegion.EAST, 500f));
	}

	@Override
	public SiteGridPanel getSiteGridPanel() {
		return siteGridPanel;
	}

	@Override
	public WorldMap getWorldMap() {
		return worldMap;
	}

	@Override
	public void onMapLoaded() {
		
	}

	@Override
	public void setPins(List<Pin> pins) {
		worldMap.setPins(pins);
	}

	@Override
	public void setStatusMessage(String message, boolean busy) {
		if(busy) {
			mapStatusBar.setBusy(message);
		} else {
			mapStatusBar.clearStatus(message);
		}
	}
	
	public Component createSitesManagementPanel() {
		siteGridPanel.initialize();
		return siteGridPanel;
	}

	public Component createMapDisplayPanel() {
		mapDisplayPanel = Panels.content(I18N.CONSTANTS.map());
		
		// Adding the map
		worldMap = new MultipleWorldMap(new OpenStreetMapWorldMap(), new GoogleWorldMap());
		mapDisplayPanel.add(worldMap.asWidget());
		
		// Adding the status bar
		mapStatusBar = new Status();
		
		final ToolBar toolBar = new ToolBar();
		toolBar.add(mapStatusBar);
		
		mapDisplayPanel.setBottomComponent(toolBar);
		
		
		return mapDisplayPanel;
	}
}
