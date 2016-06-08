package org.sigmah.client.ui.view;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.DashboardPresenter;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.HasTreeGrid;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.orgunit.OrgUnitTreeGrid;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.filters.DateFilter;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.StringFilter;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
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
public class DashboardView extends AbstractView implements DashboardPresenter.View {

	/**
	 * Reminders expected date label style name.
	 */
	private static final String EXPECTED_DATE_LABEL_STYLE = "points-date-exceeded";

	@Inject
	private Provider<ProjectsListWidget> projectsListWidgetProvider;

	private ContentPanel remindersPanel;
	private ListStore<ReminderDTO> remindersStore;

	private ContentPanel monitoredPointsPanel;
	private ListStore<MonitoredPointDTO> monitoredPointsStore;

	private LayoutContainer menuButtonsContainer;

	private ContentPanel orgUnitsPanel;
	private OrgUnitTreeGrid orgUnitsTreeGrid;

	private ProjectsListWidget projectsListWidget;

    private static ReminderOrMonitoredPointHandler handler;
    
    final ReminderOrMonitoredPointHandler getReminderOrMonitoredPointHandler() {
		return handler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReminderOrMonitoredPointHandler(final ReminderOrMonitoredPointHandler handler) {
		this.handler = handler;
	}
    
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// --
		// Left panel (Reminders + MonitoredPoints + Buttons).
		// --
		final LayoutContainer leftContainer = Layouts.vBox();

		leftContainer.add(createRemindersPanel(), Layouts.vBoxData(Margin.BOTTOM));
		leftContainer.add(createMonitoredPointsPanel(), Layouts.vBoxData(Margin.BOTTOM));
		leftContainer.add(createMenuButtonsPanel(), Layouts.vBoxData());

		add(leftContainer, Layouts.borderLayoutData(LayoutRegion.WEST, Layouts.LEFT_COLUMN_WIDTH));

		// --
		// Center panel (OrgUnits + Projects).
		// --
		final LayoutContainer centerContainer = Layouts.vBox();

		centerContainer.add(createOrgUnitsPanel(), Layouts.vBoxData(1.0, Margin.BOTTOM, Margin.LEFT));
		centerContainer.add(createProjectsPanel(), Layouts.vBoxData(2.0, Margin.LEFT));

		add(centerContainer);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getRemindersPanel() {
		return remindersPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListStore<ReminderDTO> getRemindersStore() {
		return remindersStore;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getMonitoredPointsPanel() {
		return monitoredPointsPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListStore<MonitoredPointDTO> getMonitoredPointsStore() {
		return monitoredPointsStore;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearMenuButtons() {
		menuButtonsContainer.removeAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMenuButton(final String buttonText, final AbstractImagePrototype buttonIcon, final Listener<ButtonEvent> clickHandler) {

		if (ClientUtils.isBlank(buttonText)) {
			throw new IllegalArgumentException("Invalid button text.");
		}

		final Button button = Forms.button(buttonText, buttonIcon);

		if (clickHandler != null) {
			button.addListener(Events.OnClick, clickHandler);
		}

		menuButtonsContainer.add(button, Layouts.vBoxData());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layoutButtons() {
		menuButtonsContainer.layout();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void layoutViews() {
		projectsListWidget.getView().syncSize();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasTreeGrid<OrgUnitDTO> getOrgUnitsTreeGrid() {
		return orgUnitsTreeGrid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOrgUnitsPanelTitle(final String title) {
		orgUnitsPanel.setHeadingText(title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectsListWidget getProjectsList() {
		return projectsListWidget;
	}

	// -------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------------------
	/**
	 * Creates the reminders component.
	 * 
	 * @return The reminders component widget.
	 */
	private Component createRemindersPanel() {

		remindersStore = new ListStore<ReminderDTO>();
		final Grid<ReminderDTO> reminderGrid = new Grid<ReminderDTO>(remindersStore, new ColumnModel(createRemindersGridColumns()));
		reminderGrid.getView().setForceFit(true);
		reminderGrid.setAutoExpandColumn(ReminderDTO.LABEL);

		remindersPanel = Panels.content(I18N.CONSTANTS.reminderPoints());
		remindersPanel.add(reminderGrid);

		return remindersPanel;
	}

	/**
	 * Creates the monitored points component.
	 * 
	 * @return The monitored points component widget.
	 */
	private Component createMonitoredPointsPanel() {

		monitoredPointsStore = new ListStore<MonitoredPointDTO>();
		final Grid<MonitoredPointDTO> monitoredPointsGrid = new Grid<MonitoredPointDTO>(monitoredPointsStore, new ColumnModel(createMonitoredPointsGridColumns()));
		monitoredPointsGrid.getView().setForceFit(true);
		monitoredPointsGrid.setAutoExpandColumn(MonitoredPointDTO.LABEL);

		final GridFilters filters = new GridFilters();
		filters.setLocal(true);
		filters.addFilter(new StringFilter(MonitoredPointDTO.LABEL));
		filters.addFilter(new DateFilter(MonitoredPointDTO.EXPECTED_DATE));
		monitoredPointsGrid.addPlugin(filters);

		monitoredPointsPanel = Panels.content(I18N.CONSTANTS.monitoredPoints());
		monitoredPointsPanel.add(monitoredPointsGrid);

		return monitoredPointsPanel;
	}

	/**
	 * Creates the menu buttons component.
	 * 
	 * @return The menu buttons component widget.
	 */
	private Component createMenuButtonsPanel() {

		final ContentPanel menuButtonsPanel = Panels.content(I18N.CONSTANTS.menu());

		menuButtonsContainer = Layouts.vBox();
		menuButtonsPanel.add(menuButtonsContainer);

		return menuButtonsPanel;
	}

	/**
	 * Creates the OrgUnits component.
	 * 
	 * @return The OrgUnits component widget.
	 */
	private Component createOrgUnitsPanel() {

		orgUnitsPanel = Panels.content(I18N.CONSTANTS.orgunitTree());
		orgUnitsTreeGrid = new OrgUnitTreeGrid(false);

		orgUnitsPanel.setTopComponent(orgUnitsTreeGrid.getToolbar());
		orgUnitsPanel.add(orgUnitsTreeGrid.getTreeGrid());

		return orgUnitsPanel;
	}

	/**
	 * Creates the projects component.
	 * 
	 * @return The projects component widget.
	 */
	private Widget createProjectsPanel() {

		projectsListWidget = projectsListWidgetProvider.get();
		projectsListWidget.initialize();

		return projectsListWidget.getView().asWidget();
	}

	/**
	 * Builds the reminders grid columns configuration.
	 * 
	 * @return The reminders grid columns list.
	 */
	private static List<ColumnConfig> createRemindersGridColumns() {

		final DateTimeFormat format = DateUtils.DATE_SHORT;
		final Date now = new Date();

		// Icon column.
		final ColumnConfig iconColumn = new ColumnConfig();
		iconColumn.setId("icon");
		iconColumn.setHeaderHtml("");
		iconColumn.setWidth(16);
		iconColumn.setResizable(false);
		iconColumn.setRenderer(new GridCellRenderer<ReminderDTO>() {

			@Override
			public Object render(final ReminderDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ReminderDTO> store, final Grid<ReminderDTO> grid) {

				if (DateUtils.DAY_COMPARATOR.compare(now, model.getExpectedDate()) > 0) {
					return IconImageBundle.ICONS.overdueReminder().createImage();
				} else {
					return IconImageBundle.ICONS.openedReminder().createImage();
				}
			}
		});

        // Label column.
        final ColumnConfig labelColumn = new ColumnConfig();
        labelColumn.setId(ReminderDTO.LABEL);
        labelColumn.setHeaderHtml(I18N.CONSTANTS.monitoredPointLabel());
        labelColumn.setWidth(100);
        
        // Ajout du HREF
        labelColumn.setRenderer(new GridCellRenderer<ReminderDTO>() {

			@Override
			public Object render(final ReminderDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ReminderDTO> store,
					Grid<ReminderDTO> grid) {

				final com.google.gwt.user.client.ui.Label label = new com.google.gwt.user.client.ui.Label((String) model.get(property));
                
				label.addStyleName("hyperlink-label");
				label.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
                        handler.onLabelClickEvent(model.getProjectId());
					}
				});
                
                label.setTitle(I18N.CONSTANTS.projectLabelWithDots() + ' ' + model.getProjectCode() + " - " + model.getProjectName());

				return label;
			}
		});

		// Expected date column.
		final ColumnConfig expectedDateColumn = new ColumnConfig();
		expectedDateColumn.setId(ReminderDTO.EXPECTED_DATE);
		expectedDateColumn.setHeaderHtml(I18N.CONSTANTS.monitoredPointExpectedDate());
		expectedDateColumn.setWidth(60);
		expectedDateColumn.setDateTimeFormat(format);
		expectedDateColumn.setRenderer(new GridCellRenderer<ReminderDTO>() {

			@Override
			public Object render(final ReminderDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ReminderDTO> store, final Grid<ReminderDTO> grid) {

				final Label label = new Label(format.format(model.getExpectedDate()));
				if (!model.isCompleted() && DateUtils.DAY_COMPARATOR.compare(now, model.getExpectedDate()) > 0) {
					label.addStyleName(EXPECTED_DATE_LABEL_STYLE);
				}
				return label;
			}
		});

		return Arrays.asList(new ColumnConfig[] {
				iconColumn,
				labelColumn,
				expectedDateColumn
		});
	}

	/**
	 * Builds the monitored points grid columns configuration.
	 * 
	 * @return The monitored points grid columns list.
	 */
	private static List<ColumnConfig> createMonitoredPointsGridColumns() {

		final DateTimeFormat format = DateUtils.DATE_SHORT;
		final Date now = new Date();

		// Icon
		final ColumnConfig iconColumn = new ColumnConfig();
		iconColumn.setId("icon");
		iconColumn.setHeaderHtml("");
		iconColumn.setWidth(16);
		iconColumn.setResizable(false);
		iconColumn.setRenderer(new GridCellRenderer<MonitoredPointDTO>() {

			@Override
			public Object render(final MonitoredPointDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<MonitoredPointDTO> store, final Grid<MonitoredPointDTO> grid) {

				if (DateUtils.DAY_COMPARATOR.compare(now, model.getExpectedDate()) > 0) {
					return IconImageBundle.ICONS.overduePoint().createImage();
				} else {
					return IconImageBundle.ICONS.openedPoint().createImage();
				}
			}
		});

		// Label.
		final ColumnConfig labelColumn = new ColumnConfig();
		labelColumn.setId(MonitoredPointDTO.LABEL);
		labelColumn.setHeaderHtml(I18N.CONSTANTS.monitoredPointLabel());
		labelColumn.setWidth(100);

        labelColumn.setRenderer(new GridCellRenderer<MonitoredPointDTO>() {

			@Override
			public Object render(final MonitoredPointDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<MonitoredPointDTO> store,
					Grid<MonitoredPointDTO> grid) {

				final com.google.gwt.user.client.ui.Label label = new com.google.gwt.user.client.ui.Label((String) model.get(property));
				label.addStyleName("hyperlink-label");

				label.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
                        handler.onLabelClickEvent(model.getProjectId());
					}
				});

                label.setTitle(I18N.CONSTANTS.projectLabelWithDots() + ' ' + model.getProjectCode() + " - " + model.getProjectName());
                
				return label;
			}
		});

		// Expected date.
		final ColumnConfig expectedDateColumn = new ColumnConfig();
		expectedDateColumn.setId(MonitoredPointDTO.EXPECTED_DATE);
		expectedDateColumn.setHeaderHtml(I18N.CONSTANTS.monitoredPointExpectedDate());
		expectedDateColumn.setWidth(60);
		expectedDateColumn.setDateTimeFormat(format);
		expectedDateColumn.setRenderer(new GridCellRenderer<MonitoredPointDTO>() {

			@Override
			public Object render(final MonitoredPointDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<MonitoredPointDTO> store, final Grid<MonitoredPointDTO> grid) {

				final Label label = new Label(format.format(model.getExpectedDate()));
				if (!model.isCompleted() && DateUtils.DAY_COMPARATOR.compare(now, model.getExpectedDate()) > 0) {
					label.addStyleName(EXPECTED_DATE_LABEL_STYLE);
                    
				}
				return label;
			}
		});

		return Arrays.asList(new ColumnConfig[] {
																							iconColumn,
																							labelColumn,
																							expectedDateColumn
		});
	}

}
