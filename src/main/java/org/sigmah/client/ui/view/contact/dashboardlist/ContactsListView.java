package org.sigmah.client.ui.view.contact.dashboardlist;

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

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BaseFilterPagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.contact.dashboardlist.ContactsListWidget;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.command.result.ContactHistory;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.referential.ContactModelType;

public class ContactsListView extends AbstractView implements ContactsListWidget.View {

	// CSS style names.
	private static final String STYLE_IMPORTANT_LABEL = "important-label";
	private static final String STYLE_CONTACT_GRID_NAME = "contact-grid-name";

	private ContentPanel contactTreePanel;
	private Grid<DashboardContact> contactTreeGrid;
	private GridFilters gridFilters;

	private ToolBar toolbar;
	private PagingToolBar pagingToolBar;
	private PagingContactsProxy proxy;
	private PagingLoader<PagingLoadResult<DashboardContact>> pagingLoader;

	private Button addContactButton;
	private Button importButton;
	private Button exportButton;

	// Specific Handlers provided by presenter.
	private GridEventHandler<DashboardContact> treeHandler;

	/**
	 * Initializes the widget view.
	 */
	@Override
	public void initialize() {

    // Paging
		proxy = new PagingContactsProxy();

		pagingLoader = new BasePagingLoader<PagingLoadResult<DashboardContact>>(proxy) {
			@Override
			protected Object newLoadConfig() {
				return new BaseFilterPagingLoadConfig();
			}
		};
		pagingLoader.setRemoteSort(true);

		pagingToolBar = new PagingToolBar(10);
		pagingToolBar.bind(pagingLoader);

		// Store.
		final ListStore<DashboardContact> contactStore = new ListStore<DashboardContact>(pagingLoader);
		contactStore.setMonitorChanges(true);

		// Default sort order of the contacts grid.
		pagingLoader.setSortField(ContactDTO.NAME);
		pagingLoader.setSortDir(Style.SortDir.ASC);

		// Grid.
		contactTreeGrid = new Grid<DashboardContact>(contactStore, buildContactGridColumnModel());
		contactTreeGrid.setBorders(true);
		contactTreeGrid.setTrackMouseOver(false);

		// Apply grid filters
		gridFilters = new GridFilters();

		initGridFilters();

		contactTreeGrid.addPlugin(gridFilters);

		toolbar = new ToolBar();
		addContactButton = new Button(I18N.CONSTANTS.addContact());
		toolbar.add(addContactButton);
		importButton = new Button(I18N.CONSTANTS.importContact());
		toolbar.add(importButton);

		// Preparing 'export' functionality.
		exportButton = new Button(I18N.CONSTANTS.exportAll(), IconImageBundle.ICONS.excel());
		toolbar.add(exportButton);

		// Panel
		contactTreePanel = Panels.content(I18N.CONSTANTS.contacts());
		contactTreePanel.setTopComponent(toolbar);
		contactTreePanel.setBottomComponent(pagingToolBar);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget() {
		return contactTreePanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateAccessibilityState(final boolean authorized) {

		contactTreePanel.removeAll();

		if (authorized) {
			contactTreePanel.add(contactTreeGrid);

		} else {
			final HTML insufficient = new HTML(I18N.CONSTANTS.permViewContactsInsufficient());
			insufficient.addStyleName(STYLE_IMPORTANT_LABEL);
			contactTreePanel.add(insufficient);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentPanel getContactsPanel() {
		return contactTreePanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GridFilters getGridFilters() {
		return gridFilters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getAddButton() {
		return addContactButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getImportButton() {
		return importButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getExportButton() {
		return exportButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Grid<DashboardContact> getGrid() {
		return contactTreeGrid;
	}

	@Override
	public void addContact(DashboardContact contact) {
		proxy.addContact(contact);
		pagingToolBar.refresh();
	}

	@Override
	public void clearContacts() {
		proxy.clearContacts();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListStore<DashboardContact> getStore() {
		return contactTreeGrid.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGridEventHandler(final GridEventHandler<DashboardContact> handler) {
		this.treeHandler = handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateToolbar(final boolean addContact, final boolean importContact, final boolean exportContact) {

		toolbar.remove(addContactButton);
		toolbar.remove(importButton);
		toolbar.remove(exportButton);

		if (addContact) {
			toolbar.add(addContactButton);
		}
		if (importContact) {
			toolbar.add(importButton);
		}
		if (exportContact) {
			toolbar.add(exportButton);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void syncSize() {
		contactTreeGrid.syncSize();
	}

	// ---------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------

	/**
	 * Builds and returns the columns model for the contacts tree grid.
	 * 
	 * @return The contact tree grid columns model.
	 */
	private ColumnModel buildContactGridColumnModel() {

		final DateTimeFormat format = DateUtils.DATE_SHORT;

		// Type
		final ColumnConfig typeColumn = new ColumnConfig(ContactDTO.TYPE, I18N.CONSTANTS.contactTypeLabel(), 75);
		typeColumn.setRenderer(new GridCellRenderer<DashboardContact>() {

			@Override
			public Object render(final DashboardContact model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
													 final ListStore<DashboardContact> store, final Grid<DashboardContact> grid) {

				ContactModelType type = (ContactModelType) model.get(property);

				String typeLabel = I18N.CONSTANTS.contactTypeIndividualLabel();

				if(type == ContactModelType.ORGANIZATION) {
					typeLabel = I18N.CONSTANTS.contactTypeOrganizationLabel();
				}

				return createContactGridText(typeLabel);
			}
		});

		// Name
		final ColumnConfig nameColumn = new ColumnConfig(ContactDTO.NAME, I18N.CONSTANTS.contactName(), 100);
		nameColumn.setRenderer(new GridCellRenderer<DashboardContact>() {

			@Override
			public Object render(final DashboardContact model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
													 final ListStore<DashboardContact> store, final Grid<DashboardContact> grid) {


				final Anchor nameLink = new Anchor((String) model.get(property));

				nameLink.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						if (treeHandler == null) {
							N10N.warn("Not implemented yet.");
							return;
						}
						treeHandler.onRowClickEvent(model);
					}
				});

				final com.google.gwt.user.client.ui.Grid panel = new com.google.gwt.user.client.ui.Grid(1, 1);
				panel.setCellPadding(0);
				panel.setCellSpacing(0);

				panel.setWidget(0, 0, nameLink);
				panel.getCellFormatter().addStyleName(0, 0, STYLE_CONTACT_GRID_NAME);

				return panel;
			}
		});

		// Firstname
		final ColumnConfig firstnameColumn = new ColumnConfig(ContactDTO.FIRSTNAME, I18N.CONSTANTS.contactFirstName(), 75);
		firstnameColumn.setRenderer(new GridCellRenderer<DashboardContact>() {

			@Override
			public Object render(final DashboardContact model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<DashboardContact> store, final Grid<DashboardContact> grid) {
				return createContactGridText((String) model.get(property));
			}
		});

		// Change type
		final ColumnConfig changeTypeColumn = new ColumnConfig(ContactHistory.FORMATTED_CHANGE_TYPE, I18N.CONSTANTS.contactChangeType(), 100);
		firstnameColumn.setRenderer(new GridCellRenderer<DashboardContact>() {

			@Override
			public Object render(final DashboardContact model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
													 final ListStore<DashboardContact> store, final Grid<DashboardContact> grid) {
				return createContactGridText((String) model.get(property));
			}
		});

		// Change subject
		final ColumnConfig changeSubjectColumn = new ColumnConfig(ContactHistory.SUBJECT, I18N.CONSTANTS.contactChangeSubject(), 75);
		firstnameColumn.setRenderer(new GridCellRenderer<DashboardContact>() {

			@Override
			public Object render(final DashboardContact model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
													 final ListStore<DashboardContact> store, final Grid<DashboardContact> grid) {
				return createContactGridText((String) model.get(property));
			}
		});

		// Change value
		final ColumnConfig changeValueColumn = new ColumnConfig(ContactHistory.FORMATTED_VALUE, I18N.CONSTANTS.contactChangeValue(), 75);
		firstnameColumn.setRenderer(new GridCellRenderer<DashboardContact>() {

			@Override
			public Object render(final DashboardContact model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
													 final ListStore<DashboardContact> store, final Grid<DashboardContact> grid) {
				return createContactGridText((String) model.get(property));
			}
		});

		// Email
		final ColumnConfig emailColumn = new ColumnConfig(ContactDTO.EMAIL, I18N.CONSTANTS.contactEmailAddress(), 150);
		emailColumn.setRenderer(new GridCellRenderer<DashboardContact>() {

			@Override
			public Object render(final DashboardContact model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
													 final ListStore<DashboardContact> store, final Grid<DashboardContact> grid) {
				return createContactGridText((String) model.get(property));
			}
		});
		emailColumn.setHidden(true);

		// Id
		final ColumnConfig idColumn = new ColumnConfig(ContactDTO.ID, I18N.CONSTANTS.contactId(), 100);
		emailColumn.setRenderer(new GridCellRenderer<DashboardContact>() {

			@Override
			public Object render(final DashboardContact model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
													 final ListStore<DashboardContact> store, final Grid<DashboardContact> grid) {
				return createContactGridText((String) model.get(property));
			}
		});
		idColumn.setHidden(true);

		// Organization
		final ColumnConfig organizationColumn = new ColumnConfig(ContactDTO.PARENT, I18N.CONSTANTS.contactDirectMembership(), 100);
		organizationColumn.setRenderer(new GridCellRenderer<DashboardContact>() {

			@Override
			public Object render(final DashboardContact model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<DashboardContact> store, final Grid<DashboardContact> grid) {
				return createContactGridText(organizationName((ContactDTO)model.get(property)));
			}
		});
		organizationColumn.setHidden(true);

		// Root organization
		final ColumnConfig rootOrganizationColumn = new ColumnConfig(ContactDTO.ROOT, I18N.CONSTANTS.contactTopMembership(), 100);
		rootOrganizationColumn.setRenderer(new GridCellRenderer<DashboardContact>() {

			@Override
			public Object render(final DashboardContact model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
													 final ListStore<DashboardContact> store, final Grid<DashboardContact> grid) {
				return createContactGridText(organizationName((ContactDTO)model.get(property)));
			}
		});
		rootOrganizationColumn.setHidden(true);

		// Change date
		final ColumnConfig changeDateColumn = new ColumnConfig(ContactHistory.UPDATED_AT, I18N.CONSTANTS.contactChangeDate(), 100);
		changeDateColumn.setDateTimeFormat(format);
		firstnameColumn.setRenderer(new GridCellRenderer<DashboardContact>() {

			@Override
			public Object render(final DashboardContact model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
													 final ListStore<DashboardContact> store, final Grid<DashboardContact> grid) {
				final Date d = model.get(property);
				return createContactGridText(d != null ? format.format(d) : "");
			}
		});
		changeDateColumn.setHidden(true);

		// Change comment
		final ColumnConfig changeCommentColumn = new ColumnConfig(ContactHistory.COMMENT, I18N.CONSTANTS.contactChangeComment(), 100);
		firstnameColumn.setRenderer(new GridCellRenderer<DashboardContact>() {

			@Override
			public Object render(final DashboardContact model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
													 final ListStore<DashboardContact> store, final Grid<DashboardContact> grid) {
				return createContactGridText((String) model.get(property));
			}
		});
		changeCommentColumn.setHidden(true);

		return new ColumnModel(Arrays.asList(typeColumn, nameColumn, firstnameColumn, changeTypeColumn, changeSubjectColumn,
				changeValueColumn, emailColumn, idColumn, organizationColumn, rootOrganizationColumn, changeDateColumn, changeCommentColumn));
	}

	private static Widget createContactGridText(final String content) {

		final Html label = new Html(content);

		return label;
	}

	private static String organizationName(ContactDTO organization) {
		String organizationLabel = "";

		if(organization != null) {
			organizationLabel = organization.getOrganizationName();
		}

		return organizationLabel;
	}

	/**
	 * Grid filters for contacts' TreeGrid.
	 */
	private void initGridFilters() {

		gridFilters.setLocal(false);

		// Data index of each filter should be identical with column id in ColumnConfig of TreeGrid.

		// Common filters
		gridFilters.addFilter(new StringFilter(ContactDTO.TYPE));
		gridFilters.addFilter(new StringFilter(ContactDTO.NAME));
		gridFilters.addFilter(new StringFilter(ContactDTO.FIRSTNAME));
		gridFilters.addFilter(new StringFilter(ContactHistory.FORMATTED_CHANGE_TYPE));
		gridFilters.addFilter(new StringFilter(ContactHistory.SUBJECT));
		gridFilters.addFilter(new StringFilter(ContactHistory.FORMATTED_VALUE));
		gridFilters.addFilter(new StringFilter(ContactDTO.EMAIL));
		gridFilters.addFilter(new StringFilter(ContactDTO.ID));
		gridFilters.addFilter(new StringFilter(ContactDTO.PARENT));
		gridFilters.addFilter(new StringFilter(ContactDTO.ROOT));
		gridFilters.addFilter(new DateFilter(ContactHistory.UPDATED_AT));
		gridFilters.addFilter(new StringFilter(ContactHistory.COMMENT));
	}

}
