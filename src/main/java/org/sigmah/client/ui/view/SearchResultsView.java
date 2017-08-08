
package org.sigmah.client.ui.view;

import java.util.ArrayList;

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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.search.SearchService;
import org.sigmah.client.search.SearchServiceAsync;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.SearchResultsPresenter;
import org.sigmah.client.ui.presenter.SearchResultsPresenter.ContactResultsClickHandler;
import org.sigmah.client.ui.presenter.SearchResultsPresenter.FilesResultsClickHandler;
import org.sigmah.client.ui.presenter.SearchResultsPresenter.OrgUnitResultsClickHandler;
import org.sigmah.client.ui.presenter.SearchResultsPresenter.ProjectResultsClickHandler;
import org.sigmah.client.ui.presenter.SearchResultsPresenter.SearchResultsClickHandler;
import org.sigmah.client.ui.presenter.contact.dashboardlist.ContactsListWidget;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget;
import org.sigmah.client.ui.presenter.zone.SearchPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.client.ui.res.icon.orgunit.OrgUnitImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.orgunit.OrgUnitTreeGrid;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.dto.search.SearchResultsDTO;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.util.ProfileUtils;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Dashboard view.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */

public class SearchResultsView extends AbstractView implements SearchResultsPresenter.View {

	private String searchText;

	private ContentPanel searchResultsPanel;

	ListStore<SearchResultsDTO> searchResultsStore;

	private LayoutContainer centerContainer;

	private static SearchResultsClickHandler handler;

	final SearchResultsClickHandler getSearchResultsClickHandler() {
		return handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProjectClickHandler(final ProjectResultsClickHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setContactClickHandler(final ContactResultsClickHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setOrgUnitClickHandler(final OrgUnitResultsClickHandler handler) {
		this.handler = handler;
	}

	@Override
	public void setFileClickHandler(final FilesResultsClickHandler handler) {
		// TODO Auto-generated method stub
		this.handler = handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		// default
		// Window.alert("Initializing Search View!");
	}

	public ContentPanel getSearchResultsPanel() {
		return searchResultsPanel;
	}

	public ListStore<SearchResultsDTO> getSearchResultsStore() {
		return searchResultsStore;
	}

	public LayoutContainer getCenterContainer() {
		return centerContainer;
	}

	public void setSearchString(String searchText) {
		this.searchText = searchText;
		// Window.alert("Searchtext set to " + searchText);
	}

	// -------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------------------

	public boolean addSearchData(Object searchData) {
		if (searchData != null) {
			// Window.alert("Received search results!: \n" +
			// searchData.toString());
			searchResultsStore = new ListStore<SearchResultsDTO>();
			for (Object object : (ArrayList) searchData) {

				SearchResultsDTO temp = object != null ? (SearchResultsDTO) object : null;
				searchResultsStore.add(temp);
			}
			return true;
		} else {
			Window.alert("Failed to receive search results!");
			return false;
		}
	}

	public void addResultsPanel() {

		// Window.alert("Started addResultsPanel!");
		if (centerContainer == null) {
			centerContainer = Layouts.vBox();
			centerContainer.layout();
		} else {
			centerContainer.removeAll();
			centerContainer.layout();
		}
		setContentPanel("Search results for \"" + searchText + "\"", false, null, null);
		searchResultsPanel.layout();
		ColumnModel columns = new ColumnModel(createSearchResultsGridColumns());

		Grid<SearchResultsDTO> searchResultsGrid = new Grid<SearchResultsDTO>(searchResultsStore, columns);
		searchResultsGrid.getView().setForceFit(false);
		searchResultsGrid.getView().setAutoFill(true);
		searchResultsGrid.setAutoExpandColumn("label");
		searchResultsPanel.add(searchResultsGrid);
		searchResultsPanel.layout();

		centerContainer.add(searchResultsPanel, Layouts.vBoxData(Margin.TOP));
		centerContainer.layout();
		add(centerContainer);

	}

	private void setContentPanel(String title, boolean collapsible, Layout layout, Scroll scroll,
			String... stylenames) {

		// Window.alert("Started setContentPanel!");
		searchResultsPanel = new ContentPanel(layout != null ? layout : new FitLayout());
		searchResultsPanel.layout();

		// Window.alert("Hello!");
		searchResultsPanel.setHeadingHtml(title);
		// Window.alert("Hello!");
		searchResultsPanel.layout();
		searchResultsPanel.setHeaderVisible(true);
		searchResultsPanel.setCollapsible(collapsible);

		if (ClientUtils.isNotEmpty(stylenames)) {
			for (String stylename : stylenames) {
				if (ClientUtils.isBlank(stylename)) {
					continue;
				}
				searchResultsPanel.addStyleName(stylename);
			}
		}

		if (scroll != null) {
			searchResultsPanel.setScrollMode(scroll);
		}

		// Window.alert("Ended setContentPanel!");

	}

	private List<ColumnConfig> createSearchResultsGridColumns() {

		// Window.alert("Started createSearchResultsGridColumns!");

		// Label column.
		ColumnConfig labelColumn = new ColumnConfig();
		labelColumn.setId("label");
		labelColumn.setHeaderHtml("Sort");
		labelColumn.setWidth(100);
		// labelColumn.setResizable(true);

		// Add link
		labelColumn.setRenderer(new GridCellRenderer<SearchResultsDTO>() {

			@Override
			public Object render(final SearchResultsDTO model, String property, ColumnData config, int rowIndex,
					int colIndex, ListStore<SearchResultsDTO> store, Grid<SearchResultsDTO> grid) {

				Map<String, String> retMap = SearchPresenter.toMap(model.getResult());
				HTML h = new HTML(getNiceText(retMap));

				if (retMap.get("doc_type").toString().equals("PROJECT")) {

					h.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							((ProjectResultsClickHandler) handler)
									.onLabelClickEvent(Integer.parseInt(model.getDTOid()));
						}
					});
				}

				if (retMap.get("doc_type").toString().equals("CONTACT")) {

					h.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							// handler.onLabelClickEvent(project_id);
							((ContactResultsClickHandler) handler)
									.onLabelClickEvent(Integer.parseInt(model.getDTOid()));
						}
					});
				}

				if (retMap.get("doc_type").toString().equals("ORG_UNIT")) {

					h.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {

							((OrgUnitResultsClickHandler) handler)
									.onLabelClickEvent(Integer.parseInt(model.getDTOid()));
						}
					});
				}

				if (retMap.get("doc_type").toString().equals("FILE")) {

					h.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {

							FileVersionDTO fv = new FileVersionDTO();
							fv.setAvailable(true);
							fv.setExtension(model.getFile_ext());
							fv.setId(Integer.parseInt(model.getDTOid()));
							fv.setName(model.getFile_name());
							((FilesResultsClickHandler) handler).onLabelClickEvent(fv);
						}
					});
				}

				return h;
			}

		});

		// Window.alert("Completed createSearchResultsGridColumns!");
		return Arrays.asList(new ColumnConfig[] { labelColumn });
	}

	public String getNiceText(Map<String, String> resultsMap) {
		String htmlBuilder = "";
		if (resultsMap.get("doc_type").toString().equals("PROJECT")) {

			ProjectModelType pmt = null;
			if (resultsMap.get("type_pmodel").equals("FUNDING"))
				pmt = ProjectModelType.FUNDING;
			if (resultsMap.get("type_pmodel").equals("NGO"))
				pmt = ProjectModelType.NGO;
			if (resultsMap.get("type_pmodel").equals("LOCAL_PARTNER"))
				pmt = ProjectModelType.LOCAL_PARTNER;

			htmlBuilder += "<div id=\"container\" style=\"white-space:nowrap\">";
			htmlBuilder += "<br><div id=\"image\" style=\"display:inline;padding-left:10px;\">"
					+ getProjectLogo(pmt).getHTML() + "</div>"
					+ "<div id=\"text\" style = \"font-size:13pt;font-family:tahoma;"
					+ "padding-left:16px;vertical-align:top;text-decoration:underline;"
					+ "cursor:pointer;color:#482a1e;display:inline;white-space:nowrap;\">"
					+ resultsMap.get("project_name") + " - " + resultsMap.get("project_fullname") + "</div></div>";
			htmlBuilder += "<div style = \"font-size:11pt;font-family:tahoma;padding-left:15px\">";
			htmlBuilder += "<p>";
			htmlBuilder += "<br>Active Phase: " + resultsMap.get("phase_model_name");
			htmlBuilder += "<br>Organisational Unit: " + resultsMap.get("project_org_unit_name") + " - "
					+ resultsMap.get("project_org_unit_fullname");
			htmlBuilder += "<br>Amendment Status: " + resultsMap.get("amendment_status");
			htmlBuilder += "<br>Project Model: " + resultsMap.get("pmodel_name");
			htmlBuilder += "<br>Project Model Type: " + resultsMap.get("type_pmodel");
			htmlBuilder += "<br>Start Date: " + resultsMap.get("project_startdate");
			htmlBuilder += "<br>Planned Budget: " + resultsMap.get("planned_budget");
			htmlBuilder += "</p></div><br>";
		}
		if (resultsMap.get("doc_type").toString().equals("ORG_UNIT")) {
			htmlBuilder += "<div id=\"container\" style=\"white-space:nowrap;\">";
			htmlBuilder += "<br><div id=\"image\" style=\"display:inline;padding-left:10px;\">"
					+ getOrgUnitLogo().getHTML() + "</div>"
					+ "<div id=\"text\" style = \"font-size:13pt;font-family:tahoma;"
					+ "padding-left:16px;vertical-align:top;text-decoration:underline;"
					+ "cursor:pointer;color:#482a1e;display:inline;white-space:nowrap;\">"
					+ resultsMap.get("org_unit_name") + " - " + resultsMap.get("org_unit_fullname") + "</div></div>";
			htmlBuilder += "<div style = \"font-size:11pt;font-family:tahoma;padding-left:15px\">";
			htmlBuilder += "<p>";
			htmlBuilder += "<br>Model: " + resultsMap.get("org_unit_model_name");
			htmlBuilder += "<br>Model Status: " + resultsMap.get("org_unit_model_status");
			htmlBuilder += "<br>" + "Country: " + resultsMap.get("org_unit_country_iso2") + " - "
					+ resultsMap.get("org_unit_country_name");
			htmlBuilder += "<br>Planned Budget: " + resultsMap.get("org_unit_planned_budget");
			htmlBuilder += "<br>Organization: " + resultsMap.get("org_unit_model_organization_name");
			htmlBuilder += "</p></div><br>";
		}
		if (resultsMap.get("doc_type").toString().equals("CONTACT")) {

			// ContactModelType cmt = null;
			// if (resultsMap.get("contact_model_type").equals("INDIVIDUAL"))
			// cmt = ContactModelType.INDIVIDUAL;
			// if (resultsMap.get("contact_model_type").equals("ORGANIZATION"))
			// cmt = ContactModelType.ORGANIZATION;

			htmlBuilder += "<div id=\"container\" style=\"white-space:nowrap\">";
			htmlBuilder += "<br>" + "<div id=\"image\" style=\"display:inline;padding-left:10px;\">"
					+ IconImageBundle.ICONS.user().createImage() + "</div>"
					+ "<div id=\"text\" style = \"font-size:13pt;font-family:tahoma;"
					+ "padding-left:16px;vertical-align:top;text-decoration:underline;"
					+ "cursor:pointer;color:#482a1e;display:inline;" + "white-space:nowrap;\">";
			if (resultsMap.get("user_firstname") != null) {
				htmlBuilder += resultsMap.get("user_firstname") + " - " + resultsMap.get("user_name");
				htmlBuilder += "</div></div>";
			} else {
				// for organization contacts
				htmlBuilder += resultsMap.get("organization_name");
				htmlBuilder += "</div></div>";
				htmlBuilder += "<div style = \"font-size:11pt;font-family:tahoma;padding-left:15px\">";
				htmlBuilder += "<br>Contact type: " + resultsMap.get("contact_model_type");
				htmlBuilder += "<br>Contact Model Name: " + resultsMap.get("contact_model_name");
				htmlBuilder += "</div><br>";
				return htmlBuilder;
			}
			htmlBuilder += "<div style = \"font-size:11pt;font-family:tahoma;padding-left:15px\">";
			htmlBuilder += "<p>";
			htmlBuilder += "<br>Email ID: " + resultsMap.get("user_email");
			htmlBuilder += "<br>Locale: " + resultsMap.get("user_locale");
			htmlBuilder += "<br>Contact type: " + resultsMap.get("contact_model_type");
			htmlBuilder += "<br>Contact Model Name: " + resultsMap.get("contact_model_name");
			htmlBuilder += "</p></div><br>";

		}
		if (resultsMap.get("doc_type").toString().equals("FILE")) {
			htmlBuilder += "<div id=\"container\" style=\"white-space:nowrap\">";
			htmlBuilder += "<br><div id=\"image\" style=\"display:inline;padding-left:10px;\">"
					+ IconImageBundle.ICONS.attach().createImage() + "</div>"
					+ "<div id=\"text\" style = \"font-size:13pt;font-family:tahoma;padding-left:16px;"
					+ "vertical-align:top;text-decoration:underline;cursor:pointer;color:#482a1e;"
					+ "display:inline;white-space:nowrap;\">";
			if (resultsMap.get("title") != null) {
				htmlBuilder += resultsMap.get("title") + " - " + resultsMap.get("file_name") + "."
						+ resultsMap.get("file_ext");
			} else {
				htmlBuilder += resultsMap.get("file_name") + "." + resultsMap.get("file_ext");
			}
			htmlBuilder += "</div></div>";
			htmlBuilder += "<div style = \"font-size:11pt;font-family:tahoma;padding-left:15px;"
					+ "word-wrap:break-word;\">";
			htmlBuilder += "<p>";
			htmlBuilder += "<br>Author: " + resultsMap.get("file_author") + " - " + resultsMap.get("file_author_email");
			htmlBuilder += "</p>";
			htmlBuilder += "<p>";
			String contentString;
			if (resultsMap.get("title") != null) {
				contentString = resultsMap.get("content")
						.substring(resultsMap.get("title").length() + 1, min(900, resultsMap.get("content").length()))
						.replaceAll("\\\\n", "");
			} else {
				contentString = resultsMap.get("content").substring(0, min(900, resultsMap.get("content").length()))
						.replaceAll("\\\\n", "");
			}
			int i;
			for (i = 180; i <= min(800, contentString.length()); i += 180) {
				htmlBuilder += "<br>" + contentString.substring(i - 180, i);
			}
			htmlBuilder += "<br>" + contentString.substring(i - 180, contentString.length()) + "...<br>";
			htmlBuilder += "</p></div><br>";
		}
		return htmlBuilder;
	}

	private int min(int i, int length) {
		// TODO Auto-generated method stub
		if (i < length)
			return i;
		else
			return length;
	}

	public AbstractImagePrototype getProjectLogo(final ProjectModelType projectType) {

		final AbstractImagePrototype projectIcon = FundingIconProvider.getProjectTypeIcon(projectType, IconSize.MEDIUM);
		return projectIcon;
	}

	public HTML getContactLogo(ContactModelType type) {
		// Element spanElement = DOM.createSpan();
		HTML avatar = new HTML();
		// avatar.setStyleName();
		avatar.setStyleName("contact-card-avatar");
		avatar.getElement().getStyle().clearBackgroundImage();
		avatar.getElement().getStyle().clearPadding();
		avatar.getElement().getStyle().clearHeight();
		avatar.getElement().getStyle().clearWidth();
		avatar.setWidth(36 + "px");
		avatar.setHeight(36 + "px");
		avatar.getElement().setAttribute("display", "inline");
		avatar.getElement().setAttribute("padding-left", "10px");

		switch (type) {
		case INDIVIDUAL:
			// avatar.getElement().setAttribute("background-image",
			// "url(\"../images/icon/individual-128.png\");");
			avatar.addStyleName("contact-card-avatar-individual");
			break;
		case ORGANIZATION:
			// avatar.getElement().setAttribute("background-image",
			// "url(\"../images/icon/organization-128.png\");");
			// avatar.addStyleName("background-image:
			// url(\"../images/icon/organization-128.png\");");
			avatar.addStyleName("contact-card-avatar-organization");
			break;
		default:
			throw new IllegalStateException("Unknown ContactModelType : " + type);
		}
		return avatar;
	}

	public AbstractImagePrototype getOrgUnitLogo() {
		return OrgUnitImageBundle.ICONS.orgUnitSmall();
	}

}
