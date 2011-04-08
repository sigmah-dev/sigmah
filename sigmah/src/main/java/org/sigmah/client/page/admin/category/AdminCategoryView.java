package org.sigmah.client.page.admin.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.category.AdminCategoryPresenter.View;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.common.widget.ColorField;
import org.sigmah.client.page.project.category.CategoryIconProvider;
import org.sigmah.client.ui.ToggleAnchor;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.DeleteCategories;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.category.CategoryIcon;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminCategoryView extends View {	
	
	private ListStore<CategoryTypeDTO> categoriesStore;
	private ListStore<CategoryElementDTO> categoryElementsStore;
	private final Grid<CategoryElementDTO> categoryElementsGrid;
	private final Grid<CategoryTypeDTO> categoriesGrid;
	private SimpleComboBox<String> categoryIcon;
	private TextField<String> categoryName;
	private final Dispatcher dispatcher;
	private CategoryTypeDTO currentCategoryType;
	private Button addCategoryElementButton;

	public AdminCategoryView(Dispatcher dispatcher){		
		
		this.dispatcher = dispatcher;
		
        setLayout(new BorderLayout());
        setHeaderVisible(false);
        setBorders(false);
        setBodyBorder(false);
        
        ContentPanel sidePanel = new ContentPanel(new VBoxLayout());
        sidePanel.setHeaderVisible(false);
        sidePanel.setWidth(375);
        sidePanel.setScrollMode(Scroll.NONE);
        categoriesGrid = buildCategoriesListGrid();
        sidePanel.add(categoriesGrid);
        sidePanel.setTopComponent(categoryTypeToolBar());
        
        ContentPanel categoryPanel = new ContentPanel(new FitLayout());
        categoryPanel.setHeaderVisible(false);
        categoryPanel.setBorders(true);
        categoryElementsGrid = buildCategoryElementsGrid();
        categoryPanel.add(categoryElementsGrid);
        categoryPanel.setTopComponent(categoryElementToolBar());
        
        final BorderLayoutData leftLayoutData = new BorderLayoutData(LayoutRegion.WEST, 400);
        leftLayoutData.setMargins(new Margins(0, 4, 0, 0));
		add(sidePanel, leftLayoutData);	
		 final BorderLayoutData mainLayoutData = new BorderLayoutData(LayoutRegion.CENTER);
	        mainLayoutData.setMargins(new Margins(0, 0, 0, 4));
		add(categoryPanel, mainLayoutData);		
	}

	private Grid<CategoryElementDTO> buildCategoryElementsGrid(){	
		
		categoryElementsStore = new ListStore<CategoryElementDTO>();
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		  
        ColumnConfig column = new ColumnConfig();
        column.setId("color");
        column.setWidth(75);
        column.setHeader(I18N.CONSTANTS.adminCategoryElementColor());  
        column.setRenderer(new GridCellRenderer<CategoryElementDTO>(){

			@Override
			public Object render(CategoryElementDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<CategoryElementDTO> store, Grid<CategoryElementDTO> grid) {
				return CategoryIconProvider.getIcon(model);
			}
        	
        });
        configs.add(column);
        
        column = new ColumnConfig();   
        column.setId("label");
        column.setWidth(400);
        column.setHeader(I18N.CONSTANTS.adminCategoryElementLabel()); 
        configs.add(column);
		
		ColumnModel cm = new ColumnModel(configs);		
		
		Grid<CategoryElementDTO> categoryElementsGrid = new Grid<CategoryElementDTO>(categoryElementsStore, cm); 
		categoryElementsGrid.setAutoHeight(true);
		categoryElementsGrid.setAutoWidth(false);
		categoryElementsGrid.getView().setForceFit(true);
		categoryElementsGrid.hide();
		return categoryElementsGrid;
	}
	
	private Grid<CategoryTypeDTO> buildCategoriesListGrid(){		
		
		categoriesStore = new ListStore<CategoryTypeDTO>();
		
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		  
        ColumnConfig column = new ColumnConfig("id",I18N.CONSTANTS.adminProfilesId(), 25);
        configs.add(column);
        
        column = new ColumnConfig();
        column.setId("icon_name");
        column.setWidth(75);
        column.setHeader(I18N.CONSTANTS.adminCategoryTypeIcon());  
        column.setRenderer(new GridCellRenderer<CategoryTypeDTO>(){

			@Override
			public Object render(CategoryTypeDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<CategoryTypeDTO> store, Grid<CategoryTypeDTO> grid) {
				
				CategoryElementDTO element = new CategoryElementDTO();
				element.setColor("b7a076");
				element.setLabel("");
				element.setParentCategoryDTO(model);
				return CategoryIconProvider.getIcon(element);
			}
        	
        });
        
        configs.add(column);
        
        column = new ColumnConfig("label",I18N.CONSTANTS.adminCategoryTypeName(), 300);  
		column.setRenderer(new GridCellRenderer<CategoryTypeDTO>(){

			@Override
			public Object render(final CategoryTypeDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<CategoryTypeDTO> store, Grid<CategoryTypeDTO> grid) {
				final ToggleAnchor anchor = new ToggleAnchor(model.getLabel());
	            anchor.setAnchorMode(true);

	            anchor.addClickHandler(new ClickHandler() {

	                @Override
	                public void onClick(ClickEvent event) {
	                	currentCategoryType = model;
						categoryElementsGrid.show();
						categoryElementsStore.removeAll();
						for(CategoryElementDTO categoryElementDTO : model.getCategoryElementsDTO()){
							categoryElementsStore.add(categoryElementDTO);
						}
						categoryElementsStore.commitChanges();
						addCategoryElementButton.enable();
					}
					
				});
				return anchor;
			}
	    	
	    });
		configs.add(column);
		
		ColumnModel cm = new ColumnModel(configs);		
		
		Grid<CategoryTypeDTO> grid = new Grid<CategoryTypeDTO>(categoriesStore, cm); 
		grid.setAutoHeight(true);
		grid.getView().setForceFit(true);
		grid.setAutoWidth(false);
		grid.setWidth(375);
		return grid;
	}
	
	private ToolBar categoryTypeToolBar() {		
		ToolBar toolbar = new ToolBar();
		
		categoryIcon = new SimpleComboBox<String>();
		categoryIcon.setFieldLabel(I18N.CONSTANTS.adminCategoryTypeIcon());
		categoryIcon.setWidth(75);
		categoryIcon.setEditable(false);
		categoryIcon.setAllowBlank(false);
		categoryIcon.setTriggerAction(TriggerAction.ALL);	
		List<String> values = new ArrayList<String>();  
		for(CategoryIcon e : CategoryIcon.values()){
			values.add(CategoryIcon.getName(e));
		}
		categoryIcon.add(values);
		
		toolbar.add(categoryIcon);
		
		categoryName = new TextField<String>();
		categoryName.setFieldLabel(I18N.CONSTANTS.adminCategoryTypeName());
		toolbar.add(categoryName);
    	
		Button addCategoryTypeButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
        addCategoryTypeButton.setItemId(UIActions.add);
		addCategoryTypeButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {				
				if(categoryName.getValue() != null && categoryIcon.getValue() != null
						&& categoriesStore.findModel("label", categoryName.getValue()) == null){
					Map<String, Object> newCategoryTypeProperties = new HashMap<String, Object>();
					newCategoryTypeProperties.put(AdminUtil.PROP_CATEGORY_TYPE_ICON, CategoryIcon.getIcon(categoryIcon.getValue().getValue()));
					newCategoryTypeProperties.put(AdminUtil.PROP_CATEGORY_TYPE_NAME, categoryName.getValue());
					dispatcher.execute(new CreateEntity("CategoryType", newCategoryTypeProperties), null, new AsyncCallback<CreateResult>(){

				       	 public void onFailure(Throwable caught) {
				       		 	MessageBox.alert(I18N.CONSTANTS.adminCategoryTypeCreationBox(), 
				          			I18N.MESSAGES.adminStandardCreationFailureF(I18N.CONSTANTS.adminCategoryTypeStandard()
												+ " '" + categoryName.getValue() + "'"), null);
				             }

							@Override
							public void onSuccess(CreateResult result) {
								if(result != null && result.getEntity() != null){	
									categoriesStore.add((CategoryTypeDTO) result.getEntity());
									categoriesStore.commitChanges();
									Notification.show(I18N.CONSTANTS.adminCategoryTypeCreationBox(), I18N.MESSAGES.adminStandardUpdateSuccessF(I18N.CONSTANTS.adminCategoryTypeStandard()
											+ " '" + categoryName.getValue() +"'"));					
								}					
								else{
									MessageBox.alert(I18N.CONSTANTS.adminCategoryTypeCreationBox(), 
						          			I18N.MESSAGES.adminStandardCreationNullF(I18N.CONSTANTS.adminCategoryTypeStandard()
														+ " '" + categoryName.getValue() + "'"), null);
								}
							}
				        });
				}else{
					MessageBox.alert("",I18N.CONSTANTS.adminStandardInvalidValues(), null);
				}
								
			}
			
		});
		toolbar.add(addCategoryTypeButton);
		
		Button deleteCategoryTypeButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
		deleteCategoryTypeButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				onDeleteCategoryConfirmed(categoriesGrid.getSelectionModel().getSelectedItems());                
			}
			
		});
		toolbar.add(deleteCategoryTypeButton);
		return toolbar;
	}
	
	private ToolBar categoryElementToolBar() {		
		ToolBar toolbar = new ToolBar();
		
		final TextField<String> name = new TextField<String>();
        name.setAllowBlank(false);
        toolbar.add(name);
        
        final ColorField colorField = new ColorField();
		colorField.setAllowBlank(false);
		colorField.setValue("FAAD63");
		colorField.setEditable(true);
		toolbar.add(colorField);
		
	    addCategoryElementButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
	    addCategoryElementButton.disable();
		addCategoryElementButton.setItemId(UIActions.add);
		addCategoryElementButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				if(name.getValue() != null && !name.getValue().isEmpty()
						&& categoryElementsStore.findModel("label", name.getValue()) == null){
					Map<String, Object> newCategoryElementProperties = new HashMap<String, Object>();
					newCategoryElementProperties.put(AdminUtil.PROP_CATEGORY_ELEMENT_NAME, name.getValue());
					newCategoryElementProperties.put(AdminUtil.PROP_CATEGORY_ELEMENT_COLOR, colorField.getValue());
					newCategoryElementProperties.put(AdminUtil.PROP_CATEGORY_TYPE, currentCategoryType);
					
					dispatcher.execute(new CreateEntity("CategoryType", newCategoryElementProperties), null, new AsyncCallback<CreateResult>(){

				       	 public void onFailure(Throwable caught) {
				       		 	MessageBox.alert(I18N.CONSTANTS.adminCategoryTypeCreationBox(), 
				          			I18N.MESSAGES.adminStandardCreationFailureF(I18N.CONSTANTS.adminCategoryTypeStandard()
												+ " '" + currentCategoryType.getLabel() + "'"), null);
				             }

							@Override
							public void onSuccess(CreateResult result) {
								if(result != null && result.getEntity() != null){	
									categoriesStore.remove(currentCategoryType);
									categoryElementsStore.add((CategoryElementDTO) result.getEntity());
									categoryElementsStore.commitChanges();
									List<CategoryElementDTO> elements = null;
									if(currentCategoryType.getCategoryElementsDTO()== null){
										elements = new ArrayList<CategoryElementDTO>();										
									}else{
										elements = currentCategoryType.getCategoryElementsDTO();
									}
									elements.add((CategoryElementDTO) result.getEntity());
									currentCategoryType.setCategoryElementsDTO(elements);
									categoriesStore.update(currentCategoryType);
									categoriesStore.commitChanges();
									Notification.show(I18N.CONSTANTS.adminCategoryTypeCreationBox(), I18N.MESSAGES.adminStandardUpdateSuccessF(I18N.CONSTANTS.adminCategoryTypeStandard()
											+ " '" + currentCategoryType.getLabel() +"'"));					
								}					
								else{
									MessageBox.alert(I18N.CONSTANTS.adminCategoryTypeCreationBox(), 
						          			I18N.MESSAGES.adminStandardCreationNullF(I18N.CONSTANTS.adminCategoryTypeStandard()
														+ " '" + currentCategoryType.getLabel() + "'"), null);
								}
							}
				        });
				}else{
					MessageBox.alert("",I18N.CONSTANTS.adminStandardInvalidValues(), null);
				}
			}
			
		});
		toolbar.add(addCategoryElementButton);
		
		Button deleteCategoryElementButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
		deleteCategoryElementButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				onDeleteCategoryElementConfirmed(categoryElementsGrid.getSelectionModel().getSelectedItems());	            
			}
			
		});
		toolbar.add(deleteCategoryElementButton);
		return toolbar;
	}
	
	private void onDeleteCategoryConfirmed(final List<CategoryTypeDTO> selection) {
		
		List<Integer> ids = new ArrayList<Integer>();
		String names = "";
		for(CategoryTypeDTO s : selection){
			ids.add(s.getId());
			names = s.getLabel() + ", " + names;
		}
		
		final String toDelete = names;
		final DeleteCategories deactivate = new DeleteCategories(selection, null);
        dispatcher.execute(deactivate, null, new AsyncCallback<VoidResult>() {

            @Override
            public void onFailure(Throwable caught) {
                MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(toDelete), null);
            }

            @Override
            public void onSuccess(VoidResult result) {
            	for(CategoryTypeDTO model : selection){
            		categoriesStore.remove(model);
            	}   
            	categoriesStore.commitChanges();
            }
        });
	}
	
	private void onDeleteCategoryElementConfirmed(final List<CategoryElementDTO> selection) {
		
		List<Integer> ids = new ArrayList<Integer>();
		String names = "";
		for(CategoryElementDTO s : selection){
			ids.add(s.getId());
			names = s.getLabel() + ", " + names;
		}
		
		final String toDelete = names;
		final DeleteCategories deactivate = new DeleteCategories(null, selection);
        dispatcher.execute(deactivate, null, new AsyncCallback<VoidResult>() {

            @Override
            public void onFailure(Throwable caught) {
                MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(toDelete), null);
            }

            @Override
            public void onSuccess(VoidResult result) {
            	List<CategoryElementDTO> elements = currentCategoryType.getCategoryElementsDTO();
            	for(CategoryElementDTO model : selection){
            		categoryElementsStore.remove(model);
            		elements.remove((CategoryElementDTO) model);
            	}   
            	categoryElementsStore.commitChanges();
            	
				currentCategoryType.setCategoryElementsDTO(elements);
				categoriesStore.update(currentCategoryType);
				categoriesStore.commitChanges();
            }
        });
	}
	
	@Override
	public ListStore<CategoryTypeDTO> getCategoriesStore() {
		return categoriesStore;
	}

	@Override
	public Component getMainPanel() {
		return this;
	}
	
}
