package org.sigmah.client.ui.view.project.dashboard;

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


import java.util.Date;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.project.dashboard.PhasesPresenter;
import org.sigmah.client.ui.presenter.project.dashboard.ProjectDashboardPresenter;
import org.sigmah.client.ui.presenter.project.dashboard.ProjectDashboardPresenter.PresenterHandler;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.FlexibleGrid;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.ProjectFundingDTO.LinkedProjectType;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * {@link ProjectDashboardPresenter} view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectDashboardView extends AbstractView implements ProjectDashboardPresenter.View {

	// CSS style names.
	private static final String STYLE_TOOLBAR_TITLE = "toolbar-title";

	@Inject
	private Provider<PhasesPresenter> phasesPresenterProvider;
	private PhasesPresenter phasesPresenter;

	private Grid<ReminderDTO> remindersGrid;
	private ToolBar remindersToolbar;
	private Button reminderAddButton;

	private Grid<MonitoredPointDTO> monitoredPointsGrid;
	private ToolBar monitoredPointsToolbar;
	private Button monitoredPointsAddButton;

	private ContentPanel fundingProjectsPanel;
	private Grid<ProjectFundingDTO> fundingProjectsGrid;
	private ToolBar fundingProjectsToolbar;
	private Button fundingProjectsSelectButton;
	private Button fundingProjectsCreateButton;

	private ContentPanel fundedProjectsPanel;
	private Grid<ProjectFundingDTO> fundedProjectsGrid;
	private ToolBar fundedProjectsToolbar;
	private Button fundedProjectsSelectButton;
	private Button fundedProjectsCreateButton;
	
	private LinkedProjectsColumnsProvider fundingProjectsColumnsProvider;

	/**
	 * Specific presenter's handlers.
	 */
	private PresenterHandler handler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// --
		// Center panel.
		// --

		add(createProjectDashboardPanel(), Layouts.borderLayoutData(LayoutRegion.CENTER, Margin.LEFT));

		// --
		// East panel.
		// --

		final ContentPanel remindersPanel = Panels.content(I18N.CONSTANTS.reminders(), false, Layouts.vBoxLayout());
		remindersPanel.add(createRemindersPanel(), Layouts.vBoxData());
		remindersPanel.add(createMonitoredPointsPanel(), Layouts.vBoxData());

		final BorderLayoutData eastData = Layouts.borderLayoutData(LayoutRegion.WEST, Layouts.LEFT_COLUMN_WIDTH);
		eastData.setCollapsible(true);
		add(remindersPanel, eastData);

		// --
		// South panel.
		// --

		final BorderLayoutData southData = Layouts.borderLayoutData(LayoutRegion.SOUTH, Layouts.SOUTH_PANEL_HEIGHT, Margin.TOP);
		southData.setCollapsible(true);
		add(createLinkedProjectsPanel(), southData);
	}

	/**
	 * Returns the presenter specific handlers implementation.
	 * 
	 * @return The presenter specific handlers implementation.
	 */
	final PresenterHandler getPresenterHandler() {
		return handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPresenterHandler(final PresenterHandler handler) {
		this.handler = handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Grid<ReminderDTO> getRemindersGrid() {
		return remindersGrid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Grid<MonitoredPointDTO> getMonitoredPointsGrid() {
		return monitoredPointsGrid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getReminderAddButton() {
		return reminderAddButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getMonitoredPointsAddButton() {
		return monitoredPointsAddButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateRemindersToolbars(final boolean canEditReminders, final boolean canEditMonitoredPoints) {

		reminderAddButton.setEnabled(canEditReminders);

		if (remindersToolbar.indexOf(reminderAddButton) != -1) {
			remindersToolbar.remove(reminderAddButton);
		}
		if (canEditReminders) {
			remindersToolbar.insert(reminderAddButton, 0);
		}

		monitoredPointsAddButton.setEnabled(canEditMonitoredPoints);

		if (monitoredPointsToolbar.indexOf(monitoredPointsAddButton) != -1) {
			monitoredPointsToolbar.remove(monitoredPointsAddButton);
		}
		if (canEditMonitoredPoints) {
			monitoredPointsToolbar.insert(monitoredPointsAddButton, 0);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateLinkedProjectsToolbars(final boolean canRelateProject, final boolean canCreateProject) {

		fundingProjectsToolbar.removeAll();
		fundedProjectsToolbar.removeAll();

		final Label fundingTitle = new Label(I18N.CONSTANTS.projectFinancialProjectsHeader());
		fundingTitle.addStyleName(STYLE_TOOLBAR_TITLE);

		final Label fundedTitle = new Label(I18N.CONSTANTS.projectLocalPartnerProjectsHeader());
		fundedTitle.addStyleName(STYLE_TOOLBAR_TITLE);

		fundingProjectsToolbar.add(fundingTitle);
		fundedProjectsToolbar.add(fundedTitle);

		if (canRelateProject) {
			fundingProjectsToolbar.add(new SeparatorToolItem());
			fundedProjectsToolbar.add(new SeparatorToolItem());
			fundingProjectsToolbar.add(fundingProjectsSelectButton);
			fundedProjectsToolbar.add(fundedProjectsSelectButton);
		}

		if (canCreateProject) {
			fundingProjectsToolbar.add(new SeparatorToolItem());
			fundedProjectsToolbar.add(new SeparatorToolItem());
			fundingProjectsToolbar.add(fundingProjectsCreateButton);
			fundedProjectsToolbar.add(fundedProjectsCreateButton);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Grid<ProjectFundingDTO> getFundingProjectsGrid() {
		return fundingProjectsGrid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getFundingProjectSelectButton() {
		return fundingProjectsSelectButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getFundingProjectCreateButton() {
		return fundingProjectsCreateButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Grid<ProjectFundingDTO> getFundedProjectsGrid() {
		return fundedProjectsGrid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getFundedProjectSelectButton() {
		return fundedProjectsSelectButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getFundedProjectCreateButton() {
		return fundedProjectsCreateButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PhasesPresenter getPhasesWidget() {
		return phasesPresenter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LinkedProjectsColumnsProvider getFundingProjectsColumnsProvider() {
		return fundingProjectsColumnsProvider;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layoutView() {
		layoutContainer.layout();
	}

	/**
	 * Creates the panel which displays the reminders.
	 * 
	 * @return The panel which displays the reminders.
	 */
	private Component createRemindersPanel() {

		// --
		// Store filters.
		// --

		final StoreFilter<ReminderDTO> notCompletedFilter = new StoreFilter<ReminderDTO>() {

			@Override
			public boolean select(Store<ReminderDTO> store, ReminderDTO parent, ReminderDTO item, String property) {
				return !item.isCompleted();
			}
		};

		final StoreFilter<ReminderDTO> completedFilter = new StoreFilter<ReminderDTO>() {

			@Override
			public boolean select(Store<ReminderDTO> store, ReminderDTO parent, ReminderDTO item, String property) {
				return item.isCompleted();
			}
		};

		final StoreFilter<ReminderDTO> exceededFilter = new StoreFilter<ReminderDTO>() {

			@Override
			public boolean select(Store<ReminderDTO> store, ReminderDTO parent, ReminderDTO item, String property) {
				return !item.isCompleted() && DateUtils.DAY_COMPARATOR.compare(new Date(), item.getExpectedDate()) > 0;
			}
		};

		// --
		// Store.
		// --

		final ListStore<ReminderDTO> remindersStore = new ListStore<ReminderDTO>();

		// --
		// Grid.
		// --

		remindersGrid = new Grid<ReminderDTO>(remindersStore, new RemindersColumnsProvider(this).getRemindersColumnModel());
		remindersGrid.getView().setForceFit(true);
		remindersGrid.setBorders(false);
		remindersGrid.setAutoExpandColumn(ReminderDTO.LABEL);

		// --
		// Filter menu.
		// --

		final FilterSelectionListener<ReminderDTO> filterListener = new FilterSelectionListener<ReminderDTO>(remindersStore);

		final Menu filterMenu = new Menu();
		filterMenu.add(buildFilterMenu(I18N.CONSTANTS.monitoredPointAll(), null, filterListener, null));
		filterMenu.add(new SeparatorMenuItem());
		filterMenu.add(buildFilterMenu(I18N.CONSTANTS.monitoredPointCompleted(), IconImageBundle.ICONS.closedReminder(), filterListener, completedFilter));
		filterMenu.add(buildFilterMenu(I18N.CONSTANTS.monitoredPointUncompleted(), IconImageBundle.ICONS.openedReminder(), filterListener, notCompletedFilter));
		filterMenu.add(buildFilterMenu(I18N.CONSTANTS.monitoredPointExceeded(), IconImageBundle.ICONS.overdueReminder(), filterListener, exceededFilter));

		// Fires manually the first filter (no filter).
		filterListener.filter((MenuItem) filterMenu.getItem(0), null);

		// Filter button.
		final Button filterButton = new Button(I18N.CONSTANTS.filter(), IconImageBundle.ICONS.filter());
		filterButton.setMenu(filterMenu);

		// --
		// Toolbar.
		// --

		reminderAddButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());

		remindersToolbar = new ToolBar();
		remindersToolbar.setAlignment(HorizontalAlignment.LEFT);
		remindersToolbar.add(filterButton);

		// --
		// Panel.
		// --

		final ContentPanel panel = Panels.content(I18N.CONSTANTS.reminderPoints());

		panel.setTopComponent(remindersToolbar);
		panel.add(remindersGrid);

		final Menu menuContext = new Menu();
		menuContext.add(new MenuItem(I18N.CONSTANTS.historyShow(), new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(final MenuEvent ce) {

				final ReminderDTO selectedReminder = remindersGrid.getSelectionModel() != null ? remindersGrid.getSelectionModel().getSelectedItem() : null;

				if (selectedReminder == null) {
					// A reminder has to be selected to show its history.
					return;
				}

				handler.onShowHistoryEvent(selectedReminder);
			}
		}));

		remindersGrid.setContextMenu(menuContext);

		return panel;
	}

	/**
	 * Creates the panel which displays the monitored points.
	 * 
	 * @return The panel which displays the monitored points.
	 */
	private Component createMonitoredPointsPanel() {

		// --
		// Store filters.
		// --

		final StoreFilter<MonitoredPointDTO> notCompletedFilter = new StoreFilter<MonitoredPointDTO>() {

			@Override
			public boolean select(Store<MonitoredPointDTO> store, MonitoredPointDTO parent, MonitoredPointDTO item, String property) {
				return !item.isCompleted();
			}
		};

		final StoreFilter<MonitoredPointDTO> completedFilter = new StoreFilter<MonitoredPointDTO>() {

			@Override
			public boolean select(Store<MonitoredPointDTO> store, MonitoredPointDTO parent, MonitoredPointDTO item, String property) {
				return item.isCompleted();
			}
		};

		final StoreFilter<MonitoredPointDTO> exceededFilter = new StoreFilter<MonitoredPointDTO>() {

			@Override
			public boolean select(Store<MonitoredPointDTO> store, MonitoredPointDTO parent, MonitoredPointDTO item, String property) {
				return !item.isCompleted() && DateUtils.DAY_COMPARATOR.compare(new Date(), item.getExpectedDate()) > 0;
			}
		};

		// --
		// Store.
		// --

		final ListStore<MonitoredPointDTO> monitoredPointsStore = new ListStore<MonitoredPointDTO>();

		// --
		// Grid.
		// --

		monitoredPointsGrid = new Grid<MonitoredPointDTO>(monitoredPointsStore, new RemindersColumnsProvider(this).getMonitoredPointsColumnModel());
		monitoredPointsGrid.getView().setForceFit(true);
		monitoredPointsGrid.setBorders(false);
		monitoredPointsGrid.setAutoExpandColumn(MonitoredPointDTO.LABEL);

		// --
		// Filter menu.
		// --

		final FilterSelectionListener<MonitoredPointDTO> filterListener = new FilterSelectionListener<MonitoredPointDTO>(monitoredPointsStore);

		final Menu filterMenu = new Menu();
		filterMenu.add(buildFilterMenu(I18N.CONSTANTS.monitoredPointAll(), null, filterListener, null));
		filterMenu.add(new SeparatorMenuItem());
		filterMenu.add(buildFilterMenu(I18N.CONSTANTS.monitoredPointCompleted(), IconImageBundle.ICONS.closedReminder(), filterListener, completedFilter));
		filterMenu.add(buildFilterMenu(I18N.CONSTANTS.monitoredPointUncompleted(), IconImageBundle.ICONS.openedReminder(), filterListener, notCompletedFilter));
		filterMenu.add(buildFilterMenu(I18N.CONSTANTS.monitoredPointExceeded(), IconImageBundle.ICONS.overdueReminder(), filterListener, exceededFilter));

		// Fires manually the first filter (no filter).
		filterListener.filter((MenuItem) filterMenu.getItem(0), null);

		// Filter button.
		final Button filterButton = new Button(I18N.CONSTANTS.filter(), IconImageBundle.ICONS.filter());
		filterButton.setMenu(filterMenu);

		// --
		// Toolbar.
		// --

		monitoredPointsAddButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());

		monitoredPointsToolbar = new ToolBar();
		monitoredPointsToolbar.setAlignment(HorizontalAlignment.LEFT);
		monitoredPointsToolbar.add(filterButton);

		// --
		// Panel.
		// --

		final ContentPanel panel = Panels.content(I18N.CONSTANTS.monitoredPoints());

		panel.setTopComponent(monitoredPointsToolbar);
		panel.add(monitoredPointsGrid);

		final Menu menuContext = new Menu();
		menuContext.add(new MenuItem(I18N.CONSTANTS.historyShow(), new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(final MenuEvent ce) {

				final MonitoredPointDTO selectedPoint =
						monitoredPointsGrid.getSelectionModel() != null ? monitoredPointsGrid.getSelectionModel().getSelectedItem() : null;

				if (selectedPoint == null) {
					// A monitored point has to be selected to show its history.
					return;
				}

				handler.onShowHistoryEvent(selectedPoint);
			}
		}));

		monitoredPointsGrid.setContextMenu(menuContext);

		return panel;
	}

	/**
	 * Creates linked projects panel.
	 * 
	 * @return The component.
	 */
	private Component createLinkedProjectsPanel() {

		final ContentPanel panel = Panels.content(I18N.CONSTANTS.projectLinkedProjects(), false, Layouts.hBoxLayout());

		final String subPanelsWidth = "50%";

		// --
		// Funding projects panel.
		// --

		fundingProjectsPanel = Panels.content(null);
		fundingProjectsPanel.setWidth(subPanelsWidth);

		fundingProjectsSelectButton = new Button(I18N.CONSTANTS.createProjectTypeFundingSelect(), IconImageBundle.ICONS.select());
		fundingProjectsSelectButton.setTitle(I18N.CONSTANTS.createProjectTypeFundingSelectDetails());
		fundingProjectsCreateButton = new Button(I18N.CONSTANTS.createProjectTypeFundingCreate(), IconImageBundle.ICONS.add());
		fundingProjectsCreateButton.setTitle(I18N.CONSTANTS.createProjectTypeFundingCreateDetails());

		// --
		// Funding projects store + grid.
		// --

		// The grid sorter.
		final StoreSorter<ProjectFundingDTO> storeSorter = new StoreSorter<ProjectFundingDTO>() {

			@Override
			public int compare(Store<ProjectFundingDTO> store, ProjectFundingDTO m1, ProjectFundingDTO m2, String property) {

				if (ProjectDTO.NAME.equals(property)) {
					return m1.getFunding().getName().compareTo(m2.getFunding().getName());

				} else if (ProjectDTO.FULL_NAME.equals(property)) {
					return m1.getFunding().getFullName().compareTo(m2.getFunding().getFullName());

				} else {
					return super.compare(store, m1, m2, property);
				}
			}
		};

		// Builds the grid.
		final ListStore<ProjectFundingDTO> fundingProjectsStore = new ListStore<ProjectFundingDTO>();
		fundingProjectsStore.setStoreSorter(storeSorter);

		this.fundingProjectsColumnsProvider = new LinkedProjectsColumnsProvider(this, LinkedProjectType.FUNDING_PROJECT);
		fundingProjectsGrid =
				new FlexibleGrid<ProjectFundingDTO>(fundingProjectsStore, null, 2,
					fundingProjectsColumnsProvider.getLinkedProjectsColumnModel());
		fundingProjectsGrid.setAutoExpandColumn(ProjectDTO.NAME);

		fundingProjectsToolbar = new ToolBar();

		fundingProjectsPanel.setTopComponent(fundingProjectsToolbar);
		fundingProjectsPanel.add(fundingProjectsGrid);

		// --
		// Funded projects panel.
		// --

		fundedProjectsPanel = Panels.content(null);
		fundedProjectsPanel.setWidth(subPanelsWidth);

		fundedProjectsSelectButton = new Button(I18N.CONSTANTS.createProjectTypePartnerSelect(), IconImageBundle.ICONS.select());
		fundedProjectsSelectButton.setTitle(I18N.CONSTANTS.createProjectTypePartnerSelectDetails());
		fundedProjectsCreateButton = new Button(I18N.CONSTANTS.createProjectTypePartnerCreate(), IconImageBundle.ICONS.add());
		fundedProjectsCreateButton.setTitle(I18N.CONSTANTS.createProjectTypePartnerCreateDetails());

		// --
		// Funded projects store + grid.
		// --

		// Builds the grid.
		final ListStore<ProjectFundingDTO> fundedProjectsStore = new ListStore<ProjectFundingDTO>();
		fundedProjectsStore.setStoreSorter(storeSorter);

		fundedProjectsGrid =
				new FlexibleGrid<ProjectFundingDTO>(fundedProjectsStore, null, 2,
					new LinkedProjectsColumnsProvider(this, LinkedProjectType.FUNDED_PROJECT).getLinkedProjectsColumnModel());
		fundedProjectsGrid.setAutoExpandColumn(ProjectDTO.NAME);

		fundedProjectsToolbar = new ToolBar();

		fundedProjectsPanel.setTopComponent(fundedProjectsToolbar);
		fundedProjectsPanel.add(fundedProjectsGrid);

		// --
		// Building panel.
		// --

		updateLinkedProjectsToolbars(false, false);

		panel.add(fundingProjectsPanel, Layouts.hBoxData(Margin.HALF_RIGHT));
		panel.add(fundedProjectsPanel, Layouts.hBoxData(Margin.HALF_LEFT));

		return panel;
	}

	/**
	 * Creates the project dashboard panel itself.
	 * 
	 * @return The component.
	 */
	private Component createProjectDashboardPanel() {

		phasesPresenter = phasesPresenterProvider.get();
		phasesPresenter.initialize();

		return (Component) phasesPresenter.getView().asWidget();
	}

	/**
	 * Builds a new filter {@link MenuItem}.
	 * 
	 * @param label
	 *          The menu label.
	 * @param icon
	 *          The menu icon, may be {@code null}.
	 * @param filterListener
	 *          The filter listener triggered when menu item is selected.
	 * @param storeFilter
	 *          The store filter instance, may be {@code null}.
	 * @return The menu item component.
	 */
	private static <E extends AbstractModelDataEntityDTO<?>> MenuItem buildFilterMenu(final String label, final AbstractImagePrototype icon,
			final FilterSelectionListener<E> filterListener, final StoreFilter<E> storeFilter) {

		final MenuItem filterMenu = new MenuItem(label, icon);

		filterMenu.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(final MenuEvent ce) {
				if (filterListener != null) {
					filterListener.filter(filterMenu, storeFilter);
				}
			}
		});

		return filterMenu;
	}

}
