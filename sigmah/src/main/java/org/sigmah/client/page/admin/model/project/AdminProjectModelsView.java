package org.sigmah.client.page.admin.model.project;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.EventBus;
import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.event.NavigationEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.model.common.AdminModelActionListener;
import org.sigmah.client.page.admin.model.project.AdminProjectModelsPresenter.AdminModelsStore;
import org.sigmah.client.page.admin.model.project.AdminProjectModelsPresenter.View;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.project.dashboard.funding.FundingIconProvider;
import org.sigmah.client.page.project.dashboard.funding.FundingIconProvider.IconSize;
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

	private final static String STYLE_MAIN_BACKGROUND = "main-background";
	
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
		configs.add(column); 
		
		column = new ColumnConfig();    
		column.setWidth(75);  
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
						
						AdminProjectModelsView.this.eventBus.fireEvent(new NavigationEvent(
								NavigationHandler.NavigationRequested, derivation));					
					}		        	
		        });		        		        
				return button;				
			}	    	
	    }); 
	    configs.add(column); 
		
		ColumnModel cm = new ColumnModel(configs);		
		
		Grid<ProjectModelDTOLight> grid = new Grid<ProjectModelDTOLight>(modelsStore, cm); 
		
		return grid;
	}
	
	private ToolBar initToolBar() {
		
		ToolBar toolbar = new ToolBar();
    	
		Button button = new Button(I18N.CONSTANTS.addItem());
        button.setItemId(UIActions.add);
		button.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				AdminModelActionListener listener  = new AdminModelActionListener(AdminProjectModelsView.this, dispatcher);
				listener.onUIAction(UIActions.add);
			}
			
		});
		toolbar.add(button);
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
		mainPanel.setTitle("models");	
		//if(id != -1){
			//mainPanel.remove(grid);
			//AdminOneModelPresenter modelPresenter = new AdminOneModelPresenter();
			//mainPanel.add((Widget)modelPresenter.getWidget());
		//}
		return mainPanel;			
	}

	@Override
	public void setCurrentState(AdminPageState currentState) {
		this.currentState = currentState;
	}
}
