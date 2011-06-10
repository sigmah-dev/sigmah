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
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.MessageBox;
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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;

public class AdminFlexibleElementsView extends View {	
	
	private final ListStore<FlexibleElementDTO> fieldsStore;
	
	private final Dispatcher dispatcher;
	private final Grid<FlexibleElementDTO> flexGrid;
	private final List<LayoutGroupDTO> addedGroups = new ArrayList<LayoutGroupDTO>();

	public AdminFlexibleElementsView(Dispatcher dispatcher){	
		this.dispatcher = dispatcher;
		this.fieldsStore = new ListStore<FlexibleElementDTO>();		

        setLayout(new FitLayout());
        setHeaderVisible(false);
        setBorders(false);
        setBodyBorder(false);	
        
        
        final VBoxLayoutData topVBoxLayoutData = new VBoxLayoutData();
        topVBoxLayoutData.setMargins(new Margins(0,0,0,2));
        topVBoxLayoutData.setFlex(1.0);
        
        flexGrid = buildFieldsListGrid();
        flexGrid.setSelectionModel(new GridSelectionModel<FlexibleElementDTO>());
        flexGrid.setAutoHeight(true);
        flexGrid.getView().setForceFit(true);
        
        
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
		int height = 200;
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
					if(isUpdate){						
						List<FlexibleElementDTO> modifiedFlexs = AdminFlexibleElementsView.this.getFieldsStore().findModels("group", model.getGroup());
						for(FlexibleElementDTO modifiedFlex : modifiedFlexs){
							modifiedFlex.setGroup((LayoutGroupDTO) result.getEntity());
							AdminFlexibleElementsView.this.getFieldsStore().update(modifiedFlex);							
						}			
						AdminFlexibleElementsView.this.getFieldsStore().commitChanges();						
					}
					addedGroups.add((LayoutGroupDTO) result.getEntity());
				}				
			}			
		}, model , projectModel, orgUnitModel);
		window.add(form);
        window.show();
	}
	
	public void showNewFlexibleElementForm(final FlexibleElementDTO model,
			final boolean isUpdate) {
		int width = 700;
		int height = 350;
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
					if(projectModel != null){
						ProjectModelDTO modelUpdated = (ProjectModelDTO)result.getEntity();
						AdminFlexibleElementsView.this.refreshProjectModel(modelUpdated);
					}else{
						OrgUnitModelDTO modelUpdated = (OrgUnitModelDTO)result.getEntity();
						AdminFlexibleElementsView.this.refreshOrgUnitModel(modelUpdated);
					}					
					AdminFlexibleElementsView.this.getFieldsStore().remove(model);
					AdminFlexibleElementsView.this.getFieldsStore().add((FlexibleElementDTO)result.getAnnexEntity());
					AdminFlexibleElementsView.this.getFieldsStore().commitChanges();
				}				
			}


			
		}, model, projectModel, orgUnitModel, addedGroups);
		window.add(form);
        window.show();
	}
	
	private Grid<FlexibleElementDTO> buildFieldsListGrid(){
		
		
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		  
        ColumnConfig column = new ColumnConfig("label",I18N.CONSTANTS.adminFlexibleName(), 300);   
        column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(final FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {
				
				if((projectModel != null && ProjectModelStatus.DRAFT.equals(projectModel.getStatus()))
						|| (orgUnitModel != null && ProjectModelStatus.DRAFT.equals(orgUnitModel.getStatus()))){
					final Anchor nameHyperlink ;
					if(ElementTypeEnum.DEFAULT.equals(model.getElementType())){
						nameHyperlink = new Anchor(DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO)model).getType()), true);	
					}			
					else
						nameHyperlink = new Anchor(model.getLabel().replace("?", ""), true);
	                nameHyperlink.addStyleName("credits-partner-url");
	                nameHyperlink.addClickHandler(new ClickHandler(){

						@Override
						public void onClick(ClickEvent event) {
							showNewFlexibleElementForm(model,true);
						}
	                	
	                });
	                return nameHyperlink;
				}else{				
					if(ElementTypeEnum.DEFAULT.equals(model.getElementType()))
						return AdminUtil.createGridText(DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO)model).getType()).replace("?", ""));
					else
						return AdminUtil.createGridText(model.getLabel().replace("?", ""));					
				}
			}
        });
		configs.add(column);
		
		column = new ColumnConfig("type",I18N.CONSTANTS.adminFlexibleType(), 125);   
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
				validates.disable();
				validates.setValue(model.getValidates());
				return validates;
			}	    	
	    }); 
		configs.add(column);
		
		column = new ColumnConfig("privacyGroup",I18N.CONSTANTS.adminProfilesPrivacyGroups(), 125);   
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
				amendable.disable();
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
				banner.disable();
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
					int order = model.getBannerConstraint().getSortOrder();
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
		
		column = new ColumnConfig("group",I18N.CONSTANTS.adminFlexibleGroup(), 200);   
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

			@Override
			public Object render(final FlexibleElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {	
				LayoutGroupDTO group = model.getGroup();
				
				if((projectModel != null && ProjectModelStatus.DRAFT.equals(projectModel.getStatus()))
						|| (orgUnitModel != null && ProjectModelStatus.DRAFT.equals(orgUnitModel.getStatus()))){
					final Anchor nameHyperlink = new Anchor((String)group.get("title"), true);
	                nameHyperlink.addStyleName("credits-partner-url");
	                nameHyperlink.addClickHandler(new ClickHandler(){

						@Override
						public void onClick(ClickEvent event) {
							showNewGroupForm(model, true);
						}
	                	
	                });
	                return nameHyperlink;
				}else{
					return AdminUtil.createGridText((String)group.get("title"));
				}
				
				
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
	public void enableToolBar(){
		setTopComponent(initToolBar());
	}

	@Override
	public List<FlexibleElementDTO> getDeleteSelection() {
		GridSelectionModel<FlexibleElementDTO> sm = flexGrid.getSelectionModel();
        return sm.getSelectedItems();
	}
	
	@Override
	public void confirmDeleteSelected(final ConfirmCallback confirmCallback) {
		List<FlexibleElementDTO> deleteElements = getDeleteSelection();
		String names = "";
		for(FlexibleElementDTO s : deleteElements){			
			if(s instanceof DefaultFlexibleElementDTO){
				names += DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO)s).getType()) + ", ";
			}else{
				names += s.getLabel() + ", ";
			}
		}
		if(names.isEmpty()){
			MessageBox.alert(I18N.CONSTANTS.delete(), I18N.MESSAGES.adminFlexibleDeleteNone(), null);
		}else{
			names = names.substring(0, names.lastIndexOf(", "));
			MessageBox.confirm(I18N.CONSTANTS.delete(), I18N.MESSAGES.adminFlexibleConfirmDelete(names), new Listener<MessageBoxEvent>() {
				
				@Override
				public void handleEvent(MessageBoxEvent be) {
					if(be.getButtonClicked().getItemId().equals("yes")) {
						confirmCallback.confirmed();
					}
				}
			});
		}
		
				
	}
}
