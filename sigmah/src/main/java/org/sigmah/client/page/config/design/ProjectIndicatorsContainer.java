package org.sigmah.client.page.config.design;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.page.project.ProjectPresenter;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
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
import com.google.inject.Inject;

public class ProjectIndicatorsContainer extends LayoutContainer implements SubPresenter {

	//private SiteEditor siteEditor;
	private DesignPanel designPanel;
	private SchemaDTO schema;
	private UserDatabaseDTO db;
	private final Dispatcher service;
	private final EventBus eventBus;
	
	private ContentPanel mapContainer;
	private Button newIndicatorButton;
	private Button newGroupButton;
	private Button reloadButton;
	private TreeStore<ModelData> treeStore;	
	private TabPanel tabPanel;
	private TabItem mapTabItem;
	private TabItem sitesTabItem;
	private ProjectSiteGridPanel siteEditor;
	private ProjectPresenter projectPresenter;
		
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
		mainPanel.setLayout(new FitLayout());  
		mainPanel.setSize(600, 300);  

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

		// buttons for indicator view
		newIndicatorButton = new Button("new indicator");
		newGroupButton = new Button("new group");
		reloadButton = new Button("reload button");

		mainPanel.add(newIndicatorButton);
		mainPanel.add(newGroupButton);
		mainPanel.add(reloadButton);

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
		
	}
	
	public void setProjectPresenter(ProjectPresenter projectPresenter) {
		this.projectPresenter = projectPresenter;
	}


	private ActivityDTO getCurrentActivity() {
		if (db.getActivities() != null && db.getActivities().size() > 0) {
			// TODO fix me
			return db.getActivities().get(0);
		}
		return null;
	}

	@Override
	public Component getView() {
		this.designPanel.setProjectPresenter(this.projectPresenter);
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
