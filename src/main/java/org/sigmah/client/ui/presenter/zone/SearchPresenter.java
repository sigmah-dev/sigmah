package org.sigmah.client.ui.presenter.zone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.search.SearchService;
import org.sigmah.client.search.SearchServiceAsync;
import org.sigmah.client.ui.notif.N10N;
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
import org.sigmah.shared.command.BackupArchiveManagementCommand;
import org.sigmah.shared.command.FilesSolrIndexCommand;
import org.sigmah.shared.command.GetContacts;
import org.sigmah.shared.command.GetOrgUnits;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.conf.PropertyName;
import org.sigmah.shared.dto.BackupDTO;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.search.FilesSolrIndexDTO;
import org.sigmah.shared.dto.search.SearchResultsDTO;
import org.sigmah.shared.util.ProfileUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.Button;
import org.sigmah.client.ui.widget.button.Button;

import com.google.gwt.user.client.ui.FlexTable;
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
	Set<Integer> projectIdsForFiltering;
	Set<Integer> orgUnitIdsForFiltering;
	Set<Integer> contactIdsForFiltering;
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

		Button getNewSearchButton();

		SimpleComboBox<String> getSearchOptionsComboBox();

		TextField<String> getSearchTextField();

		Panel getSearchBarPanel();

		List<String> getNewSearchOptions();

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

		view.getNewSearchButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				// TODO Auto-generated method stub
				textToServer = view.getSearchTextField().getValue();
				view.getSearchTextField().clear();
				int sel_ind = view.getSearchOptionsComboBox().getSelectedIndex();
				filter = view.getNewSearchOptions().get(sel_ind);
				if (textToServer != null) {
					if (textToServer.length() > 0) {
						search();
					}
				}
			}

		});

		view.getSearchTextField().addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(ComponentEvent event) {
				if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
					if (view.getSearchTextField().getValue() != null) {
						if (view.getSearchTextField().getValue().length() > 0) {
							textToServer = view.getSearchTextField().getValue();
							view.getSearchTextField().clear();
							int sel_ind = view.getSearchOptionsComboBox().getSelectedIndex();
							filter = view.getNewSearchOptions().get(sel_ind);
							search();
						}
					}
				}
			}
		});

	}

	private void search() {

		if (firstsearch) {
			// dummy call just to make connection
			searchService.search(textToServer, filter, new AsyncCallback<ArrayList<SearchResultsDTO>>() {
				public void onFailure(Throwable caught) {
					// Window.alert("Could not make connection!");
					firstsearch = false;
					caught.printStackTrace();
				}

				public void onSuccess(ArrayList<SearchResultsDTO> result) {
					// Window.alert("Excellent, solr connection up!");
					firstsearch = false;
				}
			});
		}
		// Send the input to the server.
		// Window.alert("Filter is: " + filter);
		searchService.search(textToServer, filter, new AsyncCallback<ArrayList<SearchResultsDTO>>() {

			public void onFailure(Throwable caught) {
				// Window.alert("Failure on the server side!");
				N10N.error("Error connecting to Solr", "Solr Server connection is not available. Try later!");
				// firstsearch = true; //will try to set up a connnection again
				caught.printStackTrace();
			}

			public void onSuccess(ArrayList<SearchResultsDTO> result) {
				if (result == null) {
					N10N.error("Error connecting to Solr", "Solr Server connection is not available. Try later!");
				} else {
					searchResults = result;
					if (result.size() == 0) {
						N10N.info("No search results found", "There are no search results or "
								+ "they may not be accessible!");
					}
					else {
						loadProjectIdsForFiltering();
					}
				}

			}

		});
	}

	private void loadProjectIdsForFiltering() {
		Integer[] orgUnitsIds = auth().getOrgUnitIds().toArray(new Integer[auth().getOrgUnitIds().size()]);
		List<Integer> orgUnitsIdsAsList = orgUnitsIds != null ? Arrays.asList(orgUnitsIds) : null;
		// Window.alert("OrgUnitIds: " +orgUnitsIdsAsList.toString());
		GetProjects cmd = new GetProjects(orgUnitsIdsAsList, null);
		cmd.setMappingMode(ProjectDTO.Mode._USE_PROJECT_MAPPER);

		dispatch.execute(cmd, new CommandResultHandler<ListResult<ProjectDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				// Window.alert("Error while getting contacts.");
			}

			@Override
			public void onCommandSuccess(final ListResult<ProjectDTO> result) {
				projectIdsForFiltering = new HashSet<Integer>();

				if (ProfileUtils.isGranted(auth(), GlobalPermissionEnum.VIEW_ALL_PROJECTS)) {
					List<ProjectDTO> projectsForFiltering = result.getList();
					// applyProjectFilters();
					for (ProjectDTO projDTO : projectsForFiltering) {
						// Window.alert("Project ID?: " + projDTO.getId() + "
						// Proj
						// Name: " + projDTO.getName());
						projectIdsForFiltering.add(projDTO.getId());
					}
				}

				else if (ProfileUtils.isGranted(auth(), GlobalPermissionEnum.VIEW_MY_PROJECTS)) {
					projectIdsForFiltering.addAll(auth().getMemberOfProjectIds());
				}

				loadOrgUnitIdsForFiltering();
				// Window.alert("Completed getting the project for filtering: "
				// + projectIdsForFiltering.toString());
			}

		});
	}

	private void loadOrgUnitIdsForFiltering() {

		Set<Integer> orgUnitIds = new HashSet<Integer>();
		orgUnitIds.addAll(auth().getOrgUnitIds());
		dispatch.execute(new GetOrgUnits(orgUnitIds, OrgUnitDTO.Mode.WITH_TREE),
				new CommandResultHandler<ListResult<OrgUnitDTO>>() {

					@Override
					public void onCommandFailure(final Throwable e) {
						// Window.alert("Error while getting contacts.");
					}

					@Override
					public void onCommandSuccess(final ListResult<OrgUnitDTO> result) {
						orgUnitIdsForFiltering = new HashSet<Integer>();
						TreeStore<OrgUnitDTO> orgUnitsForFilteringTree = new TreeStore<OrgUnitDTO>();
						orgUnitsForFilteringTree.add(result.getData(), true);
						List<OrgUnitDTO> orgUnitsForFiltering = orgUnitsForFilteringTree.getAllItems();
						for (OrgUnitDTO dto : orgUnitsForFiltering) {
							orgUnitIdsForFiltering.add(dto.getId());
						}
						loadContactIdsForFiltering();
					}
				});

	}

	private void loadContactIdsForFiltering() {
		GetContacts cmd = new GetContacts();
		dispatch.execute(cmd, new CommandResultHandler<ListResult<ContactDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				// Window.alert("Error while getting contacts.");
			}

			@Override
			public void onCommandSuccess(final ListResult<ContactDTO> result) {

				contactIdsForFiltering = new HashSet<Integer>();

				if (ProfileUtils.isGranted(auth(), GlobalPermissionEnum.VIEW_VISIBLE_CONTACTS)) {
					List<ContactDTO> contactsForFiltering = result.getData();
					for (ContactDTO dto : contactsForFiltering) {
						contactIdsForFiltering.add(dto.getId());
					}
					// Window.alert("Contacts for filtering: " +
					// contactIdsForFiltering.toString());
				}
				doPageRequest();
			}
		});
	}

	public void doPageRequest() {
		if (searchResults != null && searchResults.size() > 0) {
			PageRequest request = new PageRequest(Page.SEARCH_RESULTS);
			request.addData(RequestParameter.TITLE, textToServer);
			
			ArrayList<SearchResultsDTO> filteredSearchResults = new ArrayList<SearchResultsDTO>();
			for( SearchResultsDTO dto : searchResults){
				if (filter(dto)){
					filteredSearchResults.add(dto);
				}
			}
			if (filteredSearchResults.size() == 0) {
				N10N.info("No search results found", "There are no search results or" 
						 + " they may not be accessible!");
			}else{
				request.addData(RequestParameter.CONTENT, filteredSearchResults);
				request.addParameter(RequestParameter.ID,
						textToServer.replaceAll("[^a-zA-Z0-9\\s]", "").replaceAll(" ", "-"));
				eventBus.navigateRequest(request);
			}
			
		}
	}
	
	public boolean filter(SearchResultsDTO dto) {

		if (dto == null)
			return false;

		Map<String, String> retMap = toMap(dto.getResult());
		dto.setDTOtype(retMap.get("doc_type").toString());

		if (retMap.get("doc_type").toString().equals("PROJECT")) {
			dto.setDTOid(retMap.get("databaseid").toString());
			if (!projectIdsForFiltering.contains(Integer.parseInt(dto.getDTOid()))) {
				// Window.alert("Found project not to be included!");
				return false;
			}

		} else if (retMap.get("doc_type").toString().equals("CONTACT")) {
			dto.setDTOid(retMap.get("id_contact").toString());
			if (!contactIdsForFiltering.contains(Integer.parseInt(dto.getDTOid()))) {
				// Window.alert("Found Contact not to be included: " +
				// dto.getDTOid());
				return false;
			}
		} else if (retMap.get("doc_type").toString().equals("ORG_UNIT")) {
			dto.setDTOid(retMap.get("org_unit_id").toString());
			if (!orgUnitIdsForFiltering.contains(Integer.parseInt(dto.getDTOid()))) {
				// Window.alert("Found OrgUnit not to be included!");
				return false;
			}
		} else if (retMap.get("doc_type").toString().equals("FILE")) {
			// if(projectIdsForFiltering.size() == 0 ||
			// orgUnitIdsForFiltering.size() == 0 ) //cannot view projects or
			// orgunits
			// return false;
			dto.setDTOid(retMap.get("file_version_id").toString());
			dto.setFile_name(retMap.get("file_name"));
			dto.setFile_ext(retMap.get("file_ext"));
			//Window.alert(retMap.toString() + "\n" + retMap.get("file_author_id") + " " + auth().getUserId().toString());
			if (retMap.get("file_author_id").equals(auth().getUserId().toString())) {
				// temporary filter, since I am unable to implement a better
				// filter, users can only view those files of which they are the authors..
				return true;
			}
			return false;
		}
		return true;
	}

	public static Map<String, String> toMap(String jsonStr) {
		Map<String, String> map = new HashMap<String, String>();

		JSONValue parsed = JSONParser.parseStrict(jsonStr);
		JSONObject jsonObj = parsed.isObject();
		if (jsonObj != null) {
			for (String key : jsonObj.keySet()) {
				map.put(key.replaceAll("^\"|\"$", ""), jsonObj.get(key).toString().replaceAll("^\"|\"$", ""));
			}
		}

		return map;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onZoneRequest(ZoneRequest zoneRequest) {
		// TODO Auto-generated method stub

	}

}
