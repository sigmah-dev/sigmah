package org.sigmah.client.ui.view.admin.models;

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


import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.FlexibleElementsAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * {@link FlexibleElementsAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class FlexibleElementsAdminView extends AbstractView implements FlexibleElementsAdminPresenter.View {

	private ContentPanel mainPanel;
	private Grid<FlexibleElementDTO> grid;
	private ToolBar toolbar;
	private Button addButton;
	private Button addGroupButton;
	private Button deleteButton;
	private Button enableButton;
	private Button disableButton;

	private boolean editable;
	private GridEventHandler<FlexibleElementDTO> gridEventHandler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		mainPanel = Panels.content(null);

		mainPanel.setTopComponent(createToolBar());

		add(mainPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Grid<FlexibleElementDTO> getGrid() {
		return grid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListStore<FlexibleElementDTO> getStore() {
		return grid.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGridEventHandler(final GridEventHandler<FlexibleElementDTO> handler) {
		this.gridEventHandler = handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setModelEditable(final boolean editable) {
		this.editable = editable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getAddButton() {
		return addButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getAddGroupButton() {
		return addGroupButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getDeleteButton() {
		return deleteButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getEnableButton() {
		return enableButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getDisableButton() {
		return disableButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setToolbarEnabled(final boolean enabled) {
		if (enabled) {
			toolbar.show();
		} else {
			toolbar.hide();
		}
		toolbar.setEnabled(enabled);
		addButton.setEnabled(enabled);
		addGroupButton.setEnabled(enabled);
		// Only with selection.
		deleteButton.setEnabled(false); 
		enableButton.setEnabled(false);
		disableButton.setEnabled(false);
	}

	@Override
	public void resetGrid(boolean canHaveMandatoryFields, boolean hasBanner, boolean hasCard) {
		this.mainPanel.remove(grid);
		createGrid(canHaveMandatoryFields, hasBanner, hasCard);
		this.mainPanel.add(grid);
	}
// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Creates the grid component.
	 * 
	 * @return The grid component.
	 */
	private Component createGrid(boolean canHaveMandatoryFields, boolean hasBanner, boolean hasCard) {

		grid = new Grid<FlexibleElementDTO>(new ListStore<FlexibleElementDTO>(), new FlexibleElementsColumnsProvider() {

			@Override
			protected boolean isEditable() {
				return editable;
			}

			@Override
			protected GridEventHandler<FlexibleElementDTO> getGridEventHandler() {
				return gridEventHandler;
			}

		}.getColumnModel(canHaveMandatoryFields, hasBanner, hasCard));

		grid.setAutoHeight(true);
		grid.getView().setForceFit(true);
		grid.getStore().setSortField(FlexibleElementDTO.CONTAINER);

		final GridSelectionModel<FlexibleElementDTO> selectionModel = new GridSelectionModel<FlexibleElementDTO>();
		selectionModel.setSelectionMode(SelectionMode.MULTI);
		grid.setSelectionModel(selectionModel);

		return grid;
	}

	/**
	 * Creates the toolbar component and its buttons.
	 * 
	 * @return The toolbar component.
	 */
	private Component createToolBar() {

		toolbar = new ToolBar();

		addButton = Forms.button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		toolbar.add(addButton);

		addGroupButton = Forms.button(I18N.CONSTANTS.adminFlexibleAddGroup(), IconImageBundle.ICONS.add());
		toolbar.add(addGroupButton);

		deleteButton = Forms.button(I18N.CONSTANTS.adminFlexibleDeleteFlexibleElements(), IconImageBundle.ICONS.delete());
		deleteButton.disable();
		toolbar.add(deleteButton);
		
		enableButton = Forms.button(I18N.CONSTANTS.adminFlexibleEnableFlexibleElements(), IconImageBundle.ICONS.checked());
		enableButton.disable();
		toolbar.add(enableButton);
		
		disableButton = Forms.button(I18N.CONSTANTS.adminFlexibleDisableFlexibleElements(), IconImageBundle.ICONS.disable());
		disableButton.disable();
		toolbar.add(disableButton);

		return toolbar;
	}

}
