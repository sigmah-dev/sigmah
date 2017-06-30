package org.sigmah.client.ui.presenter.zone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.search.SearchService;
import org.sigmah.client.search.SearchServiceAsync;
import org.sigmah.client.ui.presenter.CreateProjectPresenter;
import org.sigmah.client.ui.presenter.base.AbstractZonePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.zone.SearchView;
import org.sigmah.client.ui.widget.tab.Tab;
import org.sigmah.client.ui.widget.tab.TabBar;
import org.sigmah.client.ui.widget.tab.TabId;
import org.sigmah.client.ui.widget.tab.TabBar.TabBarListener;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.conf.PropertyName;
import org.sigmah.shared.dto.search.SearchResultsDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.Events;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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

/**
 * Organization banner presenter displaying organization's name and logo.
 * 
 * @author
 */

@Singleton
public class SearchPresenter extends AbstractZonePresenter<SearchPresenter.View> {

	private final SearchServiceAsync searchService = GWT.create(SearchService.class);
	private ArrayList<SearchResultsDTO> searchResults = new ArrayList<SearchResultsDTO>();
	private Boolean dih_success;
	String textToServer = "default search text";
	String filter = "All";
	boolean firstsearch = true;

	@Inject
	public SearchPresenter(View view, Injector injector) {
		super(view, injector);
		// TODO Auto-generated constructor stub
	}

	/**
	 * View interface.
	 */
	@ImplementedBy(SearchView.class)
	public static interface View extends ViewInterface {

		HasHTML getNameLabel();

		Panel getSearchBarPanel();

		ListBox getSearchOptions();

		TextBox getSearchText();

		Button getSearchButton();

		Button getIndexButton();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Zone getZone() {
		// TODO Auto-generated method stub
		return Zone.SEARCH_BANNER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		view.getSearchText().addKeyUpHandler(new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {

					textToServer = view.getSearchText().getText();
					int sel_ind = view.getSearchOptions().getSelectedIndex();
					filter = view.getSearchOptions().getValue(sel_ind);
					if (textToServer.length() > 0) {
						search();
					}
				}
			}

		});

		view.getSearchButton().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				textToServer = view.getSearchText().getText();
				int sel_ind = view.getSearchOptions().getSelectedIndex();
				filter = view.getSearchOptions().getValue(sel_ind);
				if (textToServer.length() > 0) {
					search();
				}
			}

		});

		view.getIndexButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				index();
			}
		});

	}

	private void search() {

		if (firstsearch) {
			// dummy call just to make connection
			searchService.search(textToServer, filter, new AsyncCallback<ArrayList<SearchResultsDTO>>() {
				public void onFailure(Throwable caught) {
					//Window.alert("Could not make connection!");
					firstsearch = false;
					caught.printStackTrace();
				}

				public void onSuccess(ArrayList<SearchResultsDTO> result) {
					//Window.alert("Excellent, solr connection up!");
					firstsearch = false;
				}
			});
		}
		// Send the input to the server.
		//Window.alert("Filter is: " + filter);
		searchService.search(textToServer, filter, new AsyncCallback<ArrayList<SearchResultsDTO>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failure on the server side!");
				//firstsearch = true; //will try to set up a connnection again
				caught.printStackTrace();
			}

			public void onSuccess(ArrayList<SearchResultsDTO> result) {
				searchResults = result;
				// for (SearchResultsDTO doc : searchResults) {
				// Window.alert(doc.getResult().toString());
				// }

				if (searchResults != null) {
					PageRequest request = new PageRequest(Page.SEARCH_RESULTS);
					// request.addData(RequestParameter.HEADER, searchText);
					request.addData(RequestParameter.TITLE, textToServer.replaceAll("[^a-zA-Z0-9\\s]", ""));
					request.addData(RequestParameter.CONTENT, searchResults);
					request.addParameter(RequestParameter.ID, textToServer.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll(" ", "-"));
					// request.addParameter(RequestParameter.HEADER,searchText);
					//request.addParameter(RequestParameter.TITLE, textToServer);
					eventBus.navigateRequest(request);
				}

			}
		});
	}

	private void index() {
		searchService.index(new AsyncCallback<Boolean>() {
			public void onFailure(Throwable caught) {
				Window.alert("Failure on the server side!");
				caught.printStackTrace();
			}

			public void onSuccess(Boolean result) {
				dih_success = result;
				if (dih_success == true) {
					Window.alert("Successfully completed Full Import!");
				} else {
					Window.alert("Failed to complete Full Import!");
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onZoneRequest(ZoneRequest zoneRequest) {
		// TODO Auto-generated method stub

	}

}
