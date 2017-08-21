package org.sigmah.client.ui.view.calendar;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Generamobhtl Public License as
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

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.calendar.CalendarPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.CalendarWidget;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.calendar.CalendarWrapper;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.selection.AbstractStoreSelectionModel;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.shared.DateTimeFormatInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import org.sigmah.client.ui.res.icon.IconImageBundle;

/**
 * Calendar widget presenter.
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr) (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class CalendarView extends AbstractView implements CalendarPresenter.View {

    private ContentPanel calendarView;

    private ListStore<CalendarWrapper> calendarsStore;
    private CheckBoxSelectionModel<CalendarWrapper> selectionModel;

    private ToolBar toolbar;
    private Button addEventButton;
    private Button todayButton;
    private Button weekButton;
    private Button monthButton;
    private Button previousButton;
    private Button nextButton;

    private Button reminderAddButton;
    private Button monitoredPointsAddButton;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {

        // --
        // Calendars left panel.
        // --
        final BorderLayoutData calendarListData = Layouts.borderLayoutData(LayoutRegion.WEST, Layouts.LEFT_COLUMN_WIDTH);
        calendarListData.setCollapsible(true);
        add(createCalendarsGridPanel(), calendarListData);

        // --
        // Calendar center widget.
        // --
        add(createCalendarsMainPanel(), Layouts.borderLayoutData(LayoutRegion.CENTER, Margin.LEFT));
    }

    /**
     * Creates the calendars panel + grid.
     *
     * @return The calendars panel.
     */
    private Component createCalendarsGridPanel() {

        // Calendars panel.
        final ContentPanel calendarsPanel = Panels.content(I18N.CONSTANTS.projectTabCalendar());

        // Calendars grid.
        final ColumnConfig calendarName = new ColumnConfig("name", I18N.CONSTANTS.name(), 180);
        final ColumnConfig calendarColor = new ColumnConfig("color", "", 20);
        final ColumnConfig calendarButton = new ColumnConfig("button", "", 40);

        calendarColor.setStyle("");
        calendarColor.setRenderer(new GridCellRenderer<CalendarWrapper>() {

            @Override
            public Object render(CalendarWrapper model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<CalendarWrapper> store,
                    Grid<CalendarWrapper> grid) {

                final SimplePanel panel = new SimplePanel();
                panel.setPixelSize(14, 14);
                //SquaresColor
                panel.getElement().getStyle().setMarginTop(3, Unit.PX);

                panel.setStyleName("calendar-fullday-event-" + model.getCalendar().getStyle());
                return panel;
            }
        });

        calendarButton.setStyle("");
        calendarButton.setRenderer(new GridCellRenderer<CalendarWrapper>() {

            @Override
            public Object render(final CalendarWrapper model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<CalendarWrapper> store,
                    Grid<CalendarWrapper> grid) {

                final Button shareLinkButton = Forms.button("", IconImageBundle.ICONS.shareLink());

                shareLinkButton.setPixelSize(5, 5);
               // String nextLine="<br />";
                shareLinkButton.setTitle("Click here to get URL to share the " + getEventTypeName(model.getCalendar().getStyle())+".\nYou can copy the link and use it to import Sigmah calendar events to another calendar  supporting iCal format (f.e. Google calendar)");
                shareLinkButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                    @Override
                    public void componentSelected(final ButtonEvent ce) {
                        String shareURL = createShareURL();
                        final DecoratedPopupPanel detailPopup = new DecoratedPopupPanel(true);
                        final com.google.gwt.user.client.ui.Grid popupContent = new com.google.gwt.user.client.ui.Grid(1, 1);
                        popupContent.setText(0, 0, "URL: " + shareURL);

                        detailPopup.setWidth("200");
                        detailPopup.setWidget(popupContent);

                        // Show the popup
                        detailPopup.setPopupPositionAndShow(new PositionCallback() {

                            @Override
                            public void setPosition(int offsetWidth, int offsetHeight) {
                                detailPopup.getElement().getStyle().setPropertyPx("left", shareLinkButton.getAbsoluteLeft() + 20);
                                detailPopup.getElement().getStyle().setPropertyPx("top", shareLinkButton.getAbsoluteTop() - 20);
                                detailPopup.getElement().getStyle().setProperty("bottom", "");
                            }
                        });
                    }

                    private String createShareURL() {
                        String currentPageHref = Window.Location.getHref();
                        String pathName = Window.Location.getPath();
                        String projectId = currentPageHref.substring(currentPageHref.lastIndexOf("&id="), currentPageHref.length());
                        int posSigmah = currentPageHref.lastIndexOf(pathName);
                        String shareURL = currentPageHref.substring(0, posSigmah);
                        shareURL += pathName + "ExportCalendar?type=";
                        shareURL += getEventTypeName(model.getCalendar().getStyle());
                        shareURL += projectId;
                        return shareURL;
                    }
                });
                return shareLinkButton;
            }
        });

        selectionModel = new CheckBoxSelectionModel<CalendarWrapper>();

        final ColumnModel calendarColumnModel = new ColumnModel(Arrays.asList(selectionModel.getColumn(), calendarName, calendarButton, calendarColor));

        calendarsStore = new ListStore<CalendarWrapper>();

        final Grid<CalendarWrapper> calendarGrid = new Grid<CalendarWrapper>(calendarsStore, calendarColumnModel);

        calendarGrid.setAutoExpandColumn("name");
        calendarGrid.setSelectionModel(selectionModel);
        calendarGrid.addPlugin(selectionModel);

        calendarGrid.getView().setForceFit(true);

        calendarsPanel.add(calendarGrid);
        return calendarsPanel;
    }

    
    private String getEventTypeName(int eventType) {
        switch (eventType) {
            case 1:
                return "activities";
            case 2:
                return "events";
            case 3:
                return "expected";
            case 4:
                return "todo";
            default:
                return "activities";
        }
    }
    /**
     * Creates the calendars main panel, place holder for the
     * {@link CalendarWidget}.
     *
     * @return The calendars main panel.
     */

    private Component createCalendarsMainPanel() {

        calendarView = Panels.content(I18N.CONSTANTS.loading(), "panel-background"); // Temporary title.

        // Toolbar
        toolbar = new ToolBar();

        // Today button - center the calendar on the current day
        todayButton = Forms.button(I18N.CONSTANTS.today());
        toolbar.add(todayButton);

        toolbar.add(new SeparatorToolItem());

        // Week button - changes the calendar to display weeks
        weekButton = Forms.button(I18N.CONSTANTS.week());
        toolbar.add(weekButton);

        // Week button - changes the calendar to display monthes
        monthButton = Forms.button(I18N.CONSTANTS.month());
        toolbar.add(monthButton);

        toolbar.add(new SeparatorToolItem());

        // Previous button - move back from one unit of time (week / month)
        previousButton = Forms.button(I18N.CONSTANTS.previous());
        toolbar.add(previousButton);

        // Next button - move forward from one unit of time (week / month)
        nextButton = Forms.button(I18N.CONSTANTS.next());
        toolbar.add(nextButton);

        toolbar.add(new SeparatorToolItem());

        addEventButton = Forms.button(I18N.CONSTANTS.calendarAddEvent());

        reminderAddButton = new Button(I18N.CONSTANTS.reminderPoint(), IconImageBundle.ICONS.add());

        monitoredPointsAddButton = new Button(I18N.CONSTANTS.monitoredPoint(), IconImageBundle.ICONS.add());

        calendarView.setTopComponent(toolbar);

        return calendarView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeCalendarWidget(final CalendarWidget calendarWidget) {

        // Defining the first day of the week
        // BUGFIX #653: Contrary to the deprecated DateTimeConstants, firstDayOfTheWeek starts at 0 (and not 1). No substraction is needed.
        final DateTimeFormatInfo constants = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo();
        calendarWidget.setFirstDayOfWeek(constants.firstDayOfTheWeek());

        // Retrieving the current calendar header
        calendarView.setHeadingHtml(calendarWidget.getHeading());

        // Listening for further calendar header changes
        calendarWidget.setListener(new CalendarWidget.CalendarListener() {

            @Override
            public void afterRefresh() {
                calendarView.setHeadingHtml(calendarWidget.getHeading());
            }
        });

        calendarView.add(calendarWidget, Layouts.fitData(Margin.DOUBLE_TOP, Margin.DOUBLE_RIGHT, Margin.DOUBLE_BOTTOM, Margin.DOUBLE_LEFT));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddEventButtonEnabled(final boolean addEventButtonEnabled) {

        if (toolbar.indexOf(addEventButton) != -1) {
            toolbar.remove(addEventButton);
            toolbar.remove(reminderAddButton);
            toolbar.remove(monitoredPointsAddButton);
        }

        if (addEventButtonEnabled) {
            toolbar.add(addEventButton);
            toolbar.add(reminderAddButton);
            toolbar.add(monitoredPointsAddButton);

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Button getAddEventButton() {
        return addEventButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Button getTodayButton() {
        return todayButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Button getWeekButton() {
        return weekButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Button getMonthButton() {
        return monthButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Button getPreviousButton() {
        return previousButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Button getNextButton() {
        return nextButton;
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
    public AbstractStoreSelectionModel<CalendarWrapper> getCalendarsSelectionModel() {
        return selectionModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListStore<CalendarWrapper> getCalendarsStore() {
        return calendarsStore;
    }
}