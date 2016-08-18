package org.sigmah.client.ui.view.admin;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.CategoriesAdminPresenter;
import org.sigmah.client.ui.presenter.admin.CategoriesAdminPresenter.CategoryPresenterHandler;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.res.icon.project.category.CategoryIconProvider;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.ColorField;
import org.sigmah.client.ui.widget.ToggleAnchor;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.dto.referential.CategoryIcon;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.inject.Singleton;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.util.ColumnProviders;

/**
 * {@link CategoriesAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class CategoriesAdminView extends AbstractView implements CategoriesAdminPresenter.View {

	private static final Float CATEGORIE_PANEL_WIDTH = 450f;

	/**
	 * Category Elements Panel
	 */
	private ListStore<CategoryElementDTO> categoryElementsStore;
	private Grid<CategoryElementDTO> categoryElementsGrid;
	private Button addCategoryElementButton;
	private Button deleteCategoryElementButton;
    private Button disableCategoryElementButton;
    private Button enableCategoryElementButton;
	private TextField<String> name;
	private ColorField colorField;
    private Boolean isdisable;
	private ContentPanel categoryElementsPanel;
	/**
	 * Category Panel
	 */
	private Button addCategoryTypeButton;
	private Button deleteCategoryTypeButton;
	private Button ImportCategoryTypeButton;
	private ListStore<CategoryTypeDTO> categoriesStore;
	private Grid<CategoryTypeDTO> categoriesGrid;
	private SimpleComboBox<String> categoryIcon;
	private TextField<String> categoryName;
	private ToggleAnchor anchor;
	private ContentPanel categoryTypePanel;

	private CategoryPresenterHandler categoryPresenterHandler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		categoryTypePanel = Panels.content("", Layouts.fitLayout());
		categoryTypePanel.setHeaderVisible(false);
		categoriesGrid = buildCategoriesListGrid();
		categoryTypePanel.add(categoriesGrid);
		categoryTypePanel.setTopComponent(categoryTypeToolBar());

		categoryElementsPanel = Panels.content("", Layouts.fitLayout());
		categoryElementsPanel.setHeaderVisible(false);
		categoryElementsGrid = buildCategoryElementsGrid();
		categoryElementsPanel.add(categoryElementsGrid);
		categoryElementsPanel.setTopComponent(categoryElementToolBar());

		add(categoryTypePanel, Layouts.borderLayoutData(LayoutRegion.WEST, CATEGORIE_PANEL_WIDTH, Margin.HALF_RIGHT));
		add(categoryElementsPanel, Layouts.borderLayoutData(LayoutRegion.CENTER, Margin.HALF_LEFT));

	}

	private Grid<CategoryElementDTO> buildCategoryElementsGrid() {

		categoryElementsStore = new ListStore<CategoryElementDTO>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId("color");
		column.setWidth(75);
		column.setHeaderHtml(I18N.CONSTANTS.adminCategoryElementColor());
		column.setRenderer(new GridCellRenderer<CategoryElementDTO>() {

			@Override
			public Object render(CategoryElementDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<CategoryElementDTO> store,
					Grid<CategoryElementDTO> grid) {
				return CategoryIconProvider.getIcon(model);
			}

		});
		configs.add(column);

		column = new ColumnConfig();
        //column = new ColumnConfig(CategoryElementDTO.LABEL, I18N.CONSTANTS.adminFlexibleName(), 250);
        
		column.setId("label");
		column.setWidth(400);
		column.setHeaderHtml(I18N.CONSTANTS.adminCategoryElementLabel());
        column.setRenderer(new GridCellRenderer<CategoryElementDTO>() {
        @Override
			public Object render(CategoryElementDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<CategoryElementDTO> store,
					Grid<CategoryElementDTO> grid) {
                final String label;
                label = model.getLabel();
                if(model.getisDisabled()) {
					return model.renderDisabled(label);
                    //return label+"(disabled)";
				}
                else {
                  return model.renderText(label);
                   // return ColumnProviders.renderDisabled(label);
                   // return label+"(enabled)";
                   //return renderText(label);
                }
                
				
            }
            
        });
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		Grid<CategoryElementDTO> categoryElementsGrid = new Grid<CategoryElementDTO>(categoryElementsStore, cm);
		categoryElementsGrid.getView().setForceFit(true);
		return categoryElementsGrid;
	}

	/**
	 * Build the Category Grid
	 * 
	 * @return Grid<CategoryTypeDTO>
	 */
	private Grid<CategoryTypeDTO> buildCategoriesListGrid() {

		categoriesStore = new ListStore<CategoryTypeDTO>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig("id", I18N.CONSTANTS.adminProfilesId(), 50);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("icon_name");
		column.setWidth(70);
		column.setHeaderHtml(I18N.CONSTANTS.adminCategoryTypeIcon());
		column.setRenderer(new GridCellRenderer<CategoryTypeDTO>() {

			@Override
			public Object render(CategoryTypeDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<CategoryTypeDTO> store,
					Grid<CategoryTypeDTO> grid) {

				CategoryElementDTO element = new CategoryElementDTO();
				element.setColor("b7a076");
				element.setLabel("");
				element.setParentCategoryDTO(model);
				return CategoryIconProvider.getIcon(element);
			}

		});

		configs.add(column);

		column = new ColumnConfig("label", I18N.CONSTANTS.adminCategoryTypeName(), 280);
		column.setRenderer(new GridCellRenderer<CategoryTypeDTO>() {

			@Override
			public Object render(final CategoryTypeDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<CategoryTypeDTO> store,
					Grid<CategoryTypeDTO> grid) {

				anchor = new ToggleAnchor(model.getLabel());
				anchor.setAnchorMode(true);

				anchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {

						categoryPresenterHandler.onSelectHandler(model);

					}

				});
				return anchor;
			}

		});

		configs.add(column);

		column = new ColumnConfig();
		column.setWidth(50);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setRenderer(new GridCellRenderer<CategoryTypeDTO>() {

			@Override
			public Object render(final CategoryTypeDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<CategoryTypeDTO> store,
					Grid<CategoryTypeDTO> grid) {

				Button buttonExport = new Button(I18N.CONSTANTS.export());

				buttonExport.addListener(Events.OnClick, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {

						categoryPresenterHandler.onClickHandler(model);

					};
				});

				return buttonExport;
			}
		});
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		Grid<CategoryTypeDTO> grid = new Grid<CategoryTypeDTO>(categoriesStore, cm);
		grid.setWidth(450);
		return grid;
	}

	/**
	 * Build the Category ToolBar
	 * 
	 * @return ToolBar
	 */
	private ToolBar categoryTypeToolBar() {
		ToolBar toolbar = new ToolBar();

		categoryIcon = new SimpleComboBox<String>();
		categoryIcon.setFieldLabel(I18N.CONSTANTS.adminCategoryTypeIcon());
		categoryIcon.setWidth(75);
		categoryIcon.setEditable(false);
		categoryIcon.setAllowBlank(false);
		categoryIcon.setTriggerAction(TriggerAction.ALL);
		List<String> values = new ArrayList<String>();
		for (CategoryIcon e : CategoryIcon.values()) {
			values.add(CategoryIcon.getName(e));
		}
		categoryIcon.add(values);

		toolbar.add(categoryIcon);

		categoryName = new TextField<String>();
		categoryName.setFieldLabel(I18N.CONSTANTS.adminCategoryTypeName());
		toolbar.add(categoryName);

		addCategoryTypeButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		toolbar.add(addCategoryTypeButton);

		deleteCategoryTypeButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
		toolbar.add(deleteCategoryTypeButton);

		ImportCategoryTypeButton = new Button(I18N.CONSTANTS.importItem(), IconImageBundle.ICONS.up());

		toolbar.add(ImportCategoryTypeButton);

		return toolbar;
	}

	private ToolBar categoryElementToolBar() {
		ToolBar toolbar = new ToolBar();

		name = new TextField<String>();
		name.setAllowBlank(false);
		toolbar.add(name);

		colorField = new ColorField();
		colorField.setAllowBlank(false);
		colorField.setValue("FAAD63");
		colorField.setEditable(true);
		toolbar.add(colorField);

		addCategoryElementButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		addCategoryElementButton.disable();

		toolbar.add(addCategoryElementButton);

		deleteCategoryElementButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());

		toolbar.add(deleteCategoryElementButton);
        
        disableCategoryElementButton = new Button(I18N.CONSTANTS.disable(), IconImageBundle.ICONS.disable());

		toolbar.add(disableCategoryElementButton);
        
        enableCategoryElementButton =new Button(I18N.CONSTANTS.enable(), IconImageBundle.ICONS.checked());

		toolbar.add(enableCategoryElementButton);
		return toolbar;
	}

	@Override
	public ListStore<CategoryTypeDTO> getCategoriesStore() {
		return categoriesStore;
	}

	@Override
	public ListStore<CategoryElementDTO> getCategoryElementsStore() {
		return categoryElementsStore;
	}

	@Override
	public Grid<CategoryElementDTO> getCategoryElementsGrid() {
		return categoryElementsGrid;
	}

	@Override
	public Grid<CategoryTypeDTO> getCategoriesGrid() {
		return categoriesGrid;
	}

	@Override
	public SimpleComboBox<String> getCategoryIcon() {
		return categoryIcon;
	}

	@Override
	public TextField<String> getCategoryName() {
		return categoryName;
	}

	@Override
	public Button getAddCategoryElementButton() {
		return addCategoryElementButton;
	}

	@Override
	public Button getDeleteCategoryElementButton() {
		return deleteCategoryElementButton;
	}
    
    @Override
	public Button getDisableCategoryElementButton() {
		return disableCategoryElementButton;
	}
    
    @Override
	public Button getEnableCategoryElementButton() {
		return enableCategoryElementButton;
	}

	@Override
	public Button getDeleteCategoryTypeButton() {
		return deleteCategoryTypeButton;
	}

	@Override
	public LoadingMask getGategoriesTypeLoadingMonitor() {
		return new LoadingMask(categoryTypePanel, I18N.CONSTANTS.loading());
	}

	@Override
	public LoadingMask getGategoriesElementsLoadingMonitor() {
		return new LoadingMask(categoryElementsPanel, I18N.CONSTANTS.loading());
	}

	@Override
	public Button getAddCategoryTypeButton() {
		return addCategoryTypeButton;
	}

	@Override
	public void setCategoryPresenterHandler(CategoryPresenterHandler handler) {
		this.categoryPresenterHandler = handler;
	}

	@Override
	public TextField<String> getName() {
		return name;
	}

	@Override
	public ColorField getColorField() {
		return colorField;
	}
    
    @Override
	public Boolean getisdisable() {
		return false;
	}

	@Override
	public Button getImportCategoryTypeButton() {
		return ImportCategoryTypeButton;
	}
}
