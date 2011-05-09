package org.sigmah.client.page.admin.model.project;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.EventBus;
import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.event.NavigationEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.model.common.AdminModelActionListener;
import org.sigmah.client.page.admin.model.project.AdminProjectModelsPresenter.AdminModelsStore;
import org.sigmah.client.page.admin.model.project.AdminProjectModelsPresenter.View;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.project.dashboard.funding.FundingIconProvider;
import org.sigmah.client.page.project.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.dto.ProjectModelDTOLight;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.inject.Inject;

public class AdminProjectModelsView extends View {

	private final ContentPanel mainPanel;
	private final Grid<ProjectModelDTOLight> grid;
	private final AdminModelsStore modelsStore;
	private final EventBus eventBus;
	private AdminPageState currentState;
	private final UserLocalCache cache;
	private final Dispatcher dispatcher;
	
	@Inject
	public AdminProjectModelsView(Dispatcher dispatcher, UserLocalCache cache, EventBus eventBus) {
		this.dispatcher = dispatcher;
		this.cache = cache;
		this.eventBus = eventBus;
		
		mainPanel = new ContentPanel(new FitLayout());
        mainPanel.setHeaderVisible(false);
        mainPanel.setBorders(false);
        mainPanel.setBodyBorder(false);
        
        final VBoxLayoutData topVBoxLayoutData = new VBoxLayoutData();
        topVBoxLayoutData.setFlex(1.0);
        
        modelsStore = new AdminModelsStore();
        grid = buildModelsListGrid();
		
		grid.setAutoHeight(true);
		grid.getView().setForceFit(true);
		
		mainPanel.setTopComponent(initToolBar());
		mainPanel.setScrollMode(Style.Scroll.AUTO);
		
		mainPanel.add(grid);
	}
	
	private Grid<ProjectModelDTOLight> buildModelsListGrid(){
		
		
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		  
        ColumnConfig column = new ColumnConfig("name",I18N.CONSTANTS.adminProjectModelsName(), 300);   
		column.setRenderer(new GridCellRenderer<ProjectModelDTOLight>(){

			@Override
			public Object render(final ProjectModelDTOLight model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ProjectModelDTOLight> store, Grid<ProjectModelDTOLight> grid) {
				
				final AdminPageState derivation = new AdminPageState(AdminProjectModelsView.this.currentState.getCurrentSection());
				derivation.setModel(model.getId());
				//FIXME
				derivation.setSubModel(I18N.CONSTANTS.adminProjectModelFields());
				derivation.setIsProject(true);
				String link = derivation.getPageId().toString() + "/" + derivation.serializeAsHistoryToken();
				
				final Hyperlink h = new Hyperlink((String) model.get(property), true, link);
				
				final Anchor nameHyperlink ;
				nameHyperlink = new Anchor(model.getName(), true);
                nameHyperlink.setStyleName("hyperlink");
                nameHyperlink.addStyleName("project-grid-leaf");
                nameHyperlink.addClickHandler(new ClickHandler(){

					@Override
					public void onClick(ClickEvent event) {
						final AdminPageState derivation = new AdminPageState(AdminProjectModelsView.this.currentState.getCurrentSection());
						derivation.setModel(model.getId());
						//FIXME
						derivation.setSubModel(I18N.CONSTANTS.adminProjectModelFields());
						derivation.setIsProject(true);
						AdminProjectModelsView.this.eventBus.fireEvent(new NavigationEvent(
								NavigationHandler.NavigationRequested, derivation));
					}
                	
                });
                
                final com.google.gwt.user.client.ui.Grid panel = new com.google.gwt.user.client.ui.Grid(1, 2);
                panel.setCellPadding(0);
                panel.setCellSpacing(0);
                ProjectModelType type = model.getVisibility(cache.getOrganizationCache().getOrganization().getId()); 
                panel.setWidget(
                        0,
                        0,
                        FundingIconProvider.getProjectTypeIcon(type, IconSize.MEDIUM).createImage());
                panel.getCellFormatter().addStyleName(0, 0, "project-grid-code-icon");
                panel.setWidget(0, 1, nameHyperlink);
                panel.getCellFormatter().addStyleName(0, 1, "project-grid-code");

                return panel;
                //return nameHyperlink;
			}
        });
		configs.add(column); 
		
		column = new ColumnConfig("status",I18N.CONSTANTS.adminProjectModelsStatus(), 300); 
		column.setRenderer(new GridCellRenderer<ProjectModelDTOLight>(){

			@Override
			public Object render(ProjectModelDTOLight model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ProjectModelDTOLight> store, Grid<ProjectModelDTOLight> grid) {
				return model.getStatus()!=null ? ProjectModelStatus.getName(model.getStatus()) : "";
			}
		});
		configs.add(column); 
	    
	   
		column = new ColumnConfig();
		column.setWidth(40);
		column.setAlignment(Style.HorizontalAlignment.CENTER);
		column.setRenderer(new GridCellRenderer<ProjectModelDTOLight>() {
			@Override
			public Object render(final ProjectModelDTOLight model,
					final String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<ProjectModelDTOLight> store,
					Grid<ProjectModelDTOLight> grid) {

				Button buttonExport = new Button(I18N.CONSTANTS.export());
				buttonExport.setItemId(UIActions.exportModel);
				buttonExport.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {
							@Override
							public void handleEvent(ButtonEvent be) {
								AdminModelActionListener listener = new AdminModelActionListener(
										AdminProjectModelsView.this,
										dispatcher, true);
								listener.setModelId(model.getId());
								listener.setIsOrgUnit(false);
								listener.setIsReport(false);
								listener.onUIAction(UIActions.exportModel);
							}
						});
				return buttonExport;
			}
		});
		configs.add(column);
		
		
		column = new ColumnConfig();
		column.setWidth(40);
		column.setAlignment(Style.HorizontalAlignment.CENTER);
		column.setRenderer(new GridCellRenderer<ProjectModelDTOLight>() {
			@Override
			public Object render(final ProjectModelDTOLight model,
					final String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<ProjectModelDTOLight> store,
					Grid<ProjectModelDTOLight> grid) {

				Button buttonCopy = new Button(I18N.CONSTANTS.adminModelCopy());
				buttonCopy.setItemId(UIActions.copyModel);
				buttonCopy.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {
							@Override
							public void handleEvent(ButtonEvent be) {
								AdminModelActionListener listener = new AdminModelActionListener(
										AdminProjectModelsView.this,
										dispatcher, true);
								listener.setModelId(model.getId());
								listener.setIsOrgUnit(false);
								listener.onUIAction(UIActions.copyModel);
							}
						});
				return buttonCopy;
			}
		});
		configs.add(column);

		
		ColumnModel cm = new ColumnModel(configs);		
		
		Grid<ProjectModelDTOLight> grid = new Grid<ProjectModelDTOLight>(modelsStore, cm); 
		
		return grid;
	}
	
	private ToolBar initToolBar() {
		
		ToolBar toolbar = new ToolBar();
    	
		Button button = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
        button.setItemId(UIActions.add);
		button.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				AdminModelActionListener listener  = new AdminModelActionListener(AdminProjectModelsView.this, dispatcher, true);
				listener.onUIAction(UIActions.add);
			}
			
		});
		toolbar.add(button);
		
		Button buttonImport = new Button(I18N.CONSTANTS.importItem());
		buttonImport.setItemId(UIActions.importModel);
		buttonImport.addListener(Events.Select, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				AdminModelActionListener listener = new AdminModelActionListener(
						AdminProjectModelsView.this, dispatcher, true);
				listener.setIsOrgUnit(false);
				listener.setIsReport(false);
				listener.onUIAction(UIActions.importModel);
			}

		});
		toolbar.add(buttonImport);
		
	    return toolbar;
    }
	
	@Override
	public MaskingAsyncMonitor getProjectModelsLoadingMonitor() {
		return new MaskingAsyncMonitor(grid, I18N.CONSTANTS.loading());
	}

	@Override
	public AdminModelsStore getAdminModelsStore() {
		return modelsStore;
	}

	@Override
	public Component getMainPanel(int id) {
		return mainPanel;			
	}

	@Override
	public void setCurrentState(AdminPageState currentState) {
		this.currentState = currentState;
	}
	
}
