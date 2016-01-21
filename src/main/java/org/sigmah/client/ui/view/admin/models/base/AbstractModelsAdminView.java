package org.sigmah.client.ui.view.admin.models.base;

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


import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.base.AbstractModelsAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.IsModel;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Widget;
import java.util.Date;

/**
 * Models administration presenters abstract view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public abstract class AbstractModelsAdminView<E extends IsModel> extends AbstractView implements AbstractModelsAdminPresenter.View<E> {

	// CSS style names.
	private static final String STYLE_TOOL_CLOSE_ICON = "x-tool-close";

	// --
	// GRID PANEL.
	// --

	private ContentPanel gridPanel;
	private Grid<E> grid;

	private Button addButton;
	private Button importButton;
	private Button deleteButton;
	private Button exportButton;
	private Button duplicateButton;

	private GridEventHandler<E> gridEventHandler;

	// --
	// DETAILS PANEL.
	// --

	private LayoutContainer detailsContainer;
	private FormPanel detailsHeaderForm;
	private ToolButton closeButton;
	private Button saveDetailsHeaderButton;
	
	private AdapterField maintenanceGroupField;
	private CheckBox underMaintenanceField;
	private DateField maintenanceDateField;
	private TimeField maintenanceTimeField;

	private TabPanel tabPanel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void initialize() {

		// --
		// GRID PANEL.
		// --

		gridPanel = Panels.content("Models"); // TODO i18n

		grid = new Grid<E>(new ListStore<E>(), getColumnModel());
		grid.setAutoHeight(true);
		grid.getView().setForceFit(true);

		final GridSelectionModel<E> selectionModel = new GridSelectionModel<E>();
		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		grid.setSelectionModel(selectionModel);

		gridPanel.setTopComponent(createGridToolBar());
		gridPanel.setScrollMode(Style.Scroll.AUTO);
		gridPanel.add(grid);
		
		// --
		// MAINTENANCE FIELDS.
		// --
		
		underMaintenanceField = Forms.checkbox(I18N.CONSTANTS.UNDER_MAINTENANCE());
		maintenanceDateField = Forms.date(null, true);
		maintenanceTimeField = Forms.time(null, true);
		
		maintenanceDateField.setVisible(false);
		maintenanceTimeField.setVisible(false);
		
		final com.google.gwt.user.client.ui.Grid maintenanceGrid = new com.google.gwt.user.client.ui.Grid(1, 5);
		maintenanceGrid.setWidget(0, 0, underMaintenanceField);
		maintenanceGrid.setWidget(0, 2, maintenanceDateField);
		maintenanceGrid.setWidget(0, 4, maintenanceTimeField);
		
		maintenanceGroupField = new AdapterField(maintenanceGrid);
		maintenanceGroupField.setFieldLabel(I18N.CONSTANTS.UNDER_MAINTENANCE());

		// --
		// DETAILS PANEL.
		// --

		detailsContainer = Layouts.fit(false, "x-border-layout-ct");

		detailsHeaderForm = buildHeaderForm();
		closeButton = new ToolButton(STYLE_TOOL_CLOSE_ICON);
		detailsHeaderForm.getHeader().addTool(closeButton);
		detailsHeaderForm.setHeaderVisible(true);

		saveDetailsHeaderButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
		detailsHeaderForm.addButton(saveDetailsHeaderButton);

		tabPanel = Panels.tab(Layouts.STYLE_WHITE_BACKGROUND);
		tabPanel.setPlain(true);
		
		// --
		// GENERAL INITIALIZATION.
		// --

		add(gridPanel, Layouts.borderLayoutData(LayoutRegion.WEST, 500f, true, Margin.RIGHT));
		add(detailsContainer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setGridEventHandler(final GridEventHandler<E> gridEventHandler) {
		this.gridEventHandler = gridEventHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Button getGridAddButton() {
		return addButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Button getGridImportButton() {
		return importButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Button getGridDeleteButton() {
		return deleteButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Button getGridExportButton() {
		return exportButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Button getGridDuplicateButton() {
		return duplicateButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Grid<E> getGrid() {
		return grid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ListStore<E> getStore() {
		return grid.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Loadable getGridMask() {
		return new LoadingMask(grid);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Loadable getDetailsPanelMask() {
		return new LoadingMask(detailsContainer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void loadModel(final E model) {

		detailsContainer.removeAll();

		if (model == null) {
			return;
		}

		final String modelHeader = loadModelHeader(model);
		detailsHeaderForm.setHeadingHtml(modelHeader);

		final LayoutContainer container = Layouts.border(false);
		container.add(detailsHeaderForm, Layouts.borderLayoutData(LayoutRegion.NORTH, getDetailsHeaderFormHeight(), Margin.BOTTOM));
		container.add(tabPanel, Layouts.borderLayoutData(LayoutRegion.CENTER));

		detailsContainer.add(container);
		detailsContainer.layout();
		selectFirstTab();
	}
	
	/**
	 * Height of the details header form (can be overwritten if necessary).
	 * 
	 * @return The height of the details header form.
	 */
	public float getDetailsHeaderFormHeight() {
		return 200.0f;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final IconButton getDetailsCloseButton() {
		return closeButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Button getHeaderSaveButton() {
		return saveDetailsHeaderButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addTab(final String tabTitle, final Widget tabView, final Listener<ComponentEvent> listener) {

		final TabItem tabItem = new TabItem(tabTitle);
		tabItem.addListener(Events.Select, listener);
		tabItem.add(tabView);
		tabItem.setScrollMode(Scroll.AUTO);

		tabPanel.add(tabItem);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void selectFirstTab() {
		tabPanel.setSelection(tabPanel.getItem(0));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel getHeaderForm() {
		return detailsHeaderForm;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setModelGridPanelExpanded(boolean expanded) {
		final BorderLayout layout = (BorderLayout) ((LayoutContainer)asWidget()).getLayout();
		
		if(expanded) {
			layout.expand(LayoutRegion.WEST);
		} else {
			layout.collapse(LayoutRegion.WEST);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public com.google.gwt.user.client.ui.Grid getMaintenanceGrid() {
		return (com.google.gwt.user.client.ui.Grid) maintenanceGroupField.getWidget();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getUnderMaintenanceField() {
		return underMaintenanceField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Date> getMaintenanceDateField() {
		return maintenanceDateField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TimeField getMaintenanceTimeField() {
		return maintenanceTimeField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<?> getMaintenanceGroupField() {
		return maintenanceGroupField;
	}
	
	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Returns the grid events handler.
	 * 
	 * @return The grid events handler.
	 */
	protected final GridEventHandler<E> getGridEventHandler() {
		return gridEventHandler;
	}

	/**
	 * Creates the project models grid toolbar and its buttons.
	 * 
	 * @return The toolbar component.
	 */
	private ToolBar createGridToolBar() {

		final ToolBar toolbar = new ToolBar();

		addButton = Forms.button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		toolbar.add(addButton);

		importButton = Forms.button(I18N.CONSTANTS.importItem(), IconImageBundle.ICONS.up());
		toolbar.add(importButton);

		// --
		// Following buttons should be enabled only if a single item is selected.
		// --

		deleteButton = Forms.button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
		deleteButton.disable();
		toolbar.add(deleteButton);

		exportButton = Forms.button(I18N.CONSTANTS.export(), IconImageBundle.ICONS.down());
		exportButton.disable();
		toolbar.add(exportButton);

		duplicateButton = Forms.button(I18N.CONSTANTS.adminModelCopy(), IconImageBundle.ICONS.add());
		duplicateButton.disable();
		toolbar.add(duplicateButton);

		return toolbar;
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// ABSTRACT METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Returns the main grid's column model.
	 * 
	 * @return The column model.
	 */
	protected abstract ColumnModel getColumnModel();

	/**
	 * Builds the header form panel and its fields.<br>
	 * Should not include the save button.
	 * 
	 * @return The header form panel.
	 */
	protected abstract FormPanel buildHeaderForm();

	/**
	 * Loads the given {@code model} header.
	 * 
	 * @param model
	 *          The loaded model.
	 * @return The model header name displayed into panel header.
	 */
	protected abstract String loadModelHeader(E model);

}
