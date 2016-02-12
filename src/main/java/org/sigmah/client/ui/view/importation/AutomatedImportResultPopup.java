package org.sigmah.client.ui.view.importation;

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

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import java.util.Arrays;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * Popup displaying a short report about an automated import.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class AutomatedImportResultPopup extends PopupWidget {

	private Grid<BaseModelData> grid;
	private Button closeButton;
	
	public AutomatedImportResultPopup() {
		super(true, Layouts.fitLayout());
		setWidth("700px");
		setHeight("500px");
	}
	
	/**
	 * Creates the inner components of the popup.
	 */
	public void initialize() {
		setTitle(I18N.CONSTANTS.automatedImportResults());
		
		// Building the grid.
		grid = new Grid<BaseModelData>(new ListStore<BaseModelData>(), createColumnModel());
		
		// Creating the OK button.
		closeButton = Forms.button(I18N.CONSTANTS.ok());
		
		// Preparing the popup.
		setContent(grid);
		addButton(closeButton);
	}
	
	public ListStore<BaseModelData> getStore() {
		return grid.getStore();
	}

	public Button getCloseButton() {
		return closeButton;
	}
	
	/**
	 * Creates the column model.
	 * 
	 * @param selectionModel
	 * @return 
	 */
	private ColumnModel createColumnModel() {
		
		// Project code column.
		final ColumnConfig projectCodeColumnConfig = new ColumnConfig(ProjectDTO.NAME, I18N.CONSTANTS.code(), 75);
		
		// Project title column.
		final ColumnConfig projectTitleColumnConfig = new ColumnConfig(ProjectDTO.FULL_NAME, I18N.CONSTANTS.title(), 325);
		
		// New value column.
		final ColumnConfig statusColumnConfig = new ColumnConfig("status", I18N.CONSTANTS.importHeadingStatus(), 300);
		
		// Creating the column model.
		return new ColumnModel(Arrays.asList(
				projectCodeColumnConfig,
				projectTitleColumnConfig,
				statusColumnConfig));
	}
	
}
