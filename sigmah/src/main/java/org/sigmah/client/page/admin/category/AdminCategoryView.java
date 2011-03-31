package org.sigmah.client.page.admin.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.admin.category.AdminCategoryPresenter.View;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.common.widget.ColorField;
import org.sigmah.client.page.project.category.CategoryIconProvider;
import org.sigmah.client.ui.ToggleAnchor;
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
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public class AdminCategoryView extends View {	
	
	private ListStore<CategoryTypeDTO> categoriesStore;
	private ListStore<CategoryElementDTO> categoryElementsStore;
	private EditorGrid<CategoryElementDTO> categoryElementsGrid;
	private final HashMap<String, Object> newCategoryTypeProperties = new HashMap<String, Object>();
	private final Dispatcher dispatcher;
	private CategoryTypeDTO currentCategoryType;

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
        sidePanel.add(buildModelsListGrid());
        sidePanel.setTopComponent(categoryTypeToolBar());
        
        ContentPanel reportPanel = new ContentPanel(new FitLayout());
        reportPanel.setHeaderVisible(false);
        reportPanel.setBorders(true);
        reportPanel.add(buildCategoryElementsGrid());
        reportPanel.setTopComponent(categoryElementToolBar());
        
        final BorderLayoutData leftLayoutData = new BorderLayoutData(LayoutRegion.WEST, 375);
        leftLayoutData.setMargins(new Margins(0, 4, 0, 0));
		add(sidePanel, leftLayoutData);	
		 final BorderLayoutData mainLayoutData = new BorderLayoutData(LayoutRegion.CENTER);
	        mainLayoutData.setMargins(new Margins(0, 0, 0, 4));
		add(reportPanel, mainLayoutData);		
	}

	private EditorGrid<CategoryElementDTO> buildCategoryElementsGrid(){	
		
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
		
		categoryElementsGrid = new EditorGrid<CategoryElementDTO>(categoryElementsStore, cm); 
		categoryElementsGrid.setAutoHeight(true);
		categoryElementsGrid.setAutoWidth(false);
		categoryElementsGrid.getView().setForceFit(true);
		categoryElementsGrid.hide();
		return categoryElementsGrid;
	}
	
	private Grid<CategoryTypeDTO> buildModelsListGrid(){		
		
		categoriesStore = new ListStore<CategoryTypeDTO>();
		
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		  
        ColumnConfig column = new ColumnConfig();
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
        
        column = new ColumnConfig("name",I18N.CONSTANTS.adminCategoryTypeName(), 300);  
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
						categoryElementsGrid.show();
						categoryElementsStore.removeAll();
						for(CategoryElementDTO categoryElementDTO : model.getCategoryElementsDTO()){
							categoryElementsStore.add(categoryElementDTO);
						}
						categoryElementsStore.commitChanges();
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
		
		final SimpleComboBox<String> categoryIcon = new SimpleComboBox<String>();
		categoryIcon.setFieldLabel(I18N.CONSTANTS.adminCategoryTypeIcon());
		categoryIcon.setEditable(false);
		categoryIcon.setAllowBlank(false);
		categoryIcon.setTriggerAction(TriggerAction.ALL);	
		List<String> values = new ArrayList<String>();  
		for(CategoryIcon e : CategoryIcon.values()){
			values.add(CategoryIcon.getName(e));
		}
		categoryIcon.add(values);
		
		toolbar.add(categoryIcon);
		
		final TextField<String> categoryName = new TextField<String>();
		categoryName.setFieldLabel(I18N.CONSTANTS.adminCategoryTypeName());
		toolbar.add(categoryName);
    	
		Button addCategoryTypeButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
        addCategoryTypeButton.setItemId(UIActions.add);
		addCategoryTypeButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				if(categoryName.getValue() !=  null){				
					if(categoryName.getValue()!= null && categoryIcon.getValue() != null){
						
					}else{
						MessageBox.alert("",I18N.CONSTANTS.adminStandardInvalidValues(), null);
					}
				}				
			}
			
		});
		toolbar.add(addCategoryTypeButton);
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
		
		Button addCategoryElementButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		addCategoryElementButton.setItemId(UIActions.add);
		addCategoryElementButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				if(name.getValue() != null && !name.getValue().isEmpty()
						&& categoriesStore.findModel("label", name) == null){
					
				}else{
					MessageBox.alert("",I18N.CONSTANTS.adminStandardInvalidValues(), null);
				}
			}
			
		});
		toolbar.add(addCategoryElementButton);
		return toolbar;
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
