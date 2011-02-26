package org.sigmah.client.page.config.design;

import org.sigmah.client.page.project.SubPresenter;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class ProjectIndicatorsView extends LayoutContainer implements ProjectIndicatorsPresenter.View {

	protected ContentPanel mapContainer;
	
	private Button newIndicatorButton;
	private Button newGroupButton;
	private Button reloadButton;
	private Button showSiteMapButton;
	private Button showSiteTableButton;
	private Button loadSitesButton;
	private TreeStore<ModelData> treeStore;
	private TabPanel tabPanel;
	private TabItem mapTabItem;
	private TabItem sitesTabItem;
	
	public ProjectIndicatorsView(SubPresenter siteEditor, DesignPresenter designPresenter) {
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setContainerStyle("x-border-layout-ct main-background");
		setLayout(borderLayout);
		
		ContentPanel mainPanel = new ContentPanel();
		mainPanel.setIcon(null);
		 
		//setIcon(IconImageBundle.ICONS.design());

		// map tab panel 
        tabPanel = new TabPanel();
        tabPanel.setPlain(true);
        
        // map tab item
        mapTabItem = new TabItem("map");
        mapTabItem.setLayout(new FitLayout());
        mapTabItem.setEnabled(false);
        mapTabItem.setAutoHeight(true);
        mapTabItem.setEnabled(true);
        
        tabPanel.add(mapTabItem);
     
        // sites tab item
        sitesTabItem = new TabItem("sites");
        sitesTabItem.setLayout(new FitLayout());
        sitesTabItem.setEnabled(false);
        sitesTabItem.setAutoHeight(true);
        sitesTabItem.setEnabled(true);
        
        sitesTabItem.add(siteEditor.getView());
        tabPanel.add(sitesTabItem);
        
       // mapContainer.setHeaderVisible(false);
        //	mapContainer.setBorders(false);
        //	mapContainer.setFrame(false);
		
		// "tab" buttons for map view
		showSiteMapButton = new Button("show sitemap");
		showSiteTableButton = new Button("show site table");
		
		//mapContainer.add(showSiteMapButton);
		//mapContainer.add(showSiteTableButton);
	
		// buttons for indicator view
		newIndicatorButton = new Button("new indicator");
		newGroupButton = new Button("new group");
		reloadButton = new Button("reload button");
		
		mainPanel.add(newIndicatorButton);
		mainPanel.add(newGroupButton);
		mainPanel.add(reloadButton);
		
		// reload button for map view
		loadSitesButton = new Button("load site button");
		//mapContainer.add(loadSitesButton);
		
		BorderLayoutData centerLayout = new BorderLayoutData(Style.LayoutRegion.CENTER);
        centerLayout.setMargins(new Margins(0, 0, 0, 0));
        centerLayout.setSplit(true);
		centerLayout.setCollapsible(true);
        
		BorderLayoutData layout = new BorderLayoutData(Style.LayoutRegion.EAST);
		layout.setSplit(true);
		layout.setCollapsible(true);
		layout.setSize(375);
		layout.setMargins(new Margins(0, 0, 0, 5));
		
		add(designPresenter.getView(), centerLayout);
		add(tabPanel, layout);
		//setHeading(I18N.CONSTANTS.design() + " - " );
	}
	
	@Override
	public Button getNewIndicatorButton() {
		return this.newIndicatorButton;
	}

	@Override
	public Button getNewGroupButton() {
		return this.newGroupButton;
	}

	@Override
	public Button getReloadButton() {
		return this.reloadButton;
	}

	@Override
	public Button getShowSiteMapButton() {
		return this.showSiteMapButton;
	}

	@Override
	public Button getShowSiteTableButton() {
		return this.showSiteTableButton;
	}

	@Override
	public Button getLoadSitesButton() {
		return this.loadSitesButton;
	}
	
	@Override
	public TreeStore<ModelData> getTreeStore() {
		return this.treeStore;
	}

	/*
	@Override
	public void init(ProjectIndicatorsPresenter projectIndicatorsPresenter,
			TreeStore<ModelData> treeStore) {
		// TODO Auto-generated method stub
		
	}
	*/

}
