package org.sigmah.client.page.admin.model.common.element;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.common.element.AdminFlexibleElementsPresenter.View;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.UpdateModelResult;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminFlexibleElementsView extends View {	
	
	private final ListStore<FlexibleElementDTO> fieldsStore;
	private ProjectModelDTO projectModel;
	private OrgUnitModelDTO orgUnitModel;
	private final Dispatcher dispatcher;
	private final Grid<FlexibleElementDTO> flexGrid;

	public AdminFlexibleElementsView(Dispatcher dispatcher){	
		this.dispatcher = dispatcher;
		this.fieldsStore = new ListStore<FlexibleElementDTO>();		
		//final VBoxLayout mainPanelLayout = new StylableVBoxLayout(STYLE_MAIN_BACKGROUND);
        //mainPanelLayout.setVBoxLayoutAlign(VBoxLayout.VBoxLayoutAlign.STRETCH);
        setLayout(new FitLayout());
        setHeaderVisible(false);
        setBorders(false);
        setBodyBorder(false);	
        
        
        final VBoxLayoutData topVBoxLayoutData = new VBoxLayoutData();
        topVBoxLayoutData.setFlex(1.0);
        
        flexGrid = buildFieldsListGrid();
        flexGrid.setSelectionModel(new GridSelectionModel<FlexibleElementDTO>());
        flexGrid.setAutoHeight(true);
        flexGrid.getView().setForceFit(true);
        setTopComponent(initToolBar());
        
        add(flexGrid, topVBoxLayoutData);
        this.layout();
	}
	
	private ToolBar initToolBar() {
		
		ToolBar toolbar = new ToolBar();
    	
		Button button = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
        button.setItemId(UIActions.add);
		button.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				AdminFlexibleElementActionListener listener  = new AdminFlexibleElementActionListener(AdminFlexibleElementsView.this, dispatcher);
				listener.onUIAction(UIActions.add);
			}
			
		});
		
		Button groupButton = new Button(I18N.CONSTANTS.adminFlexibleAddGroup(), IconImageBundle.ICONS.add());
        groupButton.setItemId(UIActions.edit);
		groupButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				AdminFlexibleElementActionListener listener  = new AdminFlexibleElementActionListener(AdminFlexibleElementsView.this, dispatcher);
				listener.onUIAction(UIActions.edit);
			}
			
		});
		
		Button deleteButton = new Button(I18N.CONSTANTS.adminFlexibleDeleteFlexibleElements(), IconImageBundle.ICONS.delete());
		deleteButton.setItemId(UIActions.delete);
		deleteButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				AdminFlexibleElementActionListener listener  = new AdminFlexibleElementActionListener(AdminFlexibleElementsView.this, dispatcher);
				listener.onUIAction(UIActions.delete);
			}
			
		});
		
		toolbar.add(button);
		toolbar.add(groupButton);
		toolbar.add(deleteButton);
		
	    return toolbar;
    }
	
	public void showNewGroupForm(final FlexibleElementDTO model,
			final boolean isUpdate) {
		int width = 400;
		int height = 400;
		String title = I18N.CONSTANTS.adminFlexibleGroup();
		final Window window = new Window();		
		window.setHeading(title);
        window.setSize(width, height);
        window.setPlain(true);
        window.setModal(true);
        window.setBlinkModal(true);
        window.setLayout(new FitLayout());
		final LayoutGroupSigmahForm form = new LayoutGroupSigmahForm(dispatcher, new AsyncCallback<CreateResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				window.hide();				
			}

			@Override
			public void onSuccess(CreateResult result) {
				//FIXME add new group and update model
				window.hide();
				if(result != null){
					LayoutGroupDTO group = (LayoutGroupDTO)result.getEntity();
					if(projectModel != null){
						if(projectModel.getProjectDetailsDTO().getLayoutDTO().getId() == group.getParentLayoutDTO().getId()){
							projectModel.getProjectDetailsDTO().setLayoutDTO(group.getParentLayoutDTO());
						}else{
							for(PhaseModelDTO phaseModel : projectModel.getPhaseModelsDTO()){
								if(phaseModel.getLayoutDTO().getId() == group.getParentLayoutDTO().getId()){
									phaseModel.setLayoutDTO(group.getParentLayoutDTO());
								}
							}
						}
						
					}else if(orgUnitModel != null){
						if(orgUnitModel.getDetails().getLayout().getId() == group.getParentLayoutDTO().getId()){
							orgUnitModel.getDetails().setLayout(group.getParentLayoutDTO());
						}
					}
				}				
			}			
		}, model , projectModel, orgUnitModel);
		window.add(form);
        window.show();
	}
	
	public void showNewFlexibleElementForm(final FlexibleElementDTO model,
			final boolean isUpdate) {
		int width = 800;
		int height = 650;
		String title = I18N.CONSTANTS.adminFlexible();
		final Window window = new Window();		
		window.setHeading(title);
        window.setSize(width, height);
        window.setPlain(true);
        window.setModal(true);
        window.setBlinkModal(true);
        window.setLayout(new FitLayout());
        final ElementForm form = new ElementForm(dispatcher, new AsyncCallback<UpdateModelResult>(){

			@Override
			public void onFailure(Throwable caught) {
				window.hide();
			}

			@Override
			public void onSuccess(UpdateModelResult result) {
				if(!isUpdate){
					window.hide();
					AdminFlexibleElementsView.this.getFieldsStore().add((FlexibleElementDTO)result.getAnnexEntity());
					AdminFlexibleElementsView.this.getFieldsStore().commitChanges();
				}else{
					window.hide();
					ProjectModelDTO projectModelUpdated = (ProjectModelDTO)result.getEntity();
					AdminFlexibleElementsView.this.setModel(projectModelUpdated);
					AdminFlexibleElementsView.this.getFieldsStore().remove(model);
					AdminFlexibleElementsView.this.getFieldsStore().add((FlexibleElementDTO)result.getAnnexEntity());
					AdminFlexibleElementsView.this.getFieldsStore().commitChanges();
				}				
			}


			
		}, model, projectModel, orgUnitModel);
		window.add(form);
        window.show();
	}
	
	private Grid<FlexibleElementDTO> buildFieldsListGrid(){
		
		
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		  
        ColumnConfig 
		column = new ColumnConfig("id",I18N.CONSTANTS.adminFlexibleFieldId(), 50);   
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {	
				return AdminUtil.createGridText(String.valueOf(model.getId()));
			}	    	
	    });
		configs.add(column);
				
		column = new ColumnConfig("label",I18N.CONSTANTS.adminFlexibleName(), 200);   
		configs.add(column);
		
		column = new ColumnConfig("type",I18N.CONSTANTS.adminFlexibleType(), 100);   
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {	
				return AdminUtil.createGridText(ElementTypeEnum.getName(model.getElementType()));
			}	    	
	    });
		configs.add(column);
				
		column = new ColumnConfig("validates",I18N.CONSTANTS.adminFlexibleCompulsory(), 50);   
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {	
				CheckBox validates = AdminUtil.createCheckBox(I18N.CONSTANTS.adminFlexibleCompulsory(), null);
				validates.setValue(model.getValidates());
				return validates;
			}	    	
	    }); 
		configs.add(column);
		
		column = new ColumnConfig("privacyGroup",I18N.CONSTANTS.adminProfilesPrivacyGroups(), 100);   
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {	
				PrivacyGroupDTO privacy = model.getPrivacyGroup(); 
				if(privacy!= null)
					return AdminUtil.createGridText(privacy.getTitle());
				else
					return AdminUtil.createGridText("");
			}	    	
	    }); 
		configs.add(column);
		
		column = new ColumnConfig("amendable",I18N.CONSTANTS.adminFlexibleAmendable(), 50);   
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {	
				CheckBox amendable = AdminUtil.createCheckBox(I18N.CONSTANTS.adminFlexibleAmendable(), null);
				amendable.setValue(model.getAmendable());
				return amendable;
			}	    	
	    });
		configs.add(column);
		
		column = new ColumnConfig("banner",I18N.CONSTANTS.Admin_BANNER(), 50);   
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {					
				CheckBox banner = AdminUtil.createCheckBox(I18N.CONSTANTS.Admin_BANNER(), null);
				if(model.getBannerConstraint()!=null){
					banner.setValue(true);
				}
				return banner;
			}	    	
	    });
		configs.add(column);
		
		column = new ColumnConfig("bannerPos",I18N.CONSTANTS.adminFlexibleBannerPosition(), 50);   
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {					
				if(model.getBannerConstraint()!=null){
					int order = model.getConstraint().getSortOrder();
					return AdminUtil.createGridText(String.valueOf(order));
				}
				return AdminUtil.createGridText("");
			}	    	
	    });
		configs.add(column);
		
		column = new ColumnConfig("container",I18N.CONSTANTS.adminFlexibleContainer(), 120);   
        column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {	
				BaseModelData container = model.getContainerModel();
				
				return AdminUtil.createGridText((String)container.get("name"));
			}
	    	
	    }); 
		configs.add(column);
		
		column = new ColumnConfig("groupId",I18N.CONSTANTS.adminFlexibleGroup(), 50);   
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {	
				LayoutGroupDTO group = model.getGroup();
				
				return AdminUtil.createGridText(String.valueOf((Integer)group.get("id")));
			}	    	
	    }); 
		configs.add(column);
		
		column = new ColumnConfig("group",I18N.CONSTANTS.adminFlexibleGroup(), 200);   
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {	
				LayoutGroupDTO group = model.getGroup();
				
				return AdminUtil.createGridText((String)group.get("title"));
			}	    	
	    }); 
		configs.add(column);
		
		column = new ColumnConfig();
		column.setWidth(120);   		
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(final FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {	
				Button groupButton = new Button(I18N.CONSTANTS.adminFlexibleUpdateGroup());
		        groupButton.setItemId(UIActions.edit);
				groupButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

					@Override
					public void handleEvent(ButtonEvent be) {
						showNewGroupForm(model, true);
					}
					
				});
				return groupButton;
			}
	    	
	    }); 
		configs.add(column);
		
		column = new ColumnConfig();
		column.setWidth(100);   		
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(final FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {
				
				Button button = new Button(I18N.CONSTANTS.edit());
		        button.setItemId(UIActions.edit);
		        button.addListener(Events.OnClick, new Listener<ButtonEvent>(){

					@Override
					public void handleEvent(ButtonEvent be) {
						showNewFlexibleElementForm(model,true);
					}
				});		        		   
		        
				return button;			
			}
	    	
	    }); 
		configs.add(column);
		
		
		
		ColumnModel cm = new ColumnModel(configs);		
		fieldsStore.setSortField("container");
		Grid<FlexibleElementDTO> grid = new Grid<FlexibleElementDTO>(fieldsStore, cm); 
		return grid;
	}

	public ListStore<FlexibleElementDTO> getFieldsStore() {
		return fieldsStore;
	}


	@Override
	public Component getMainPanel() {
		return this;
	}


	@Override
	public void setProjectModel(ProjectModelDTO model) {
		this.projectModel = model;
	}


	@Override
	public ProjectModelDTO getProjectModel() {
		return projectModel;
	}
	
	@Override
	public void setOrgUnitModel(OrgUnitModelDTO model) {
		this.orgUnitModel = model;
	}


	@Override
	public OrgUnitModelDTO getOrgUnitModel() {
		return orgUnitModel;
	}

	@Override
	public List<FlexibleElementDTO> getDeleteSelection() {
		GridSelectionModel<FlexibleElementDTO> sm = flexGrid.getSelectionModel();
        return sm.getSelectedItems();
	}
	
	@Override
	public void confirmDeleteSelected(ConfirmCallback confirmCallback) {
		confirmCallback.confirmed();		
	}
}
