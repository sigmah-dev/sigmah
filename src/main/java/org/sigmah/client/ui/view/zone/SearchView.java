package org.sigmah.client.ui.view.zone;

import java.util.Arrays;
import java.util.List;

import org.sigmah.client.i18n.I18N;

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

import org.sigmah.client.ui.presenter.zone.SearchPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.password.ExpirationPolicy;

import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
//import com.google.gwt.user.client.ui.Button;
import org.sigmah.client.ui.widget.button.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


/**
 * Search view (just a widgets set).
 * 
 * @author 
 */
public class SearchView extends AbstractView implements SearchPresenter.View {

	private Panel searchBarPanel;
	private TextField<String> searchTextField;
	private SimpleComboBox<String> searchOptionsComboBox;
	private Button newSearchButton;
	List<String> newSearchOptions;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		
		searchTextField = Forms.text("Search", false);
		searchTextField.setEmptyText("Search");
		searchTextField.setWidth(190);
		
		searchOptionsComboBox = new SimpleComboBox<String>();
		newSearchOptions = Arrays.asList("All", "Projects", "OrgUnits", "Contacts", "Your Files");
		searchOptionsComboBox.add(newSearchOptions);
		searchOptionsComboBox.setTriggerAction(ComboBox.TriggerAction.ALL);
		searchOptionsComboBox.setEditable(false);
		searchOptionsComboBox.setWidth(75);
		searchOptionsComboBox.setSimpleValue("All");
		
		
		newSearchButton = Forms.button("Go");
		
		searchBarPanel = new HorizontalPanel();
		searchBarPanel.getElement().setId("search-bar");
		searchBarPanel.add(searchTextField);
		searchBarPanel.add(searchOptionsComboBox);
		searchBarPanel.add(newSearchButton);

	}
	
	public TextField<String> getSearchTextField() {
		return searchTextField;
	}

	public void setSearchTextField(TextField<String> searchTextField) {
		this.searchTextField = searchTextField;
	}

	public SimpleComboBox<String> getSearchOptionsComboBox() {
		return searchOptionsComboBox;
	}

	public void setSearchOptionsComboBox(SimpleComboBox<String> searchOptionsComboBox) {
		this.searchOptionsComboBox = searchOptionsComboBox;
	}

	public Button getNewSearchButton() {
		return newSearchButton;
	}

	public void setNewSearchButton(Button newSearchButton) {
		this.newSearchButton = newSearchButton;
	}

	public List<String> getNewSearchOptions() {
		return newSearchOptions;
	}

	public void setNewSearchOptions(List<String> newSearchOptions) {
		this.newSearchOptions = newSearchOptions;
	}
	
	@Override
	public Panel getSearchBarPanel() {
		return searchBarPanel;
	}

	@Override
	public void onViewRevealed() {
		// Nothing to do here.
	}


}


