package org.sigmah.client.page.config.design;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.toolbar.ActionToolBar;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.inject.Inject;

public class ProjectIndicatorsContainer extends LayoutContainer implements SubPresenter {

	//private SiteEditor siteEditor;
	private DesignPanelBase designPanel;
	private SchemaDTO schema;
	private UserDatabaseDTO db;
	private final Dispatcher service;
	private final EventBus eventBus;
	
	private ContentPanel mapContainer;
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
	private ProjectSiteGridPanel siteEditor;

	@Inject
	public ProjectIndicatorsContainer(ProjectSiteGridPanel siteEditor,
			final DesignPanel designPanel, Dispatcher service, EventBus eventBus) {
		
		this.siteEditor = siteEditor;
		this.designPanel = designPanel;
		this.service = service;
		this.eventBus = eventBus;

		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setContainerStyle("x-border-layout-ct main-background");
		setLayout(borderLayout);

		ContentPanel mainPanel = new ContentPanel();
		mainPanel.setIcon(null);

		// setIcon(IconImageBundle.ICONS.design());

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
		
		// "tab" buttons for map view
		showSiteMapButton = new Button("show sitemap");
		showSiteTableButton = new Button("show site table");

		// mapContainer.add(showSiteMapButton);
		// mapContainer.add(showSiteTableButton);

		// buttons for indicator view
		newIndicatorButton = new Button("new indicator");
		newGroupButton = new Button("new group");
		reloadButton = new Button("reload button");

		mainPanel.add(newIndicatorButton);
		mainPanel.add(newGroupButton);
		mainPanel.add(reloadButton);

		// reload button for map view
		loadSitesButton = new Button("load site button");
		// mapContainer.add(loadSitesButton);

		BorderLayoutData centerLayout = new BorderLayoutData(
				Style.LayoutRegion.CENTER);
		centerLayout.setMargins(new Margins(0, 0, 0, 0));
		centerLayout.setSplit(true);
		centerLayout.setCollapsible(true);

		BorderLayoutData layout = new BorderLayoutData(Style.LayoutRegion.EAST);
		layout.setSplit(true);
		layout.setCollapsible(true);
		layout.setSize(375);
		layout.setMargins(new Margins(0, 0, 0, 5));

		add(designPanel, centerLayout);
		add(tabPanel, layout);
		// setHeading(I18N.CONSTANTS.design() + " - " );
	
		ActionToolBar bar = designPanel.getToolbar();
		Menu newMenu = new Menu();

		Button newButtonMenu = new Button(I18N.CONSTANTS.newText(),
				IconImageBundle.ICONS.add());
		newButtonMenu.setMenu(newMenu);
		newButtonMenu.setEnabled(true);
		bar.add(newButtonMenu);

		SelectionListener<MenuEvent> listener = new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				designPanel.onNew(ce.getItem().getItemId());
			}
		};
		
		final MenuItem newIndicatorGroup = new MenuItem(
				I18N.CONSTANTS.newIndicatorGroup(),
				IconImageBundle.ICONS.indicator(), listener);
		newIndicatorGroup.setItemId("IndicatorGroup");
		newMenu.add(newIndicatorGroup);
		
		final MenuItem newIndicator = new MenuItem(
				I18N.CONSTANTS.newIndicator(),
				IconImageBundle.ICONS.indicator(), listener);
		newIndicator.setItemId("Indicator");
		newMenu.add(newIndicator);
		
		Button reloadButtonMenu = new Button(I18N.CONSTANTS.refresh(),
				IconImageBundle.ICONS.refresh());
		reloadButtonMenu.setEnabled(true);
		bar.add(reloadButtonMenu);		
	}

	
	

	private ActivityDTO getCurrentActivity() {
		if (db.getActivities() != null && db.getActivities().size() > 0) {
			// TODO fix me
			return db.getActivities().get(0);
		}
		return null;
	}
	
	/*private void wireViews() {
		
		getNewIndicatorButton().addListener(Events.OnClick,
				new Listener<ButtonEvent>() {
					@Override
					public void handleEvent(ButtonEvent be) {

					}
				});

		getNewGroupButton().addListener(Events.OnClick,
				new Listener<ButtonEvent>() {
					@Override
					public void handleEvent(ButtonEvent be) {

					}
				});

		getReloadButton().addListener(Events.OnClick,
				new Listener<ButtonEvent>() {
					@Override
					public void handleEvent(ButtonEvent be) {

					}
				});

		getShowSiteMapButton().addListener(Events.OnClick,
				new Listener<ButtonEvent>() {
					@Override
					public void handleEvent(ButtonEvent be) {

					}
				});

		getLoadSitesButton().addListener(Events.OnClick,
				new Listener<ButtonEvent>() {
					@Override
					public void handleEvent(ButtonEvent be) {

					}
				});
	}*/

	@Override
	public Component getView() {
		// create a tree store
		//SiteGrid siteGrid = new SiteGrid();
		//siteGrid.setHeaderVisible(false);
		return (Component) this;
	}

	@Override
	public void viewDidAppear() {
		// TODO Auto-generated method stub
	}

	@Override
	public void discardView() {
		// TODO Auto-generated method stub
		
	}
}
