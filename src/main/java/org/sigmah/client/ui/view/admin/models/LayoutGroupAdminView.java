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
import org.sigmah.client.ui.presenter.admin.models.LayoutGroupAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * {@link LayoutGroupAdminPresenter}'s view implementation.
 * 
 * @author Nikita Jibhkate (niksj1996@gmail.com) 
 */

public class LayoutGroupAdminView extends AbstractView implements LayoutGroupAdminPresenter.View {

    private Grid<LayoutGroupDTO> grid;
    private ToolBar toolbar;
    private Button addButton;
    private Button deleteButton;
    private boolean editable;
    private GridEventHandler<LayoutGroupDTO> gridEventHandler;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {

        final ContentPanel mainPanel = Panels.content(null);

        mainPanel.add(createGrid());
        mainPanel.setTopComponent(createToolBar());

        add(mainPanel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Grid<LayoutGroupDTO> getGrid() {
        return grid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListStore<LayoutGroupDTO> getStore() {
        return grid.getStore();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setGridEventHandler(final GridEventHandler<LayoutGroupDTO> handler) {
        this.gridEventHandler = handler;
    }

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
    public Button getDeleteButton() {
        return deleteButton;
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
        deleteButton.setEnabled(false);
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
    private Component createGrid() {

        grid = new Grid<LayoutGroupDTO>(new ListStore<LayoutGroupDTO>(), new LayoutGroupColumnsProvider() {

            @Override
            protected boolean isEditable() {
                return editable;
            }

            @Override
            protected GridEventHandler<LayoutGroupDTO> getGridEventHandler() {
                return gridEventHandler;
            }

        }.getColumnModel());

        grid.setAutoHeight(true);
        grid.getView().setForceFit(true);
        grid.getStore().setSortField(LayoutGroupDTO.ROW);

        final GridSelectionModel<LayoutGroupDTO> selectionModel = new GridSelectionModel<LayoutGroupDTO>();
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

        deleteButton = Forms.button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
        deleteButton.disable();
        toolbar.add(deleteButton);

        return toolbar;
    }

}
