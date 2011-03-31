package org.sigmah.client.page.admin.model.common;

import org.sigmah.client.EventBus;
import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.event.NavigationEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.AdminPresenter;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.client.page.admin.model.common.element.AdminFlexibleElementsPresenter;
import org.sigmah.client.page.admin.model.project.logframe.AdminLogFramePresenter;
import org.sigmah.client.page.admin.model.project.phase.AdminPhasesPresenter;
import org.sigmah.client.util.state.IStateManager;
import org.sigmah.shared.command.GetOrgUnitModel;
import org.sigmah.shared.command.GetProjectModel;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

public class AdminOneModelPresenter{
	private final static String[] MAIN_TABS = { I18N.CONSTANTS.adminProjectModelFields(),
		I18N.CONSTANTS.adminProjectModelPhases(), I18N.CONSTANTS.adminProjectModelLogFrame()};

    private final View view;
    private final Dispatcher dispatcher;
    private final EventBus eventBus;
    private AdminPageState currentState;
    private TabItem currentTabItem;
    private final AdminModelSubPresenter[] presenters;
    private ProjectModelDTO currentProjectModel;
    private OrgUnitModelDTO currentOrgUnitModel;
    
    @ImplementedBy(AdminOneModelView.class)
    public interface View {

		public Widget getMainPanel();
		
		public TabPanel getTabPanelParameters();
		
		public LayoutContainer getPanelSelectedTab();
		
		public void initModelView(Object model);
    }
    
    
    @Inject
    public AdminOneModelPresenter(final EventBus eventBus, final Dispatcher dispatcher, final View view,
            final UserLocalCache cache, final Authentication authentication, IStateManager stateMgr) {
    	this.dispatcher = dispatcher;
        this.view = view;
        this.presenters = new AdminModelSubPresenter[] { 
        		new AdminFlexibleElementsPresenter(dispatcher), 
        		new AdminPhasesPresenter(dispatcher),
        		new AdminLogFramePresenter(dispatcher)};
        this.eventBus = eventBus;
        
    }

    public void setCurrentState(AdminPageState currentState) {
		this.currentState = currentState;
	}
    
    private void addAllTabs(){
    	for (int i = 0; i < MAIN_TABS.length; i++) {
            final int index = i;
            String tabTitle = MAIN_TABS[i];
            
            final TabItem tabItem = new TabItem(tabTitle);
            tabItem.setLayout(new FitLayout());
            tabItem.setEnabled(true);
            tabItem.setAutoHeight(true);
            
            tabItem.addListener(Events.Select, new Listener<ComponentEvent>() {

				@Override
				public void handleEvent(ComponentEvent be) {
					final TabItem item = AdminOneModelPresenter.this.view.getTabPanelParameters().getItem(index);
					
					if(!item.equals(currentTabItem)){
						eventBus.fireEvent(new NavigationEvent(NavigationHandler.NavigationRequested, currentState
                            .deriveTo(currentState.getCurrentSection(), currentState.getModel(), MAIN_TABS[index], currentState.isProject())));
					}
				}
            	
            });
            
			this.view.getTabPanelParameters().add(tabItem);
            
        }
    }
    
    private void addSingleTab(Integer i){
    	final int index = i;
        String tabTitle = MAIN_TABS[i];
        
        final TabItem tabItem = new TabItem(tabTitle);
        tabItem.setLayout(new FitLayout());
        tabItem.setEnabled(true);
        tabItem.setAutoHeight(true);
        
        tabItem.addListener(Events.Select, new Listener<ComponentEvent>() {

			@Override
			public void handleEvent(ComponentEvent be) {
				final TabItem item = AdminOneModelPresenter.this.view.getTabPanelParameters().getItem(index);
				
				if(!item.equals(currentTabItem)){
					eventBus.fireEvent(new NavigationEvent(NavigationHandler.NavigationRequested, currentState
                        .deriveTo(currentState.getCurrentSection(), currentState.getModel(), MAIN_TABS[index], currentState.isProject())));
				}
			}
        	
        });
        
		this.view.getTabPanelParameters().add(tabItem);  
    }

	
	public boolean navigate(PageState place, final AdminPresenter.View view) {
		
		final AdminPageState adminPageState = (AdminPageState) place;
        currentState = adminPageState;
        if(currentState.isProject()){
        	addAllTabs();
        	GetProjectModel command = new GetProjectModel(currentState.getModel());
            command.setId(currentState.getModel().intValue());
            dispatcher.execute(command, null, new AsyncCallback<ProjectModelDTO>(){

    			@Override
    			public void onFailure(Throwable throwable) {
    				//FIXME
    			}

    			@Override
    			public void onSuccess(ProjectModelDTO model) {
    				AdminOneModelPresenter.this.setCurrentProjectModel(model);
    				if(model != null)
    					selectTab(currentState.getSubModel(), view, model, false, true);
    				else{
    					//FIXME	
    				}
    			}        	
            });
        }else{
        	addSingleTab(0);
        	GetOrgUnitModel command = new GetOrgUnitModel(currentState.getModel());
            command.setId(currentState.getModel().intValue());
            dispatcher.execute(command, null, new AsyncCallback<OrgUnitModelDTO>(){

    			@Override
    			public void onFailure(Throwable throwable) {
    				//FIXME
    			}

    			@Override
    			public void onSuccess(OrgUnitModelDTO model) {
    				AdminOneModelPresenter.this.setCurrentOrgUnitModel(model);
    				if(model != null)
    					selectTab(currentState.getSubModel(), view, model, false, false);
    				else{
    					//FIXME	
    				}
    			}        	
            });
        }
        
        
        return true;
	}
	
	private void selectTab(String subModel, AdminPresenter.View adminView, Object model, boolean force, boolean isProject) {
    	
		int index = arrayIndexOf(MAIN_TABS, subModel);
		if(index != -1){
			
			final TabItem item = this.view.getTabPanelParameters().getItem(index);
			
			if(!item.equals(currentTabItem)){
				currentTabItem = item;
				
				presenters[index].setCurrentState(currentState);
		        presenters[index].setModel(model);
		        
	         	this.view.getTabPanelParameters().setSelection(this.view.getTabPanelParameters().getItem(index));
	         	LayoutContainer l = this.view.getPanelSelectedTab();
	         	l.add(presenters[index].getView());
	         	l.setScrollMode(Style.Scroll.AUTO);
	         	this.view.getTabPanelParameters().getSelectedItem().add(l);
	         	view.initModelView(model);
		        adminView.setMainPanel(view.getMainPanel());
		        if(isProject)
		        	this.setCurrentProjectModel((ProjectModelDTO)presenters[index].getModel());
		        else
		        	this.setCurrentOrgUnitModel((OrgUnitModelDTO)presenters[index].getModel());
		        presenters[index].viewDidAppear();
		        
	        }else if (force) { 
	        	presenters[index].setCurrentState(currentState);
		        presenters[index].setModel(model);
		        
	        	this.view.getTabPanelParameters().setSelection(this.view.getTabPanelParameters().getItem(index));
	         	LayoutContainer l = this.view.getPanelSelectedTab();
	         	l.add(presenters[index].getView());
	         	this.view.getTabPanelParameters().getSelectedItem().add(l);
	         	view.initModelView(model);
		        adminView.setMainPanel(view.getMainPanel());
		        if(isProject)
		        	this.setCurrentProjectModel((ProjectModelDTO)presenters[index].getModel());
		        else
		        	this.setCurrentOrgUnitModel((OrgUnitModelDTO)presenters[index].getModel());
	        	presenters[index].viewDidAppear(); 
	        }
		}                
    }
	
	public int arrayIndexOf(Object[] array, Object o){
		int index = -1;
		for(int i= 0; i<array.length; i++){
			if(o.equals(array[i])){
				index = i;
				break;
			}
		}
		return index;
	}

	public void setCurrentProjectModel(ProjectModelDTO currentProjectModel) {
		this.currentProjectModel = currentProjectModel;
	}

	public ProjectModelDTO getCurrentProjectModel() {
		return currentProjectModel;
	}
	
	public void setCurrentOrgUnitModel(OrgUnitModelDTO currentOrgUnitModel) {
		this.currentOrgUnitModel = currentOrgUnitModel;
	}

	public OrgUnitModelDTO getCurrentOrgUnitModel() {
		return currentOrgUnitModel;
	}
	
}
