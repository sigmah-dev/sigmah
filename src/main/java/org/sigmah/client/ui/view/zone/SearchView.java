package org.sigmah.client.ui.view.zone;

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

import com.google.gwt.user.client.ui.Button;
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
	private ListBox searchOptions;
	private HTML searchForLabel;
	private TextBox searchText;
	private Button searchButton;
	private Button indexButton;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {


		searchBarPanel = new HorizontalPanel();
		searchBarPanel.getElement().setId("search-bar");

		searchForLabel = new HTML("Search for");
		//searchForLabel.setStylePrimaryName("search-for");
		searchForLabel.getElement().setId("search-for");
		
		String[] optionStrings = { "All", "Projects", "OrgUnits", "Contacts", "Files" };
		searchOptions = new ListBox();
		for( int i = 0; i < 5; i++ ){
			searchOptions.addItem(optionStrings[i]);
		}
		
		
		searchText = new TextBox();
		//searchText.setText("Enter search text");
		searchText.getElement().setPropertyString("placeholder", "Search");
		
		searchButton = new Button("Go");
		indexButton = new Button("FIndex");
		
		//searchBarPanel.add(searchForLabel);
		searchBarPanel.add(searchText);
		searchBarPanel.add(searchOptions);
		searchBarPanel.add(searchButton);
		//searchBarPanel.add(indexButton);

		// initWidget(); Useless

	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel getSearchBarPanel() {
		return searchBarPanel;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onViewRevealed() {
		// Nothing to do here.
	}

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasHTML getNameLabel() {
		return searchForLabel;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListBox getSearchOptions() {
		return searchOptions;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TextBox getSearchText() {
		return searchText;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getSearchButton() {
		return searchButton;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getIndexButton() {
		return indexButton;
	}

}

