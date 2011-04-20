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
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Image;
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
		final VBoxLayout mainPanelLayout = new VBoxLayout();
        mainPanelLayout.setVBoxLayoutAlign(VBoxLayout.VBoxLayoutAlign.STRETCH);
        mainPanel.setLayout(mainPanelLayout);
        mainPanel.setHeaderVisible(false);
        mainPanel.setBorders(false);
        mainPanel.setBodyBorder(false);
        
        //ContentPanel modelsListPanel = new ContentPanel();
        //modelsListPanel.setTitle(I18N.CONSTANTS.adminProjectModelsPanel());
        final VBoxLayoutData topVBoxLayoutData = new VBoxLayoutData();
        topVBoxLayoutData.setFlex(1.0);
        
        modelsStore = new AdminModelsStore();
        grid = buildModelsListGrid();
		
		grid.setAutoHeight(true);
		grid.getView().setForceFit(true);
		
		mainPanel.setTopComponent(initToolBar());
		
		//modelsListPanel.add(grid);
		mainPanel.add(grid, topVBoxLayoutData);
	}
	
	private Grid<ProjectModelDTOLight> buildModelsListGrid(){
		
		
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		  
        ColumnConfig column = new ColumnConfig("visibility",I18N.CONSTANTS.adminProjectModelsUse(),50);  
		column.setRenderer(new GridCellRenderer<ProjectModelDTOLight>(){

			@Override
			public Object render(ProjectModelDTOLight model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ProjectModelDTOLight> store, Grid<ProjectModelDTOLight> grid) {
				ProjectModelType type = model.getVisibility(cache.getOrganizationCache().getOrganization().getId());
				
				Image icon = FundingIconProvider.getProjectTypeIcon(type, IconSize.MEDIUM).createImage();
				return icon;
			}
			
		});  
		configs.add(column);
		
		column = new ColumnConfig("name",I18N.CONSTANTS.adminProjectModelsName(), 400);   
		configs.add(column); 
		
		column = new ColumnConfig("status",I18N.CONSTANTS.adminProjectModelsStatus(), 400); 
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
		column.setAlignment(Style.HorizontalAlignment.RIGHT);
	    column.setRenderer(new GridCellRenderer<ProjectModelDTOLight>(){

			@Override
			public Object render(final ProjectModelDTOLight model, final String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ProjectModelDTOLight> store, Grid<ProjectModelDTOLight> grid) {
				
				Button button = new Button(I18N.CONSTANTS.edit());
		        button.setItemId(UIActions.edit);
		        button.addListener(Events.OnClick, new Listener<ButtonEvent>(){

					@Override
					public void handleEvent(ButtonEvent be) {
						
						final AdminPageState derivation = new AdminPageState(AdminProjectModelsView.this.currentState.getCurrentSection());
						derivation.setModel(model.getId());
						//FIXME
						derivation.setSubModel(I18N.CONSTANTS.adminProjectModelFields());
						derivation.setIsProject(true);
						AdminProjectModelsView.this.eventBus.fireEvent(new NavigationEvent(
								NavigationHandler.NavigationRequested, derivation));					
					}		        	
		        });		        		        
				return button;				
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
