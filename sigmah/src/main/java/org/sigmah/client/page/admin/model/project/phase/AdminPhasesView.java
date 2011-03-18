package org.sigmah.client.page.admin.model.project.phase;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.project.phase.AdminPhasesPresenter.View;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminPhasesView extends View {	

	private final ListStore<PhaseModelDTO> phaseStore;
	private ProjectModelDTO projectModel;
	private final Dispatcher dispatcher;

	public AdminPhasesView(Dispatcher dispatcher){	
		this.dispatcher = dispatcher;
		this.phaseStore = new ListStore<PhaseModelDTO>();		
		this.setLayout(new FitLayout());
        setHeaderVisible(false);
        setBorders(false);
        setBodyBorder(false);	
        
        final VBoxLayoutData topVBoxLayoutData = new VBoxLayoutData();
        topVBoxLayoutData.setFlex(1.0);
        
        Grid<PhaseModelDTO> grid = buildModelsListGrid();
        grid.setAutoHeight(true);
        
        add(grid, topVBoxLayoutData);
	}
	
	
	private Grid<PhaseModelDTO> buildModelsListGrid(){
		
		
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		  
        ColumnConfig column = new ColumnConfig("displayOrder",I18N.CONSTANTS.adminPhaseOrder(), 50);   
		configs.add(column);
		
		column = new ColumnConfig("name",I18N.CONSTANTS.adminPhaseName(), 200);   
		configs.add(column); 
		
		column = new ColumnConfig("successorsDTO",I18N.CONSTANTS.adminPhaseSuccessors(), 400);   		
		column.setRenderer(new GridCellRenderer<PhaseModelDTO>(){

			@Override
			public Object render(PhaseModelDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<PhaseModelDTO> store, Grid<PhaseModelDTO> grid) {	
				List<String> listSuccess = new ArrayList<String>();
				for(PhaseModelDTO phase : model.getSuccessorsDTO()){
					listSuccess.add(phase.getName());
				}
				return AdminUtil.getInList(listSuccess, I18N.CONSTANTS.adminUsersNoProfiles());
				
			}
	    	
	    });  
		configs.add(column); 
		
		column = new ColumnConfig();
		column.setWidth(200);   		
		column.setRenderer(new GridCellRenderer<PhaseModelDTO>(){

			@Override
			public Object render(final PhaseModelDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<PhaseModelDTO> store, Grid<PhaseModelDTO> grid) {		
				
				Button button = new Button(I18N.CONSTANTS.edit());
		        button.setItemId(UIActions.edit);
		        button.addListener(Events.OnClick, new Listener<ButtonEvent>(){

					@Override
					public void handleEvent(ButtonEvent be) {
						FormPanel form = new PhaseSigmahForm(dispatcher, new AsyncCallback<CreateResult>(){

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onSuccess(CreateResult result) {
								ProjectModelDTO projectModelUpdated = (ProjectModelDTO)result.getEntity();
								AdminPhasesView.this.setModel(projectModelUpdated);
							}


							
						}, model, projectModel);
						int width = 400;
						int height = 400;
						String title = I18N.CONSTANTS.adminPhaseName();
						final Window window = new Window();		
						window.setHeading(title);
				        window.setSize(width, height);
				        window.setPlain(true);
				        window.setModal(true);
				        window.setBlinkModal(true);
				        window.setLayout(new FitLayout());
						window.add(form);
				        window.show();
					}
				});		        		        
				return button;			
			}
	    	
	    }); 
		configs.add(column); 
		
		ColumnModel cm = new ColumnModel(configs);		
		phaseStore.setSortField("displayOrder");
		Grid<PhaseModelDTO> grid = new Grid<PhaseModelDTO>(phaseStore, cm); 
		
		return grid;
	}
	
	private Object createUserGridText(String content) {
        final Text label = new Text(content);
        label.addStyleName("project-grid-leaf");
        
        return label;
    }

	public ListStore<PhaseModelDTO> getPhaseStore() {
		return phaseStore;
	}


	@Override
	public Component getMainPanel() {
		this.setTitle("phases");
		return this;
	}


	@Override
	public void setModel(ProjectModelDTO model) {
		this.projectModel = model;
	}


	@Override
	public ProjectModelDTO getModel() {
		return projectModel;
	}
}
