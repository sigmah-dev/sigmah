package org.sigmah.client.page.project.design;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.config.design.DesignPanel;
import org.sigmah.client.page.config.design.ProjectSiteGridPanel;
import org.sigmah.client.page.entry.SiteMap;
import org.sigmah.client.page.entry.editor.SiteForm;
import org.sigmah.client.page.entry.editor.SiteFormDialog;
import org.sigmah.client.page.project.ProjectSubPresenter;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;
import org.sigmah.shared.report.model.DimensionType;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.inject.Inject;

public class ProjectIndicatorsContainer extends LayoutContainer implements ProjectSubPresenter {

	//private SiteEditor siteEditor;
	private DesignPanel designPanel;
	private SchemaDTO schema;
	private UserDatabaseDTO db;
	private final Dispatcher service;
	private final EventBus eventBus;
	
	private ContentPanel mapContainer;
	private TreeStore<ModelData> treeStore;	
	private TabPanel tabPanel;
	private TabItem mapTabItem;
	private TabItem sitesTabItem;
	
	private ProjectSiteGridPanel siteEditor;
	private SiteMap siteMap;
	
	private ProjectDTO project;
	
	
		
	@Inject
	public ProjectIndicatorsContainer(
			ProjectSiteGridPanel siteEditor,
			SiteMap siteMap,
			final DesignPanel designPanel, 
			Dispatcher service, EventBus eventBus) {
		
		this.siteEditor = siteEditor;
		this.siteMap = siteMap;
		this.designPanel = designPanel;
		this.designPanel.setHeaderVisible(false);
		
		this.service = service;
		this.eventBus = eventBus;
		
		
		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setContainerStyle("x-border-layout-ct main-background");
		setLayout(borderLayout);

		// setIcon(IconImageBundle.ICONS.design());
		// map tab panel
		tabPanel = new TabPanel();
		tabPanel.setPlain(true);

		// map tab item
		mapTabItem = new TabItem(I18N.CONSTANTS.map());
		mapTabItem.setLayout(new FitLayout());
		mapTabItem.setEnabled(false);
		mapTabItem.setAutoHeight(true);
		mapTabItem.setEnabled(true);
		mapTabItem.add(siteMap);
		tabPanel.add(mapTabItem);

		// sites tab item
		sitesTabItem = new TabItem(I18N.CONSTANTS.sites());
		sitesTabItem.setLayout(new FitLayout());
		sitesTabItem.setEnabled(false);
		sitesTabItem.setAutoHeight(true);
		sitesTabItem.setEnabled(true);
		sitesTabItem.add(siteEditor);
		tabPanel.add(sitesTabItem);

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
		designPanel.getMappedIndicator().addValueChangeHandler(new ValueChangeHandler<IndicatorDTO>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<IndicatorDTO> event) {
				onMappedIndicatorChanged(event.getValue());
			}
		});
		
		add(tabPanel, layout);
		
		siteEditor.addActionListener(new Listener<ComponentEvent>() {
			
			@Override
			public void handleEvent(ComponentEvent be) {
				if(UIActions.add.equals(be.getComponent().getItemId() )) {
					addSite();
				} else if(UIActions.edit.equals(be.getComponent().getItemId())) {
					editSite();
				}
			}
		});
	}

	@Override
	public void loadProject(ProjectDTO project) {
		this.project = project;
		
		// load design panel
		designPanel.load(project.getId());
		
		// load site grid
		Filter siteFilter = new Filter();
		siteFilter.addRestriction(DimensionType.Database, project.getId());
		siteEditor.load(siteFilter);			
	}
	
	private void onMappedIndicatorChanged(IndicatorDTO value) {
		Filter filter = new Filter();
		filter.addRestriction(DimensionType.Indicator, value.getId());
		
		siteMap.loadSites(project.getCountry(),	filter);
	}

	private void addSite() {

		final int projectId = project.getId();

		SiteDTO site = new SiteDTO();
		site.setDatabaseId(projectId);
		//site.setPartner(projectPresenter.getCurrentProjectDTO().getOrgUnitId());
		
		final SiteForm form = new SiteForm(service, project.getCountry());
		
		final SiteFormDialog dialog = new SiteFormDialog(eventBus, service, form);
		dialog.create(site);
	}
	
	private void editSite() {
		if(!siteEditor.getSelection().isEmpty()) {
			SiteDTO site = siteEditor.getSelection().get(0);
			final SiteForm form = new SiteForm(service, project.getCountry());
			
			final SiteFormDialog dialog = new SiteFormDialog(eventBus, service, form);
			dialog.edit(site);
		}	
	}
	
	@Override
	public Component getView() {
		return (Component) this;
	}

	@Override
	public void viewDidAppear() {
	}

	@Override
	public void discardView() {		
	}


}
