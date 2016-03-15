package org.sigmah.client.ui.view.project.treegrid;

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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget.HandlerProvider;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.res.icon.dashboard.DashboardImageBundle;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.client.ui.res.icon.project.category.CategoryIconProvider;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.RatioBar;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.form.ProjectModelTypeField;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.DateUtils;
import org.sigmah.client.util.NumberUtils;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;
import org.sigmah.shared.dto.referential.ProjectModelType;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnHeader;
import com.extjs.gxt.ui.client.widget.grid.ColumnHeader.Head;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.Filter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.ListFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.NumericFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.menu.CheckMenuItem;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.allen_sauer.gwt.log.client.Log;
/**
 * {@link ProjectsListWidget} corresponding view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectsListView extends AbstractView implements ProjectsListWidget.View {

	// CSS style names.
	private static final String STYLE_FILTERED_COLUMN_HEADER = "filtered-column-header";
	private static final String STYLE_FLEXIBILITY_ELEMENT_LABEL = "flexibility-element-label";
	private static final String STYLE_PROJECT_STARRED_ICON = "project-starred-icon";
	private static final String STYLE_PROJECT_REFRESH_BUTTON = "project-refresh-button";
	private static final String STYLE_IMPORTANT_LABEL = "important-label";
	private static final String STYLE_PROJECT_GRID_NODE = "project-grid-node";
	private static final String STYLE_PROJECT_GRID_LEAF = "project-grid-leaf";
	private static final String STYLE_PROJECT_GRID_CODE_ICON = "project-grid-code-icon";
	private static final String STYLE_PROJECT_GRID_CODE = "project-grid-code";
	private static final String PORJECT_CODE_COLUMN_ID="projectCodeColumnId_";

	/**
	 * HTML double spaces characters.
	 */
	private static final String DOUBLE_SPACES = "&nbsp;&nbsp;";

	/**
	 * Refresh time format.
	 */
	private static final DateTimeFormat REFRESH_TIME_FORMAT = DateTimeFormat.getFormat("HH:mm");

	private ContentPanel projectTreePanel;
	private TreeGrid<ProjectDTO> projectTreeGrid;
	private Button filterButton;
	private GridFilters gridFilters;

	private ToolBar toolbar;
	private SeparatorToolItem refreshSeparator;
	private Button refreshButton;
	private Label refreshDateLabel;
	private Button exportButton;
	private ProjectModelTypeField projectModelTypeField;

	// Specific Handlers provided by presenter.
	private HandlerProvider handlerProvider;
	private TreeGridEventHandler<ProjectDTO> treeHandler;

	/**
	 * Initializes the widget view.
	 */
	@Override
	public void initialize() {

		// Store.
		final TreeStore<ProjectDTO> projectStore = new TreeStore<ProjectDTO>();
		projectStore.setMonitorChanges(true);

		// Default sort order of the projects grid.
		projectStore.setSortInfo(new SortInfo(ProjectDTO.NAME, SortDir.ASC));

		// Grid.
		projectTreeGrid = new TreeGrid<ProjectDTO>(projectStore, buildProjectGridColumnModel());
		projectTreeGrid.setBorders(true);
		projectTreeGrid.getStyle().setNodeOpenIcon(null);
		projectTreeGrid.getStyle().setNodeCloseIcon(null);
		projectTreeGrid.getStyle().setLeafIcon(null);
		projectTreeGrid.setAutoExpandColumn(ProjectDTO.FULL_NAME);
		projectTreeGrid.setTrackMouseOver(false);
		projectTreeGrid.setAutoExpand(true);

		// Apply grid filters
		gridFilters = new GridFilters() {

			private CheckMenuItem checkItem;
			private SeparatorMenuItem seperator;
			private Menu filterMenuO;
			private StoreFilter<ModelData> currentFilterO;

			private DelayedTask deferredUpdateO = new DelayedTask(new Listener<BaseEvent>() {

				@Override
				public void handleEvent(BaseEvent be) {
					reload();
				}
			});

			@Override
			protected void onContextMenu(GridEvent<?> be) {

				final int column = be.getColIndex();

				if (seperator == null) {
					seperator = new SeparatorMenuItem();
				}
				seperator.removeFromParent();

				if (checkItem == null) {
					checkItem = new CheckMenuItem(getMessages().getFilterText());

					checkItem.addListener(Events.CheckChange, new Listener<MenuEvent>() {

						@Override
						public void handleEvent(MenuEvent me) {
							onCheckChange(me);
						}
					});
					checkItem.addListener(Events.BeforeCheckChange, new Listener<MenuEvent>() {

						@Override
						public void handleEvent(MenuEvent me) {
							onBeforeCheck(me);
						}
					});
				}
				checkItem.removeFromParent();
				checkItem.setData("index", column);

				final Filter f = getFilter(grid.getColumnModel().getColumn(column).getDataIndex());
				if (f != null) {
					checkItem.show();

					filterMenuO = f.getMenu();
					checkItem.setChecked(f.isActive(), true);
					checkItem.setSubMenu(filterMenuO);

					final Menu menu = be.getMenu();
					menu.add(seperator);
					if (columnModel.getDataIndex(column).equals(TIME_COLUMN)) {
						checkItem.setText(I18N.CONSTANTS.closedProjectsFilterText());
					} else {
						checkItem.setText(GXT.MESSAGES.gridFilters_filterText());
					}
					menu.add(checkItem);
				}
			}

			@Override
			protected void onStateChange(Filter filter) {

				if (checkItem != null && checkItem.isAttached()) {
					checkItem.setChecked(filter.isActive(), true);
				}

				if ((isAutoReload() || isLocal())) {
					deferredUpdateO.delay(getUpdateBuffer());
				}

				updateColumnHeadings();
			}

			@Override
			protected void reload() {
				if (isLocal()) {
					if (currentFilterO != null) {
						getStore().removeFilter(currentFilterO);
					}

					currentFilterO = getModelFilter();
					// BUGFIX #742 : Verifying that grid is not null to avoid a NPE in getStore().
					if(currentFilterO != null && grid != null) {
						getStore().addFilter(currentFilterO);

						if (!getStore().isFiltered()) {
							getStore().applyFilters("");
						}
					}

				} else {
					deferredUpdateO.cancel();

					final Loader<?> l = getLoader(getStore());
					if (l != null) {
						l.load();
					}
				}
			}

			@Override
			public void updateColumnHeadings() {
				// BUGFIX #742 : Verifying that grid is not null to avoid a NPE when the user cannot access projects.
				if(grid == null || grid.getColumnModel() == null) {
					return;
				}
				
				final int cols = grid.getColumnModel().getColumnCount();

				for (int i = 0; i < cols; i++) {

					final ColumnConfig config = grid.getColumnModel().getColumn(i);

					if (config.isHidden()) {
						continue;
					}

					final ColumnHeader header = grid.getView().getHeader();

					if (header == null) {
						continue;
					}

					final Head h = header.getHead(i);
					if (h != null && h.isRendered()) {
						final Filter f = getFilter(config.getDataIndex());
						if (f != null) {
							h.el().setStyleName(STYLE_FILTERED_COLUMN_HEADER, f.isActive());
						}
					}
				}
			}
		};

		projectTreeGrid.addPlugin(createGridFilters());

		// Top panel
		final HTML headLabel = new HTML(DOUBLE_SPACES + I18N.CONSTANTS.projectTypeFilter() + I18N.CONSTANTS.form_label_separator() + DOUBLE_SPACES);
		headLabel.addStyleName(STYLE_FLEXIBILITY_ELEMENT_LABEL);

		// Expand all button.
		final Button expandButton = Forms.button("", IconImageBundle.ICONS.expand(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				projectTreeGrid.expandAll();
			}
		});

		// Collapse all button.
		final Button collapseButton = Forms.button("", IconImageBundle.ICONS.collapse(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				projectTreeGrid.collapseAll();
			}
		});

		// Filter button.
		filterButton = new Button(I18N.CONSTANTS.filter(), IconImageBundle.ICONS.filter());

		// Refresh button.
		refreshButton = new Button(I18N.CONSTANTS.refreshProjectList(), IconImageBundle.ICONS.refresh());
		refreshButton.setToolTip(I18N.CONSTANTS.refreshProjectListDetails());
		refreshButton.addStyleName(STYLE_PROJECT_REFRESH_BUTTON);

		// Refresh date.
		refreshDateLabel = new Label();
		refreshSeparator = new SeparatorToolItem();

		toolbar = new ToolBar();
		toolbar.add(expandButton);
		toolbar.add(collapseButton);
		toolbar.add(new SeparatorToolItem());
		toolbar.add(filterButton);
		toolbar.add(new WidgetComponent(headLabel));

		projectModelTypeField = new ProjectModelTypeField(null, false);
		toolbar.add(projectModelTypeField);
		projectModelTypeField.setValue(ProjectModelType.NGO); // Default selected option.

		// Preparing 'export' functionality.
		toolbar.add(new FillToolItem());
		exportButton = new Button(I18N.CONSTANTS.exportAll(), IconImageBundle.ICONS.excel());

		// Panel
		projectTreePanel = Panels.content(I18N.CONSTANTS.projects());
		projectTreePanel.setTopComponent(toolbar);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget() {
		return projectTreePanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHandlerProvider(final HandlerProvider handlerProvider) {
		this.handlerProvider = handlerProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateAccessibilityState(final boolean authorized) {

		projectTreePanel.removeAll();

		if (authorized) {
			projectTreePanel.add(projectTreeGrid);

		} else {
			final HTML insufficient = new HTML(I18N.CONSTANTS.permViewProjectsInsufficient());
			insufficient.addStyleName(STYLE_IMPORTANT_LABEL);
			projectTreePanel.add(insufficient);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentPanel getProjectsPanel() {
		return projectTreePanel;
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
	public Field<ProjectModelType> getProjectModelTypeField() {
		return projectModelTypeField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateRefreshingDate(final Date date) {

		if (date == null) {
			return;
		}
		refreshDateLabel.setHtml('(' + REFRESH_TIME_FORMAT.format(date) + ')');
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getRefreshButton() {
		return refreshButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getFilterButton() {
		return filterButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreeGrid<ProjectDTO> getTreeGrid() {
		return projectTreeGrid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreeStore<ProjectDTO> getStore() {
		return projectTreeGrid.getTreeStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTreeGridEventHandler(final TreeGridEventHandler<ProjectDTO> handler) {
		this.treeHandler = handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateToolbar(final boolean refresh, final boolean export) {

		if (refresh) {
			// Inserts 'refresh' functionality.
			toolbar.insert(refreshSeparator, 0);
			toolbar.insert(refreshDateLabel, 0);
			toolbar.insert(refreshButton, 0);

		} else if (refreshSeparator.isAttached()) {
			// Removes 'refresh' functionality.
			toolbar.remove(refreshSeparator);
			toolbar.remove(refreshDateLabel);
			toolbar.remove(refreshButton);
		}

		if (export) {
			// Inserts 'export' functionality.
			toolbar.add(exportButton);

		} else if (exportButton.isAttached()) {
			// Removes 'export' functionality.
			toolbar.remove(exportButton);
		}
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
	public void syncSize() {
		projectTreeGrid.syncSize();
	}

	// ---------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------

	/**
	 * Builds and returns the columns model for the projects tree grid.
	 * 
	 * @return The project tree grid columns model.
	 */
	private ColumnModel buildProjectGridColumnModel() {

		final DateTimeFormat format = DateUtils.DATE_SHORT;

		// Starred icon
		final ColumnConfig starredIconColumn = new ColumnConfig("starred", "", 30);
		starredIconColumn.setRenderer(new GridCellRenderer<ProjectDTO>() {

			@Override
			public Object render(final ProjectDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectDTO> store, final Grid<ProjectDTO> grid) {

				// A star icon
				final Image icon;

				if (handlerProvider.isFavoriteProject(model)) {
					// Favorite project (filled star).
					icon = DashboardImageBundle.ICONS.star().createImage();
					icon.setTitle(I18N.CONSTANTS.projectStarred_tooltip_on());

				} else {
					// Non-favorite project (empty star).
					icon = DashboardImageBundle.ICONS.emptyStar().createImage();
					icon.setTitle(I18N.CONSTANTS.projectStarred_tooltip_off());
				}

				// Star icon click-handler
				icon.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(final ClickEvent event) {
						handlerProvider.onStarIconClicked(model);
					}
				});

				icon.addStyleName(STYLE_PROJECT_STARRED_ICON);

				return icon;
			}
		});

		// Code
		final ColumnConfig codeColumn = new ColumnConfig(ProjectDTO.NAME, I18N.CONSTANTS.projectName(), 110);
		codeColumn.setRenderer(new WidgetTreeGridCellRenderer<ProjectDTO>() {		
			@Override
			public Widget getWidget(final ProjectDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectDTO> store, final Grid<ProjectDTO> grid) {

				final Anchor nameLink = new Anchor((String) model.get(property));
				nameLink.ensureDebugId(nameLink.getElement(),PORJECT_CODE_COLUMN_ID+model.getId());
				if (!model.isLeaf()) {
					nameLink.setStyleName(STYLE_PROJECT_GRID_NODE );
				} else {
					nameLink.setStyleName(STYLE_PROJECT_GRID_LEAF);
				}
				
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

				final com.google.gwt.user.client.ui.Grid panel = new com.google.gwt.user.client.ui.Grid(1, 2);
				panel.setCellPadding(0);
				panel.setCellSpacing(0);

				panel.setWidget(0, 0, FundingIconProvider.getProjectTypeIcon(handlerProvider.getProjectModelType(model), IconSize.SMALL_MEDIUM).createImage());
				panel.getCellFormatter().addStyleName(0, 0, STYLE_PROJECT_GRID_CODE_ICON);
				nameLink.getElement().getId();
				nameLink.getElement().setId(PORJECT_CODE_COLUMN_ID+"-4"+model.getId());
				panel.setWidget(0, 1, nameLink);
				panel.getCellFormatter().addStyleName(0, 1, STYLE_PROJECT_GRID_CODE);
				//panel.getElement().setId(PORJECT_CODE_COLUMN_ID+model.getId());
				return panel;
			}
		});

		// Title
		final ColumnConfig titleColumn = new ColumnConfig(ProjectDTO.FULL_NAME, I18N.CONSTANTS.projectFullName(), 230);
		titleColumn.setRenderer(new GridCellRenderer<ProjectDTO>() {

			@Override
			public Object render(final ProjectDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectDTO> store, final Grid<ProjectDTO> grid) {

				String title = (String) model.get(property);

				if (model.getParent() != null) {
					title = DOUBLE_SPACES + DOUBLE_SPACES + title;
				}

				return createProjectGridText(model, title);
			}
		});

		// Current phase
		final ColumnConfig currentPhaseName = new ColumnConfig(ProjectDTO.CURRENT_PHASE_NAME, I18N.CONSTANTS.projectActivePhase(), 150);
		currentPhaseName.setRenderer(new GridCellRenderer<ProjectDTO>() {

			@Override
			public Object render(final ProjectDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectDTO> store, final Grid<ProjectDTO> grid) {

				return createProjectGridText(model, (String) model.get(property));
			}
		});

		// Org Unit
		final ColumnConfig orgUnitColumn = new ColumnConfig(ProjectDTO.ORG_UNIT_NAME, I18N.CONSTANTS.orgunit(), 150);
		orgUnitColumn.setRenderer(new GridCellRenderer<ProjectDTO>() {

			@Override
			public Object render(final ProjectDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectDTO> store, final Grid<ProjectDTO> grid) {

				return createProjectGridText(model, (String) model.get(property));
			}
		});

		// Ratio budget
		final ColumnConfig spentBudgetColumn = new ColumnConfig(ProjectDTO.SPEND_BUDGET, I18N.CONSTANTS.projectSpendBudget(), 100);
		spentBudgetColumn.setRenderer(new GridCellRenderer<ProjectDTO>() {

			@Override
			public Object render(final ProjectDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectDTO> store, final Grid<ProjectDTO> grid) {

				final String ratioDividendLabel;
				if (model.getRatioDividendType() != null) {
					ratioDividendLabel = BudgetSubFieldType.getName(model.getRatioDividendType());
				} else {
					ratioDividendLabel = model.getRatioDividendLabel();
				}

				final String ratioDivisorLabel;
				if (model.getRatioDivisorType() != null) {
					ratioDivisorLabel = BudgetSubFieldType.getName(model.getRatioDivisorType());
				} else {
					ratioDivisorLabel = model.getRatioDivisorLabel();
				}

				final String titleRatioLabel = '(' + ratioDividendLabel + '/' + ratioDivisorLabel + ')';

				if (model.getRatioDividendValue() != null && model.getRatioDivisorValue() != null) {
					return new RatioBar(NumberUtils.ratio(model.getRatioDividendValue(), model.getRatioDivisorValue()), titleRatioLabel);

				} else {
					return new RatioBar(0.0);
				}
			}
		});

		// Planned budget
		final ColumnConfig plannedBudgetColumn = new ColumnConfig(ProjectDTO.PLANNED_BUDGET, I18N.CONSTANTS.projectPlannedBudget(), 75);
		plannedBudgetColumn.setHidden(true);

		// Spend budget
		final ColumnConfig spendBudgetColumn = new ColumnConfig(ProjectDTO.SPEND_BUDGET, I18N.CONSTANTS.projectSpendBudget(), 75);
		spendBudgetColumn.setHidden(true);

		// Received budget
		final ColumnConfig receivedBudgetColumn = new ColumnConfig(ProjectDTO.RECEIVED_BUDGET, I18N.CONSTANTS.projectReceivedBudget(), 75);
		receivedBudgetColumn.setHidden(true);

		// Time
		final ColumnConfig timeColumn = new ColumnConfig(TIME_COLUMN, I18N.CONSTANTS.projectTime(), 100);
		timeColumn.setRenderer(new GridCellRenderer<ProjectDTO>() {

			@Override
			public Object render(final ProjectDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectDTO> store, final Grid<ProjectDTO> grid) {

				if (!model.isClosed()) {
					return new RatioBar(model.getElapsedTime());

				} else {
					return new Label(I18N.CONSTANTS.projectClosedLabel());
				}
			}
		});

		// Start date
		final ColumnConfig startDateColumn = new ColumnConfig(ProjectDTO.START_DATE, I18N.CONSTANTS.projectStartDate(), 75);
		startDateColumn.setHidden(true);
		startDateColumn.setDateTimeFormat(format);
		startDateColumn.setRenderer(new GridCellRenderer<ProjectDTO>() {

			@Override
			public Object render(final ProjectDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectDTO> store, final Grid<ProjectDTO> grid) {

				final Date d = (Date) model.get(property);
				return createProjectGridText(model, d != null ? format.format(d) : "");
			}
		});

		// End date
		final ColumnConfig endDateColumn = new ColumnConfig(ProjectDTO.END_DATE, I18N.CONSTANTS.projectEndDate(), 75);
		endDateColumn.setDateTimeFormat(format);
		endDateColumn.setHidden(true);
		endDateColumn.setRenderer(new GridCellRenderer<ProjectDTO>() {

			@Override
			public Object render(final ProjectDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectDTO> store, final Grid<ProjectDTO> grid) {

				final Date d = (Date) model.get(property);
				return createProjectGridText(model, d != null ? format.format(d) : "");
			}
		});

		// Close date
		final ColumnConfig closeDateColumn = new ColumnConfig(ProjectDTO.CLOSE_DATE, I18N.CONSTANTS.projectClosedDate(), 75);
		closeDateColumn.setDateTimeFormat(format);
		closeDateColumn.setHidden(true);
		closeDateColumn.setRenderer(new GridCellRenderer<ProjectDTO>() {

			@Override
			public Object render(final ProjectDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectDTO> store, final Grid<ProjectDTO> grid) {

				final Date d = (Date) model.get(property);
				return createProjectGridText(model, d != null ? format.format(d) : "");
			}
		});

		// Activity
		final ColumnConfig activityColumn = new ColumnConfig(ProjectDTO.ACTIVITY_ADVANCEMENT, I18N.CONSTANTS.logFrameActivity(), 100);
		activityColumn.setSortable(false);
		activityColumn.setRenderer(new GridCellRenderer<ProjectDTO>() {

			@Override
			public Object render(final ProjectDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectDTO> store, final Grid<ProjectDTO> grid) {

				return new RatioBar(model.getActivityAdvancement() != null ? model.getActivityAdvancement() : 0);
			}
		});

		// Category
		final ColumnConfig categoryColumn = new ColumnConfig(ProjectDTO.CATEGORY_ELEMENTS, I18N.CONSTANTS.category(), 150);
		categoryColumn.setSortable(false);
		categoryColumn.setRenderer(new GridCellRenderer<ProjectDTO>() {

			@Override
			public Object render(final ProjectDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProjectDTO> store, final Grid<ProjectDTO> grid) {

				final Set<CategoryElementDTO> elements = model.getCategoryElements();
				final LayoutContainer panel = new LayoutContainer();
				panel.setLayout(new FlowLayout());
				final FlowData data = new FlowData(new Margins(0, 5, 0, 0));

				if (elements != null) {
					for (final CategoryElementDTO element : elements) {
						panel.add(CategoryIconProvider.getIcon(element), data);
					}
				}

				return panel;
			}
		});

		return new ColumnModel(Arrays.asList(starredIconColumn, codeColumn, titleColumn, currentPhaseName, orgUnitColumn, spentBudgetColumn, plannedBudgetColumn,
			spendBudgetColumn, receivedBudgetColumn, startDateColumn, endDateColumn, closeDateColumn, timeColumn, activityColumn, categoryColumn));
	}

	/**
	 * Creates a text widget for the projects grid.
	 * 
	 * @param model
	 *          The project model.
	 * @param content
	 *          The text content.
	 * @return The built widget.
	 */
	private static Widget createProjectGridText(final ProjectDTO model, final String content) {

		final Html label = new Html(content);

		if (!model.isLeaf()) {
			label.addStyleName(STYLE_PROJECT_GRID_NODE);

		} else {
			label.addStyleName(STYLE_PROJECT_GRID_LEAF);
		}

		return label;
	}

	/**
	 * Grid filters for projects' TreeGrid.
	 */
	private GridFilters createGridFilters() {

		gridFilters.setLocal(true);

		// Data index of each filter should be identical with column id in ColumnConfig of TreeGrid.

		// Common filters
		gridFilters.addFilter(new StringFilter(ProjectDTO.NAME));
		gridFilters.addFilter(new StringFilter(ProjectDTO.FULL_NAME));
		gridFilters.addFilter(new StringFilter(ProjectDTO.CURRENT_PHASE_NAME));
		gridFilters.addFilter(new StringFilter(ProjectDTO.ORG_UNIT_NAME));
		gridFilters.addFilter(new NumericFilter(ProjectDTO.SPEND_BUDGET));
		gridFilters.addFilter(new NumericFilter(ProjectDTO.RECEIVED_BUDGET));
		gridFilters.addFilter(new NumericFilter(ProjectDTO.PLANNED_BUDGET));
		gridFilters.addFilter(new DateFilter(ProjectDTO.START_DATE));
		gridFilters.addFilter(new DateFilter(ProjectDTO.END_DATE));
		gridFilters.addFilter(new DateFilter(ProjectDTO.CLOSE_DATE));

		// Custom filter for category elements' list
		final ListFilter categoryListFilter = new ListFilter(ProjectDTO.CATEGORY_ELEMENTS, new ListStore<ModelData>()) {

			@SuppressWarnings("unchecked")
			@Override
			public boolean validateModel(final ModelData model) {

				final Set<CategoryElementDTO> elementsPerProject = getModelValue(model);
				final List<String> filterLabels = (ArrayList<String>) getValue();

				if (elementsPerProject != null) {
					String label = null;
					for (final CategoryElementDTO element : elementsPerProject) {
						label = element.getLabel() + " (" + element.getParentCategoryDTO().getLabel() + ")";
						if (filterLabels.contains(label)) {
							return true;
						}
					}
				}

				return filterLabels.size() == 0;
			}
		};

		categoryListFilter.setDisplayProperty(CATEGORY_FILTER);
		gridFilters.addFilter(categoryListFilter);
		gridFilters.addFilter(new ClosedFilter(TIME_COLUMN));

		return gridFilters;
	}

}
