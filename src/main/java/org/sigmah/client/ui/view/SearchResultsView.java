
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
import java.util.List;
import java.util.Map;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.search.SearchService;
import org.sigmah.client.search.SearchServiceAsync;
import org.sigmah.client.ui.presenter.SearchResultsPresenter;
import org.sigmah.client.ui.presenter.contact.dashboardlist.ContactsListWidget;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.orgunit.OrgUnitTreeGrid;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.dto.search.SearchResultsDTO;

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
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.sigmah.client.ui.presenter.DashboardPresenter.ReminderOrMonitoredPointHandler;


/**
 * Dashboard view.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class SearchResultsView extends AbstractView implements SearchResultsPresenter.View {
	
	private String searchText;
	
	private static final String EXPECTED_DATE_LABEL_STYLE = "points-date-exceeded";

	private ContentPanel searchResultsPanel;
	ListStore<SearchResultsDTO> searchResultsStore;
	
	private LayoutContainer centerContainer;

//	private ContentPanel resultsShowPanel;
//	private ListStore<SearchResultsDTO> remindersStore;
   
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		//default
	}
	
	public void initialize(String searchText) {
		this.searchText = searchText;
		centerContainer = Layouts.vBox();
		Window.alert("Searchtext set to " + searchText );
		createSearchResultsPanel();
		addResultsPanel();
		centerContainer.add(searchResultsPanel, Layouts.vBoxData(Margin.TOP));
		//centerContainer.add(addResultsPanel(), Layouts.vBoxData(Margin.BOTTOM));
		add(centerContainer);
	}




	// -------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------------------

	private void createSearchResultsPanel() {

		//Window.alert("Searchtext is currently " + searchText );
		setContentPanel("Search results for \"" + searchText + "\"", false, null, null );

	}
	
	public void setContentPanel(String title, boolean collapsible, Layout layout, Scroll scroll, String... stylenames) {
		
//		searchResultsPanel = new VerticalPanel();

		searchResultsPanel = new ContentPanel(layout != null ? layout : new FitLayout());

		searchResultsPanel.setHeadingHtml(ClientUtils.isNotBlank(title) ? title : null);
		searchResultsPanel.setHeaderVisible(ClientUtils.isNotBlank(title));
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

	}
	
	private static List<ColumnConfig> createSearchResultsGridColumns() {

//		final DateTimeFormat format = DateUtils.DATE_SHORT;
//		final Date now = new Date();

		// Icon column.
//		final ColumnConfig iconColumn = new ColumnConfig();
//		iconColumn.setId("icon");
//		iconColumn.setHeaderHtml("");
//		iconColumn.setWidth(16);
//		iconColumn.setResizable(false);
//		iconColumn.setRenderer(new GridCellRenderer<ReminderDTO>() {
//
//			@Override
//			public Object render(final ReminderDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
//					final ListStore<ReminderDTO> store, final Grid<ReminderDTO> grid) {
//
//				if (DateUtils.DAY_COMPARATOR.compare(now, model.getExpectedDate()) > 0) {
//					return IconImageBundle.ICONS.overdueReminder().createImage();
//				} else {
//					return IconImageBundle.ICONS.openedReminder().createImage();
//				}
//			}
//		});

        // Label column.
        final ColumnConfig labelColumn = new ColumnConfig();
        labelColumn.setId("label");
        labelColumn.setHeaderHtml("Label");
        labelColumn.setWidth(100);
        
        // Add link
        labelColumn.setRenderer(new GridCellRenderer<SearchResultsDTO>() {

			@Override
			public Object render(final SearchResultsDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<SearchResultsDTO> store,
					Grid<SearchResultsDTO> grid) {

				final com.google.gwt.user.client.ui.Label label = new com.google.gwt.user.client.ui.Label(model.getResult());
				label.addStyleName("hyperlink-label");
				label.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
//                        handler.onLabelClickEvent(model.getProjectId());
					}
				});
                
                //label.setTitle(I18N.CONSTANTS.projectLabelWithDots() + ' ' + model.getProjectCode() + " - " + model.getProjectName());
				label.setTitle(model.getResult());
				return label;
			}

		});

		// Expected date column.
//		final ColumnConfig expectedDateColumn = new ColumnConfig();
//		expectedDateColumn.setId(ReminderDTO.EXPECTED_DATE);
//		expectedDateColumn.setHeaderHtml(I18N.CONSTANTS.monitoredPointExpectedDate());
//		expectedDateColumn.setWidth(60);
//		expectedDateColumn.setDateTimeFormat(format);
//		expectedDateColumn.setRenderer(new GridCellRenderer<ReminderDTO>() {
//
//			@Override
//			public Object render(final ReminderDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
//					final ListStore<ReminderDTO> store, final Grid<ReminderDTO> grid) {
//
//				final Label label = new Label(format.format(model.getExpectedDate()));
//				if (!model.isCompleted() && DateUtils.DAY_COMPARATOR.compare(now, model.getExpectedDate()) > 0) {
//					label.addStyleName(EXPECTED_DATE_LABEL_STYLE);
//				}
//				return label;
//			}
//		});

		return Arrays.asList(new ColumnConfig[] {
				//iconColumn,
				labelColumn,
				//expectedDateColumn
		});
	}
	
	private void addResultsPanel() {

		Grid<SearchResultsDTO> searchResultsGrid = new Grid<SearchResultsDTO>(searchResultsStore, new ColumnModel(createSearchResultsGridColumns()));
		searchResultsGrid.getView().setForceFit(true);
		searchResultsGrid.setAutoExpandColumn("Label");

//		remindersPanel = Panels.content(I18N.CONSTANTS.reminderPoints());
		//resultsShowPanel = setContentPanel("Search Results", false, null, null);
		searchResultsPanel.add(searchResultsGrid);

	}
	
	public void addSearchData(Object searchData){
		if( searchData != null ){
			Window.alert("Received search results!: \n" + searchData.toString());
			searchResultsStore = new ListStore<SearchResultsDTO>();
			for (Object object : (ArrayList)searchData ) {
			    searchResultsStore.add(object != null ? (SearchResultsDTO)object : null);
			    //Window.alert("Received search result!: \n" + object.getResult());
			}
			
//			for( SearchResultsDTO element: searchResultsStore ){
//				
//				Window.alert("Received search result!: \n" + element.getResult());
//				
////				Widget widget = new Widget();
////				HorizontalPanel panel = new HorizontalPanel();
////				HTML html = new HTML("---------------------------------------------------\n" + 
////				element.getResult() + "---------------------------------------------------\n");
////				panel.add(html);
////				searchResultsPanel.add(panel);
//			}
		}else{
			Window.alert("Failed to receive search results!");
		}
		
	}

}


