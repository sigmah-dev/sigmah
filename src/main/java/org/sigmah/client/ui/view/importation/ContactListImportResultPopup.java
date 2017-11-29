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

import java.util.Arrays;
import java.util.List;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.ContactDTO;

/**
 * Popup displaying a short report about an automated import.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ContactListImportResultPopup extends PopupWidget {

	private Grid<ContactDTO> grid;
	private Button closeButton;

	public ContactListImportResultPopup() {
		super(true, Layouts.fitLayout());
		setWidth("700px");
		setHeight("500px");
    initialize();
	}

	public ContactListImportResultPopup(List<ContactDTO> contacts) {
		this();
    grid.getStore().add(contacts);
	}
	
	/**
	 * Creates the inner components of the popup.
	 */
	public void initialize() {
		setTitle(I18N.CONSTANTS.ignoredImportedContacts());
		
		// Building the grid.
		grid = new Grid<ContactDTO>(new ListStore<ContactDTO>(), createColumnModel());
		
		// Creating the OK button.
		closeButton = Forms.button(I18N.CONSTANTS.ok(), new SelectionListener<ButtonEvent>() {
      @Override
      public void componentSelected(ButtonEvent buttonEvent) {
        hide();
      }
    });
		
		// Preparing the popup.
		setContent(grid);
		addButton(closeButton);
	}

	/**
	 * Creates the column model.
	 */
	private ColumnModel createColumnModel() {
		
		// Project code column.
		final ColumnConfig projectNameColumnConfig = new ColumnConfig(ContactDTO.NAME, I18N.CONSTANTS.contactName(), 325);
		
		// Project title column.
		final ColumnConfig projectFirstNameColumnConfig = new ColumnConfig(ContactDTO.FIRSTNAME, I18N.CONSTANTS.contactFirstName(), 325);

		// Creating the column model.
		return new ColumnModel(Arrays.asList(
				projectNameColumnConfig,
        projectFirstNameColumnConfig));
	}
}
