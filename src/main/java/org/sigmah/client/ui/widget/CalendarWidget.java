package org.sigmah.client.ui.widget;

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
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.panel.ClickableFlowPanel;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.calendar.ActivityCalendarIdentifier;
import org.sigmah.shared.dto.calendar.Event;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.util.ProfileUtils;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.Widget;
import org.sigmah.client.util.FormattingUtil;

/**
 * This widget displays a calendar.
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@SuppressWarnings("deprecation")
public class CalendarWidget extends Composite {

    public static final int CELL_DEFAULT_WIDTH = 150;
    public static final int CELL_DEFAULT_HEIGHT = 80;

    /**
     * Multiple calculated (&quot;cached&quot;) values reused during laying out
     * the month view elements.
     */
    private static final int DAYS_IN_A_WEEK = 7;
    private int calculatedWeekDayHeaderHeight;
    private int calculatedDayHeaderHeight;
    /**
     * Height of each Cell (day), including the day's header.
     */
    private float calculatedCellOffsetHeight;

    /**
     * Height of each Cell (day), excluding the day's header.
     */
    private float calculatedCellHeight;

    public interface CalendarListener {

        void afterRefresh();

    }

    public interface Delegate {

        void edit(Event event, CalendarWidget calendarWidget);

        void delete(Event event, CalendarWidget calendarWidget);

        void deleteChain(Event event, CalendarWidget calendarWidget);

    }

    /**
     * Types of displays availables for a calendar.
     *
     * @author rca
     */
    public static enum DisplayMode {
        DAY(1, 1) {

            @Override
            public Date getStartDate(Date date, int firstDay) {
                return new Date(date.getYear(), date.getMonth(), date.getDate());
            }

            @Override
            public void nextDate(Date currentDate) {
                currentDate.setDate(currentDate.getDate() + 1);
            }

            @Override
            public void previousDate(Date currentDate) {
                currentDate.setDate(currentDate.getDate() - 1);
            }

            @Override
            public void firstDay(Date currentDate, Date today, int firstDay) {
                currentDate.setYear(today.getYear());
                currentDate.setMonth(today.getMonth());
                currentDate.setDate(today.getDate());
            }

            @Override
            public String getStyleName() {
                return "calendar-day";
            }
        },
        WEEK(DAYS_IN_A_WEEK, 1) {

            @Override
            public Date getStartDate(Date date, int firstDay) {
                return getFirstDateOfWeek(date, firstDay);
            }

            @Override
            public void nextDate(Date currentDate) {
                currentDate.setDate(currentDate.getDate() + DAYS_IN_A_WEEK);
            }

            @Override
            public void previousDate(Date currentDate) {
                currentDate.setDate(currentDate.getDate() - DAYS_IN_A_WEEK);
            }

            @Override
            public void firstDay(Date currentDate, Date today, int firstDay) {
                int decal = (today.getDay() + DAYS_IN_A_WEEK - firstDay) % DAYS_IN_A_WEEK;
                currentDate.setYear(today.getYear());
                currentDate.setMonth(today.getMonth());
                currentDate.setDate(today.getDate() - decal);
                final Date date = getFirstDateOfWeek(today, firstDay);
                currentDate.setTime(date.getTime());
            }

            @Override
            public String getStyleName() {
                return "calendar-week";
            }
        },
        MONTH(7, 6) {

            @Override
            public Date getStartDate(Date date, int firstDay) {
                Date firstDayOfMonth = new Date(date.getYear(), date.getMonth(), 1);
                return getFirstDateOfWeek(firstDayOfMonth, firstDay);
            }

            @Override
            public void nextDate(Date currentDate) {
                currentDate.setMonth(currentDate.getMonth() + 1);
            }

            @Override
            public void previousDate(Date currentDate) {
                currentDate.setMonth(currentDate.getMonth() - 1);
            }

            @Override
            public void firstDay(Date currentDate, Date today, int firstDay) {
                currentDate.setYear(today.getYear());
                currentDate.setMonth(today.getMonth());
                currentDate.setDate(1);
            }

            @Override
            public String getStyleName() {
                return "calendar-month";
            }
        };

        private int columns;
        private int rows;

        private DisplayMode(int columns, int rows) {
            this.columns = columns;
            this.rows = rows;
        }

        public int getRows() {
            return rows;
        }

        public int getColumns() {
            return columns;
        }

        public abstract Date getStartDate(Date date, int firstDay);

        public abstract void nextDate(Date currentDate);

        public abstract void previousDate(Date currentDate);

        public abstract void firstDay(Date currentDate, Date today, int firstDay);

        public abstract String getStyleName();
    }

    public final static int NO_HEADERS = 0;
    public final static int COLUMN_HEADERS = 1;
    public final static int ALL_HEADERS = 2;

    private final static int UNDEFINED = -1;
    private final static int EVENT_HEIGHT = 16;
    private int eventLimit = UNDEFINED;

    private int firstDayOfWeek;
    private DisplayMode displayMode = DisplayMode.MONTH;
    private int displayHeaders = ALL_HEADERS;
    private boolean displayWeekNumber = true;

    private List<Calendar> calendars;

    private Date today;
    private Date startDate;

    private DateTimeFormat titleFormatter = DateTimeFormat.getFormat("MMMM y");
    private DateTimeFormat headerFormatter = DateTimeFormat.getFormat("EEEE");
    private DateTimeFormat dayFormatter = DateTimeFormat.getFormat("d");
    private DateTimeFormat hourFormatter = DateTimeFormat.getFormat("HH:mm");

    private CalendarListener listener;
    private Delegate delegate;

    private final Authentication authentication;

    public CalendarWidget(int displayHeaders, boolean displayWeekNumber, Authentication authentication) {
        this.calendars = new ArrayList<Calendar>();
        this.displayHeaders = displayHeaders;
        this.displayWeekNumber = displayWeekNumber;
        this.authentication = authentication;

        // final SimplePanel container;
        final FlexTable grid = new FlexTable();
//        getColumnFormatter().getElement( column ).setAttribute( "width",
//        grid.getFlexCellFormatter().getElement(0, 0)
        grid.addStyleName("calendar");
        grid.addStyleName(displayMode.getStyleName());
//        VerticalPanel dialogContents = new VerticalPanel();
//         dialogContents.add(grid);
//                initWidget(dialogContents);
        initWidget(grid);

        final Date now = new Date();
        today = new Date(now.getYear(), now.getMonth(), now.getDate());
        startDate = new Date(0, 0, 0);

        today();
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public void setListener(CalendarListener listener) {
        this.listener = listener;
    }

    public void next() {
        displayMode.nextDate(startDate);
        refresh();
    }

    public void previous() {
        displayMode.previousDate(startDate);
        refresh();
    }

    public final void today() {
        displayMode.firstDay(startDate, today, firstDayOfWeek);
        refresh();
    }

    /**
     * Retrieves the current start date of the calendar.
     *
     * @return the current start date of the calendar.
     */
    public Date getStartDate() {
        return startDate;
    }

    public void addCalendar(Calendar calendar) {
        calendars.add(calendar);
        refresh();
    }

    public List<Calendar> getCalendars() {
        return calendars;
    }

    public void setCalendars(List<Calendar> calendars) {
        // this.calendars = calendars;
//                 Window.alert("SET setCalendars");
//        for (Calendar calendar : calendars) {
//            if (!(calendar.getIdentifier() instanceof ActivityCalendarIdentifier)) {
//                if (calendar.getEvents() != null) {
//                    //SSS     final Map<Date, List<Event>> eventMap = normalize(calendar.getEvents());
//                    final Map<Date, List<Event>> eventMap = normalizeHourEvents(calendar.getEvents());
//                    calendar.setEvents(eventMap);
//                }
//            }
//        }
        this.calendars = calendars;
        refresh();
    }

    /**
     * Defines the formatter used to display the title of the calendar.<br>
     * <br>
     * The default format is "<code>MonthName</code> <code>FullYear</code> "
     * (pattern : "N y").
     *
     * @param titleFormatter The formatter to use to display the title of the
     * calendar.
     */
    public void setTitleFormatter(DateTimeFormat titleFormatter) {
        this.titleFormatter = titleFormatter;
        refresh();
    }

    /**
     * Defines the formatter used to display the title of each column.<br>
     * <br>
     * The default format is "<code>WeekName</code>" (pattern : "E").
     *
     * @param headerFormatter The formatter to use to display the title of each
     * column.
     */
    public void setHeaderFormatter(DateTimeFormat headerFormatter) {
        this.headerFormatter = headerFormatter;
        refresh();
    }

    /**
     * Defines the formatter used to display the title of each cell.<br>
     * <br>
     * The default format is "<code>DayNumber</code>" (pattern : "d").
     *
     * @param dayFormatter The formatter to use to display the title of each
     * cell.
     */
    public void setDayFormatter(DateTimeFormat dayFormatter) {
        this.dayFormatter = dayFormatter;
        refresh();
    }

    /**
     * Defines the display mode of the calendar and perform a redraw.
     *
     * @param displayMode Style of the calendar (day, week or month).
     * @see CalendarWidget.DisplayMode
     */
    public void setDisplayMode(DisplayMode displayMode) {
        final FlexTable grid = (FlexTable) getWidget();

        clear();

        // Resetting the CSS style
        grid.removeStyleName(this.displayMode.getStyleName());

        this.displayMode = displayMode;

        // Applying the CSS style associated with the new display mode
        grid.addStyleName(displayMode.getStyleName());
        refresh();
    }

    /**
     * Defines the first day of the week and refresh the calendar.
     *
     * @param firstDayOfWeek The first day of the week as an int (Sunday = 0,
     * Saturday = 6)
     */
    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
        refresh();
    }

    public int getDisplayHeaders() {
        return displayHeaders;
    }

    public void setDisplayHeaders(int displayHeaders) {
        clear();
        this.displayHeaders = displayHeaders;
        refresh();
    }

    public boolean isDisplayWeekNumber() {
        return displayWeekNumber;
    }

    public void setDisplayWeekNumber(boolean displayWeekNumber) {
        clear();
        this.displayWeekNumber = displayWeekNumber;
        refresh();
    }

    /**
     * Removes all rows. Must be when the structure of the calendar has been
     * changed (display mode)
     */
    private void clear() {
        final FlexTable grid = (FlexTable) getWidget();
        grid.clear();
        grid.removeAllRows();
    }

    /**
     * @param date1
     * @param date2
     * @return boolean indicating if date1 and date2 are on the same day
     */
    public static boolean isSameDay(Date date1, Date date2) {
        DateTimeFormat fmt = DateTimeFormat.getFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    /**
     * Normalizes the given {@code calendar}'s events map (needed particularly
     * when there is a timezone difference between the client and the server).
     *
     * @param calendar The calendar instance.
     * @return The map with each event with the right key.
     */
    //public static Map<Date, List<Event>> normalize(final Calendar calendar) {
    public static Map<Date, List<Event>> normalize(final Map<Date, List<Event>> eventMap) {

//		final Map<Date, List<Event>> eventMap = calendar.getEvents();
        final Map<Date, List<Event>> eventMapNormalized = new HashMap<Date, List<Event>>();

//		boolean isActivityCalendar = false;
//		if (calendar.getIdentifier() instanceof ActivityCalendarIdentifier) {
//			isActivityCalendar = true;
//		}
        for (final Date key : eventMap.keySet()) {
            for (final Event event : eventMap.get(key)) {
                Date normalizedKeyDate = new Date(key.getYear(), key.getMonth(), key.getDate());

                // Activities events have different startDate from the key date
                // They shouldn't be placed in their startDate list
                //if (!isSameDay(normalizedKeyDate, event.getDtstart()) && !isActivityCalendar) {
                if (!isSameDay(normalizedKeyDate, event.getDtstart())) {
                    normalizedKeyDate = new Date(event.getDtstart().getYear(), event.getDtstart().getMonth(), event.getDtstart().getDate());
                }

                if (eventMapNormalized.get(normalizedKeyDate) == null) {
                    eventMapNormalized.put(normalizedKeyDate, new ArrayList<Event>());
                }
                eventMapNormalized.get(normalizedKeyDate).add(event);
            }
        }

        return eventMapNormalized;
    }

    public static Map<Date, List<Event>> normalizeHourEvents(final Map<Date, List<Event>> eventMap) {

        final Map<Date, List<Event>> hourEventMapNormalized = new HashMap<Date, List<Event>>();

        for (final Date key : eventMap.keySet()) {
            for (final Event event : eventMap.get(key)) {
                Date normalizedKeyDate = new Date(key.getYear(), key.getMonth(), key.getDate());
                if (!isSameDay(event.getDtstart(), event.getDtend())) {
                    //   Window.alert("! isSameDay" + event.getSummary());
                    // if (event.getDtstart() != event.getDtend()) {
                    //1   
                    int daysdiff = calculateEventDurationInDays(event);
                    for (int i = 0; i < daysdiff; i++) {
                        long StartTime = event.getDtstart().getTime() + (24 * 60 * 60 * 1000) * i;
                        long EndTime = event.getDtstart().getTime() + (24 * 60 * 60 * 1000) * i;

                        Date StartDate = new Date(StartTime);

                        Date EndDate = new Date(EndTime);
                        EndDate.setHours(event.getDtend().getHours());
                        EndDate.setMinutes(event.getDtend().getMinutes());

                        Event theNewEv = new Event();
                        theNewEv.setDtstart(StartDate);
                        theNewEv.setDtend(EndDate);
//                        theNewEv.setIdentifier(event.getIdentifier());
//                        theNewEv.setReferenceId(event.getReferenceId());
//                        theNewEv.setParent(event.getParent());
                        theNewEv.setSummary(event.getSummary());// + "-S-" + i);
                        theNewEv.setDescription(event.getDescription());// + "-D-" + i);
//                        theNewEv.setEventType(event.getEventType());
                        ////////////
                        if (!isSameDay(normalizedKeyDate, theNewEv.getDtstart())) {
                            normalizedKeyDate = new Date(theNewEv.getDtstart().getYear(), theNewEv.getDtstart().getMonth(), theNewEv.getDtstart().getDate());
                        }

                        if (hourEventMapNormalized.get(normalizedKeyDate) == null) {
                            hourEventMapNormalized.put(normalizedKeyDate, new ArrayList<Event>());
                        }
                        event.setSummary(theNewEv.getSummary());
                        event.setDescription(theNewEv.getDescription());
                        if (!hourEventMapNormalized.get(normalizedKeyDate).contains(event)) {
                            hourEventMapNormalized.get(normalizedKeyDate).add(event);
                        }
                        //                       -------------
//                        if (!isSameDay(normalizedKeyDate, theNewEv.getDtstart())) {
//                            normalizedKeyDate = new Date(theNewEv.getDtstart().getYear(), theNewEv.getDtstart().getMonth(), theNewEv.getDtstart().getDate());
//                        }
//
//                        if (hourEventMapNormalized.get(normalizedKeyDate) == null) {
//                            hourEventMapNormalized.put(normalizedKeyDate, new ArrayList<Event>());
//                        }
//                        hourEventMapNormalized.get(normalizedKeyDate).add(theNewEv);

                        ///////
                    }
                } else {
                    //  Window.alert("THE SameDay " + event.getSummary());
                    if (!isSameDay(normalizedKeyDate, event.getDtstart())) {
                        normalizedKeyDate = new Date(event.getDtstart().getYear(), event.getDtstart().getMonth(), event.getDtstart().getDate());
                    }

                    if (hourEventMapNormalized.get(normalizedKeyDate) == null) {
                        hourEventMapNormalized.put(normalizedKeyDate, new ArrayList<Event>());
                    }
                    if (!hourEventMapNormalized.get(normalizedKeyDate).contains(event)) {
                        hourEventMapNormalized.get(normalizedKeyDate).add(event);
                    }
                }
            }
        }

        return hourEventMapNormalized;
    }

    /**
     * Calculates the number of events that can be displayed in a cell.
     */
    public void calibrateCalendar() {
        final FlexTable grid = (FlexTable) getWidget();

        final Element row = grid.getRowFormatter().getElement(displayHeaders);
        row.setId("calendar-row-calibration");

        final Element cell = grid.getCellFormatter().getElement(displayHeaders, displayWeekNumber ? 1 : 0);
        cell.setId("calendar-cell-calibration");
//ss
//           cell.setPropertyString(DEBUG_ID_PREFIX, DEBUG_ID_PREFIX);setAttribute("hight", "40px");
        eventLimit = (getCellHeight(CELL_DEFAULT_HEIGHT) / EVENT_HEIGHT) - 2;
        if (eventLimit < 0) {
            eventLimit = 0;
        }

    }

    /**
     * Calculates the height of the cell identified by
     * "calendar-cell-calibration".
     *
     * @return height of a cell.
     */
    private native int getCellHeight(int defaultHeight) /*-{
		var height = 0;

		if (!$wnd.getComputedStyle)
			return defaultHeight;

		var row = $wnd.document.getElementById('calendar-row-calibration');

		var style = $wnd.getComputedStyle(row, null);
		height += parseInt(style.height);

		return height;
	}-*/;

    /**
     * Calculates the width of the cell identified by
     * "calendar-cell-calibration".
     *
     * @return width of a cell.
     */
    private native int getCellWidth(int defaultWidth) /*-{
		var width = 0;

		if (!$wnd.getComputedStyle)
			return defaultWidth;

		var cell = $wnd.document.getElementById('calendar-cell-calibration');

		var style = $wnd.getComputedStyle(cell, null);
		width += parseInt(style.width);

		return width;
	}-*/;

    /**
     * Retrieves the current heading of the calendar.
     *
     * @return The heading value.
     */
    public String getHeading() {
        final String title = titleFormatter.format(startDate);
        return Character.toUpperCase(title.charAt(0)) + title.substring(1);
    }

    /**
     * Render the calendar.
     */
    public void refresh() {
        final FlexTable grid = (FlexTable) getWidget();
        grid.removeAllRows();
        drawEmptyCells();

        if (isAttached()) {
            calibrateCalendar();
            drawEvents();
        }
        if (listener != null) {
            listener.afterRefresh();
        }
    }

    /**
     * Render the whole calendar but do not render the events.
     */
    public void drawEmptyCells() {
        final FlexTable grid = (FlexTable) getWidget();

        final int rows = displayMode.getRows() + displayHeaders;
        final int columns = displayMode.getColumns() + (displayWeekNumber ? 1 : 0);

        Date date = displayMode.getStartDate(startDate, firstDayOfWeek);

        // Column headers
        if (displayHeaders != NO_HEADERS) {
            if (displayHeaders == ALL_HEADERS) {
                // Header of the calendar
                final Label calendarHeader = new Label(getHeading());
                calendarHeader.addStyleName("calendar-header");
                grid.setWidget(0, 0, calendarHeader);
                grid.getFlexCellFormatter().setColSpan(0, 0, columns + (displayWeekNumber ? 1 : 0));
            }

            final Date currentHeader = new Date(date.getTime());
            for (int x = displayWeekNumber ? 1 : 0; x < columns; x++) {
                final Label columnHeader = new Label(headerFormatter.format(currentHeader));
                columnHeader.addStyleName("calendar-column-header");
                grid.setWidget(displayHeaders == ALL_HEADERS ? 1 : 0, x, columnHeader);

                currentHeader.setDate(currentHeader.getDate() + 1);
            }
        }

        int currentMonth = startDate.getMonth();
        for (int y = displayHeaders; y < rows; y++) {
            if (displayWeekNumber) {
                grid.getCellFormatter().addStyleName(y, 0, "calendar-row-header");
                grid.setText(y, 0, Integer.toString(getWeekNumber(date, firstDayOfWeek)));
            }

            for (int x = displayWeekNumber ? 1 : 0; x < columns; x++) {
                drawCell(y, x, date, currentMonth);
                date.setDate(date.getDate() + 1);
            }
        }
    }

    /**
     * Render the events for every cells.
     */
    public void drawEvents() {
        final int rows = displayMode.getRows() + displayHeaders;
        final int columns = displayMode.getColumns() + (displayWeekNumber ? 1 : 0);

        Date date = displayMode.getStartDate(startDate, firstDayOfWeek);
        List<Event> alreadyShownWeekViewEvents = new ArrayList<Event>();
        int[][] theShiftVecorOfFullDayEvents = new int[8][10];

        for (int y = displayHeaders; y < rows; y++) {
            for (int x = displayWeekNumber ? 1 : 0; x < columns; x++) {

                if (displayMode.equals(displayMode.MONTH)) {
                    drawFullDayEvents(y, x, date);
                    drawHourMultiDayEvents(y, x, date);
                    drawEvents(y, x, date, -1);
                } else {
                    drawFullDayEventsForWeek(1, x, date, alreadyShownWeekViewEvents, theShiftVecorOfFullDayEvents);
                    drawHourMultiDayEventsForWeek(1, x, date, alreadyShownWeekViewEvents, theShiftVecorOfFullDayEvents);
                    drawEvents(1, x, date, getLastFDPanelInCellCounter(theShiftVecorOfFullDayEvents, x));
                }
                date.setDate(date.getDate() + 1);
            }
            if (displayMode.equals(displayMode.WEEK)) {
                break;
            }
        }
    }

    private int getLastFDPanelInCellCounter(int[][] theShiftVecorOfFullDayEvents, int theDayColumn) {
        int theFDEventInColumnMaxlength = theShiftVecorOfFullDayEvents[theDayColumn].length;
        int theLastFDEventInCellPosition = 0;
        for (int position = theFDEventInColumnMaxlength; position >= 0; position--) {
            if (theShiftVecorOfFullDayEvents[theDayColumn][position] > 0) {
                theLastFDEventInCellPosition = position;
                break;
            }
        }
        return theLastFDEventInCellPosition;
    }

    /**
     * Render the cell located at <code>column</code>, <code>row</code>
     *
     * @param row
     * @param column
     * @param date
     * @param currentMonth
     */
    private void drawCell(int row, int column, Date date, int currentMonth) {
        final Label header = new Label(dayFormatter.format(date));
        header.addStyleName("calendar-cell-header");

        final FlexTable grid = (FlexTable) getWidget();

        grid.getCellFormatter().setStyleName(row, column, "calendar-cell");

        FlowPanel cell = (FlowPanel) grid.getWidget(row, column);
        if (cell == null) {
            cell = new FlowPanel();
            cell.setWidth("100%");

            grid.setWidget(row, column, cell);
        } else {
            // Reusing an existing cell
            cell.clear();
        }

        if (currentMonth != date.getMonth()) {
            grid.getCellFormatter().addStyleName(row, column, "calendar-cell-other-month");
        }
        if (date.equals(today)) {
            grid.getCellFormatter().addStyleName(row, column, "calendar-cell-today");
        }

        cell.add(header);
    }

    /**
     * Display the events for the cell located at <code>column</code>,
     * <code>row</code>
     *
     * @param row
     * @param column
     * @param date
     * @param currentMonth
     */
    private void drawEvents(int row, int column, final Date date, int startDrawPosition) {
        final FlexTable grid = (FlexTable) getWidget();
        int fullDayEventCounter = 0;
        int hourMultiDayEventCounter = 0;

        final FlowPanel cell = (FlowPanel) grid.getWidget(row, column);

        if (cell == null) {
            throw new NullPointerException("The specified cell (" + row + ',' + column + ") doesn't exist.");
        }

        // Displaying events
        TreeSet<Event> sortedHourEvents = createSortedEventsSet();
        // Displaying multi day events       
        TreeSet<Event> sortedHourMultiDayEvents = createSortedEventsSet();
        // Displaying full day events
        TreeSet<Event> sortedFullDayEvents = createSortedEventsSet();

        for (final Calendar calendar : calendars) {
            if (!(calendar.getIdentifier() instanceof ActivityCalendarIdentifier)) {
                if (calendar.getEvents() != null) {
                    //   && calendar.getEvents().size()>0) {
                    //SSS     final Map<Date, List<Event>> eventMap = normalize(calendar.getEvents());
                    //ss 
                    Map<Date, List<Event>> eventMap = normalizeHourEvents(calendar.getEvents());
                    //  final Map<Date, List<Event>> eventMap = normalize(eventMap2);
                    //final Map<Date, List<Event>> eventMap = calendar.getEvents();

                    final List<Event> events = eventMap.get(date);
                    if (events != null) {
                        sortedHourEvents.addAll(events);
                    }
                }
                hourMultiDayEventCounter = getHourMulriDayEventCounter(calendar, date, hourMultiDayEventCounter, sortedHourMultiDayEvents);
                fullDayEventCounter = getFullDayEventCounter(calendar, date, fullDayEventCounter, sortedFullDayEvents);
            }
        }

        final Iterator<Event> iterator = sortedHourEvents.iterator();
        if (startDrawPosition == -1) {
            eventLimit = 1;
        } else {
            eventLimit = 99;
        }

        for (int i = 0; iterator.hasNext() && i < eventLimit; i++) {
            final Event event = iterator.next();

            final ClickableFlowPanel flowPanel = new ClickableFlowPanel();
            flowPanel.addStyleName("calendar-event");

            boolean fullDayEvent = false;

            final StringBuilder eventDate = new StringBuilder();
            eventDate.append(hourFormatter.format(event.getDtstart()));
            if (event.getDtend() != null) {
                eventDate.append(" ");
                eventDate.append(hourFormatter.format(event.getDtend()));
//SS
//                if (event.getDtstart().getDate() != event.getDtend().getDate()
//                        || event.getDtstart().getMonth() != event.getDtend().getMonth()
//                        || event.getDtstart().getYear() != event.getDtend().getYear()) {
                if (event.getEventType().contains("F")) {
                    fullDayEvent = true;
                    flowPanel.addStyleName("calendar-fullday-event");
                }
            }

            final InlineLabel dateLabel = new InlineLabel(eventDate.toString());
            dateLabel.addStyleName("calendar-event-date");

            //final InlineLabel eventLabel = new InlineLabel(event.getReferenceId()==null?event.getSummary():"--->>");
            //  final InlineLabel eventLabel = new InlineLabel(row + "; " + (!(event.getReferenceId() != null && event.getReferenceId().intValue() > 0)? event.getSummary() : event.getSummary() + "->>serial"));
            final InlineLabel eventLabel = new InlineLabel(event.getSummary());
            eventLabel.addStyleName("calendar-event-label");

            if (fullDayEvent) {
                flowPanel.addStyleName("calendar-fullday-event-" + event.getParent().getStyle());
            } else {
                eventLabel.addStyleName("calendar-event-" + event.getParent().getStyle());
            }

            if (!fullDayEvent) {
                flowPanel.add(dateLabel);

                int daysdiff = calculateEventDurationInDays(event);

                flowPanel.setTitle(createTitleForHourEvent(daysdiff, event));
            }
            flowPanel.add(eventLabel);
            if (event.getEventType().contains("H")) {
                DOM.setStyleAttribute(flowPanel.getElement(), "background-color", "rgba(28,97,217)");
                //  DOM.setStyleAttribute(flowPanel.getElement(), "background", "linear-gradient(-90deg, #1c61d9, #0000)");
            }
            final DecoratedPopupPanel detailPopup = new DecoratedPopupPanel(true);

            final Grid popupContent = new Grid(event.getParent().isEditable() ? 6 : 3, 1);
            popupContent.setText(0, 0, event.getSummary());
            popupContent.getCellFormatter().addStyleName(0, 0, "calendar-popup-header");

            if (!fullDayEvent) {
                popupContent.getCellFormatter().addStyleName(1, 0, "calendar-popup-date");
                popupContent.getCellFormatter().addStyleName(1, 0, "calendar-event-" + event.getParent().getStyle());
                popupContent.setText(1, 0, eventDate.toString());
            } else {
                popupContent.setText(1, 0, "");
            }

            if (event.getDescription() != null && !"".equals(event.getDescription())) {
                popupContent.getCellFormatter().addStyleName(2, 0, "calendar-popup-description");
                popupContent.setText(2, 0, event.getDescription());
            } else {
                popupContent.setText(2, 0, "");
            }

            if (event.getParent().isEditable()
                    && ProfileUtils.isGranted(authentication, GlobalPermissionEnum.EDIT_PROJECT_AGENDA)) {

                final Anchor editAnchor = new Anchor(I18N.CONSTANTS.calendarEditEvent());
                editAnchor.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        delegate.edit(event, CalendarWidget.this);
                    }
                });

                final Anchor deleteAnchor = new Anchor(I18N.CONSTANTS.calendarDeleteEvent());
                deleteAnchor.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        delegate.delete(event, CalendarWidget.this);
                        detailPopup.hide();
                    }
                });

                final Anchor deleteChainAnchor = new Anchor("Delete all recurrences");
                deleteChainAnchor.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent clickEvent) {
                        delegate.deleteChain(event, CalendarWidget.this);
                        refresh();
                        detailPopup.hide();

                    }
                });

                popupContent.setWidget(3, 0, editAnchor);
                popupContent.setWidget(4, 0, deleteAnchor);
                if (!event.getEventType().contains("O")) {
                    popupContent.setWidget(5, 0, deleteChainAnchor);
                }
            }

            detailPopup.setWidget(popupContent);

            flowPanel.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    final int left = flowPanel.getAbsoluteLeft() - 10;
                    final int bottom = Window.getClientHeight() - flowPanel.getAbsoluteTop();

                    detailPopup.setWidth((getCellWidth(CELL_DEFAULT_WIDTH) + 20) + "px");

                    // Show the popup
                    detailPopup.setPopupPositionAndShow(new PositionCallback() {

                        @Override
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            detailPopup.getElement().getStyle().setPropertyPx("left", left);
                            detailPopup.getElement().getStyle().setProperty("top", "");
                            detailPopup.getElement().getStyle().setPropertyPx("bottom", bottom);
                        }
                    });
                }
            });

            if (startDrawPosition != -1) {
                int height = 12;
                int top = startDrawPosition > 0 ? (startDrawPosition - 1) * height : 0;
                DOM.setStyleAttribute(flowPanel.getElement(), "position", "relative");
                DOM.setStyleAttribute(flowPanel.getElement(), "top", top + "px");
                // startDrawPosition ++;
            }
            cell.add(flowPanel);
        }

        //	if (eventLimit != UNDEFINED && (fullDayEventCounter + sortedHourEvents.size()) > eventLimit) {
        if (startDrawPosition == -1 && eventLimit != UNDEFINED && (hourMultiDayEventCounter + fullDayEventCounter + sortedHourEvents.size()) > 1) {
            //final Anchor eventLabel = new Anchor("\u25BC +" + (sortedHourEvents.size()-eventLimit) + " st:" + fullDayEventCounter +" fd events");
            final Anchor eventLabel = new Anchor("\u25BC " + (sortedHourEvents.size() + hourMultiDayEventCounter + fullDayEventCounter) + " events");
            // eventLabel.setTitle("Click to view all events of the day.");
            final Date thisDate = new Date(date.getTime());
            String theDateString = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_WEEKDAY_DAY).format(thisDate);
            eventLabel.setTitle(theDateString + "\nClick to view all events of the day:"
                    + "\nOne day events: " + sortedHourEvents.size()
                    + "\nMulti day events: " + hourMultiDayEventCounter
                    + "\nFull day events: " + fullDayEventCounter);

            eventLabel.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    startDate = thisDate;
                    setDisplayMode(DisplayMode.WEEK);
                }
            });
            eventLabel.addStyleName("calendar-event-limit");
            DOM.setStyleAttribute(eventLabel.getElement(), "border-color", "#1c61d9");
            DOM.setStyleAttribute(eventLabel.getElement(), "border-style", "solid");
            DOM.setStyleAttribute(eventLabel.getElement(), "border-width", "1px");

//            if (fullDayEventCounter > 0 
//                
//                
//                sortedHourEvents.size() //--        DOM.setStyleAttribute(eventLabel.getElement(), "position", "relative");
            //  DOM.setStyleAttribute(eventLabel.getElement(), "top", (cell.getOffsetHeight()-16) + "px");
            //--      DOM.setStyleAttribute(eventLabel.getElement(), "top", (cell.getAbsoluteTop() + 30) + "px");
            //              Window.alert(cell.getTitle() + " cell.getOffsetHeight()="+cell.getOffsetHeight()
            //              + " - " + " cell.getAbsoluteTop())=" +  cell.getAbsoluteTop()
            //                      + " Label  OffsetHeight = " + eventLabel.getElement().getOffsetHeight()+
            //                      " cell.getElement() OffsetHeight =" + cell.getElement().getOffsetHeight()+
            //                      " cell.getElement() AbsoluteBottom=" + cell.getElement().getAbsoluteBottom()+
            //                      " cell.getElement() ClientHeight =" + cell.getElement().getClientHeight()
            //                      )  ;
/*            int chiled = grid.getCellFormatter().getElement(row, column).getChildCount();

            int gridHeight = grid.getOffsetHeight();
            int weekdayRowHeight = grid.getRowFormatter()
                    .getElement(0).getOffsetHeight();
             */
//            Window.alert("chiled=" + chiled + " gridHeight =" + gridHeight
//                    + " weekdayRowHeight =" + weekdayRowHeight
//            );
            cell.add(eventLabel);
        }
    }

    private int getHourMulriDayEventCounter(final Calendar calendar, final Date date, int hourMultiDayEventCounter, TreeSet<Event> sortedHourMultiDayEvents) {
        //hourMultiDayEventCounter
        if (calendar.getHourMultiDayEvents() != null) {
            final Map<Date, List<Event>> hourMultiDayEventMap = normalize(calendar.getHourMultiDayEvents());
            for (Map.Entry<Date, List<Event>> entry : hourMultiDayEventMap.entrySet()) {
                Date key = entry.getKey();
                List<Event> value = entry.getValue();
                for (Iterator<Event> iterator = value.iterator(); iterator.hasNext();) {
                    Event next = iterator.next();
//                            Date theStartEventDate = next.getDtstart();
//                            Date theEndEventDate = next.getDtend();

                    Date theStartEventDateWithTime = next.getDtstart();
                    Date theEndEventDateWithTime = next.getDtend();

                    Date theStartEventDate = new Date(date.getTime());
                    Date theEndEventDate = new Date(date.getTime());

                    theStartEventDate.setYear(theStartEventDateWithTime.getYear());
                    theStartEventDate.setMonth(theStartEventDateWithTime.getMonth());
                    theStartEventDate.setDate(theStartEventDateWithTime.getDate());

                    theEndEventDate.setYear(theEndEventDateWithTime.getYear());
                    theEndEventDate.setMonth(theEndEventDateWithTime.getMonth());
                    theEndEventDate.setDate(theEndEventDateWithTime.getDate());
//<----

                    if (next.getEventType().contains("H")
                            && date.after(theStartEventDate) && date.before(theEndEventDate)
                            || date.equals(theStartEventDate)
                            || date.equals(theEndEventDate)) {
                        hourMultiDayEventCounter++;
                    }
                }
            }
            final List<Event> fullDayEvents = hourMultiDayEventMap.get(date);
            if (fullDayEvents != null) {
                sortedHourMultiDayEvents.addAll(fullDayEvents);
            }
        }
        return hourMultiDayEventCounter;
    }

    private int getFullDayEventCounter(final Calendar calendar, final Date date, int fullDayEventCounter, TreeSet<Event> sortedFullDayEvents) {
        if (calendar.getFullDayEvents() != null) {
            final Map<Date, List<Event>> fullDayEventMap = normalize(calendar.getFullDayEvents());
            for (Map.Entry<Date, List<Event>> entry : fullDayEventMap.entrySet()) {
                Date key = entry.getKey();
                List<Event> value = entry.getValue();
                for (Iterator<Event> iterator = value.iterator(); iterator.hasNext();) {
                    Event next = iterator.next();
                    Date theStartEventDate = next.getDtstart();
                    Date theEndEventDate = next.getDtend();
                    if (next.getEventType().contains("F")
                            && date.after(theStartEventDate) && date.before(theEndEventDate)
                            || date.equals(theStartEventDate)
                            || date.equals(theEndEventDate)) {
                        fullDayEventCounter++;
                    }
                }

            }
            final List<Event> fullDayEvents = fullDayEventMap.get(date);
            if (fullDayEvents != null) {
                sortedFullDayEvents.addAll(fullDayEvents);
            }
        }
        return fullDayEventCounter;
    }

    private static int calculateEventDurationInDays(final Event event) {
        long diff = event.getDtend().getTime() - event.getDtstart().getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        int daysdiff = (int) diffDays;
        return daysdiff;
    }

    /**
     * Returns the first date of the week that includes the given date.
     *
     * @param day A date
     * @param firstDay The first day of the week (such as
     * {@link #SUNDAY}, {@link #MONDAY} or anything else).
     * @return The first date of the week that includes <code>day</day>, as a
     * {@link Date}.
     */
    private static Date getFirstDateOfWeek(Date day, int firstDay) {
        final int decal = (day.getDay() + DAYS_IN_A_WEEK - firstDay) % DAYS_IN_A_WEEK;
        return new Date(day.getYear(), day.getMonth(), day.getDate() - decal);
    }

    /**
     * Calculates the number of the week that includes the given date.
     *
     * @param date A date
     * @param firstDay The first day of the week (such as
     * {@link #SUNDAY}, {@link #MONDAY} or anything else).
     * @return The number of the week that includes <code>date</code>.
     */
    private static int getWeekNumber(Date date, int firstDay) {
        int daysToThursday = 4 - date.getDay();

        if (date.getDay() < firstDay) {
            daysToThursday -= DAYS_IN_A_WEEK;
        }

        final Date thursday = new Date(date.getYear(), date.getMonth(), date.getDate() + daysToThursday);

        final Date januaryFourth = new Date(thursday.getYear(), 0, 4);
        final int daysToMonday = 1 - januaryFourth.getDay(); // Essayer avec le
        // 1er jour de
        // la
        // semaine
        final Date monday = new Date(thursday.getYear(), 0, 4 + daysToMonday);

        final double diff = Math.floor((thursday.getTime() - monday.getTime()) / (1000 * 60 * 60 * 24));
        return (int) Math.ceil(diff / 7.0);
    }

    private void placeItemInGridMonth(Widget panel, int colStart, int colEnd,
            int row, int cellPosition) {
        final FlexTable grid = (FlexTable) getWidget();
        // FlowPanel cell = (FlowPanel) grid.getWidget(1, 1);
        FlowPanel cell = (FlowPanel) grid.getWidget(row, colStart);
        cell.add(panel);
        calculatedCellOffsetHeight = cell.getElement().getClientHeight();//OffsetHeight();

        int height = appointmentHeight();
        height = 12;
        //float width = ((float) (colEnd - colStart) / (float) DAYS_IN_A_WEEK) * 100f - 1f;
        float width = (colEnd - colStart) * 100f;

        float top = 0;

        cell = (FlowPanel) grid.getWidget(row, colStart);

        setFullDayEventStyle(panel, top, width, height);
    }

    private static int appointmentPaddingTop() {
        return 1 + (Math.abs(FormattingUtil.getBorderOffset()) * 3);
    }

    private static int appointmentHeight() {
        // TODO: calculate appointment height dynamically
        return 12;
    }

    /**
     * Display full day events with started <code>date</code>
     *
     * @param row
     * @param column
     * @param date
     */
    private void drawFullDayEvents(int row, int column, final Date date) {

        TreeSet<Event> sortedFullDayEvents = createSortedEventsSet();

        for (final Calendar calendar : calendars) {
            if (!(calendar.getIdentifier() instanceof ActivityCalendarIdentifier)
                    && calendar.getFullDayEvents() != null) {
                final Map<Date, List<Event>> fullDayEventMap = normalize(calendar.getFullDayEvents());
                final List<Event> fullDayEvents = fullDayEventMap.get(date);
                if (fullDayEvents != null) {
                    sortedFullDayEvents.addAll(fullDayEvents);
                }
            }
        }

        final Iterator<Event> iterator = sortedFullDayEvents.iterator();

        eventLimit = 1;//999
        for (int i = 0; iterator.hasNext() && i < eventLimit; i++) {
            final Event event = iterator.next();
            final ClickableFlowPanel flowPanelFullDayFirst = createFullDayPanel(event);
            final ClickableFlowPanel flowPanelFullDayContinue = createFullDayPanel(event);
            final FlexTable grid = (FlexTable) getWidget();
            int daysdiff = calculateEventDurationInDays(event);

            //String theDateString = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_WEEKDAY_DAY).format(thisDate);
            flowPanelFullDayFirst.setTitle(createTitleForFullDayEvent(daysdiff, event));

            if (column + daysdiff - 1 < 8) {
                placeItemInGridMonth(flowPanelFullDayFirst, column, column + daysdiff, row, i);
            } else {
                placeItemInGridMonth(flowPanelFullDayFirst, column, 8, row, i);
                if (row <= 5) {
                    flowPanelFullDayContinue.setTitle(createTitleForFullDayEvent(daysdiff, event));
                    placeItemInGridMonth(flowPanelFullDayContinue, 1, 1 + daysdiff - (8 - column), row + 1, i);
                }
            }
        }
    }

    /**
     * Display full day events with started <code>date</code>
     *
     * @param row
     * @param column
     * @param date
     */
    private void drawHourMultiDayEvents(int row, int column, final Date date) {

        TreeSet<Event> sortedFullDayEvents = createSortedEventsSet();

        for (final Calendar calendar : calendars) {
            if (!(calendar.getIdentifier() instanceof ActivityCalendarIdentifier)
                    && calendar.getFullDayEvents() != null) {
                final Map<Date, List<Event>> fullDayEventMap = normalize(calendar.getHourMultiDayEvents());//EE
                final List<Event> fullDayEvents = fullDayEventMap.get(date);
                if (fullDayEvents != null) {
                    sortedFullDayEvents.addAll(fullDayEvents);
                }
            }
        }

        final Iterator<Event> iterator = sortedFullDayEvents.iterator();

        eventLimit = 1;//999
        for (int i = 0; iterator.hasNext() && i < eventLimit; i++) {
            final Event event = iterator.next();
            final ClickableFlowPanel flowPanelFullDayFirst = createFullDayPanel(event);
            final ClickableFlowPanel flowPanelFullDayContinue = createFullDayPanel(event);
            final FlexTable grid = (FlexTable) getWidget();
            int daysdiff = calculateEventDurationInDays(event);

            //String theDateString = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_WEEKDAY_DAY).format(thisDate);
            flowPanelFullDayFirst.setTitle(createTitleForHourMultiDayEvent(daysdiff, event));//EE3
            DOM.setStyleAttribute(flowPanelFullDayFirst.getElement(), "background-color", "rgba(28,97,217)");
            DOM.setStyleAttribute(flowPanelFullDayFirst.getElement(), "background", "linear-gradient(-90deg, #1c61d9, #0000)");
            if (column + daysdiff - 1 < 8) {
                placeItemInGridMonth(flowPanelFullDayFirst, column, column + daysdiff, row, i);
            } else {
                placeItemInGridMonth(flowPanelFullDayFirst, column, 8, row, i);
                if (row <= 5) {
                    flowPanelFullDayContinue.setTitle(createTitleForHourMultiDayEvent(daysdiff, event));//EE2
                    DOM.setStyleAttribute(flowPanelFullDayContinue.getElement(), "background-color", "rgba(28,97,217)");
                    DOM.setStyleAttribute(flowPanelFullDayContinue.getElement(), "background", "linear-gradient(-90deg, #1c61d9, #0000)");
                    placeItemInGridMonth(flowPanelFullDayContinue, 1, 1 + daysdiff - (8 - column), row + 1, i);
                }
            }
        }
    }

    private static String createTitleForFullDayEvent(int daysdiff, final Event event) {
        return "Event: " + event.getSummary()
                + "\nDescr: " + (event.getDescription() != null ? event.getDescription() : "")
                + "\nDuration: " + daysdiff + " full day" + (daysdiff > 1 ? "s." : ".")
                + (event.getDtstart().equals(event.getDtend())
                ? (DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_WEEKDAY_DAY).format(event.getDtstart()))
                : ("\nFrom " + DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_WEEKDAY_DAY).format(event.getDtstart())
                + " To " + DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_WEEKDAY_DAY).format(event.getDtend())));
    }

    private static String createTitleForHourMultiDayEvent(int daysdiff, final Event event) {
        return "Event: " + event.getSummary()
                + "\nDescr: " + (event.getDescription() != null ? event.getDescription() : "")
                + "\nDuration: " + daysdiff + " day" + (daysdiff > 1 ? "s." : ".")
                + (event.getDtstart().equals(event.getDtend())
                ? (DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_WEEKDAY_DAY).format(event.getDtstart()))
                : ("\nFrom " + DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_WEEKDAY_DAY).format(event.getDtstart())
                + " " + DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.HOUR24_MINUTE).format(event.getDtstart())
                + " \nTo " + DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_WEEKDAY_DAY).format(event.getDtend())
                + " " + DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.HOUR24_MINUTE).format(event.getDtend())));
    }

    /**
     *
     * @param daysdiff the value of daysdiff
     * @param event the value of event
     * @return the java.lang.String
     */
    private String createTitleForHourEvent(int daysdiff, final Event event) {
        String duration = hourFormatter.format(new Date(event.getDtend().getTime() - event.getDtstart().getTime()));
        long millis = event.getDtend().getTime() - event.getDtstart().getTime();
        long minute = (millis / (1000 * 60)) % 60;
        long hour = (millis / (1000 * 60 * 60)) % 24;
        return "Hour Event: " + event.getSummary()
                + "\nDescr: " + (event.getDescription() != null ? event.getDescription() : "")
                + "\nDuration: from " + hourFormatter.format(event.getDtstart())
                + " to " + hourFormatter.format(event.getDtend())
                //               + " (" + duration + " hours" + (daysdiff > 0 ? " every day)" : ")")
                + " (" + (hour > 0 ? (hour + " hours ") : "")
                + (minute > 0 ? (minute + " minutes") : "") + (daysdiff > 1 ? " every day)" : ") ")
                + (daysdiff > 1 ? (daysdiff + " days in row. ") : " ")
                + (isSameDay(event.getDtstart(), event.getDtend())
                ? (DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_WEEKDAY_DAY).format(event.getDtstart()))
                : ("\nFrom " + DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_WEEKDAY_DAY).format(event.getDtstart())
                + " to " + DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH_WEEKDAY_DAY).format(event.getDtend())));
    }

    /**
     * @param event
     * @return
     */
    private ClickableFlowPanel createFullDayPanel(final Event event) {
        final ClickableFlowPanel flowPanel = new ClickableFlowPanel();
        flowPanel.addStyleName("calendar-event");

        boolean fullDayEvent = false;

        final StringBuilder eventDate = new StringBuilder();
        eventDate.append(hourFormatter.format(event.getDtstart()));
        if (event.getDtend() != null) {
            eventDate.append(" ");
            eventDate.append(hourFormatter.format(event.getDtend()));
            fullDayEvent = true;
            flowPanel.addStyleName("calendar-fullday-event");
        }

        final InlineLabel dateLabel = new InlineLabel(eventDate.toString());
        dateLabel.addStyleName("calendar-event-date");

        final InlineLabel eventLabel = new InlineLabel(event.getEventType().contains("H") ? (hourFormatter.format(event.getDtstart()) + " " + event.getSummary()) : event.getSummary());
        //final InlineLabel eventLabel = new InlineLabel(event.getDtstart().getDate() + "; " + (event.getReferenceId()==null?event.getSummary():event.getSummary()) + "+>>");
        eventLabel.addStyleName("calendar-event-label");

        if (fullDayEvent) {
            flowPanel.addStyleName("calendar-fullday-event-" + event.getParent().getStyle());
        } else {
            eventLabel.addStyleName("calendar-event-" + event.getParent().getStyle());
        }

        if (!fullDayEvent) {
            flowPanel.add(dateLabel);
        }
        flowPanel.add(eventLabel);//ak
        //flowPanel.setTitle(event.getSummary());

        final DecoratedPopupPanel detailPopup = new DecoratedPopupPanel(true);

        final Grid popupContent = new Grid(event.getParent().isEditable() ? 6 : 3, 1);
        popupContent.setText(0, 0, event.getSummary());
        popupContent.getCellFormatter().addStyleName(0, 0, "calendar-popup-header");

        if (!fullDayEvent) {
            popupContent.getCellFormatter().addStyleName(1, 0, "calendar-popup-date");
            popupContent.getCellFormatter().addStyleName(1, 0, "calendar-event-" + event.getParent().getStyle());
            popupContent.setText(1, 0, eventDate.toString());
        } else {
            popupContent.setText(1, 0, "");
        }

        if (event.getDescription() != null && !"".equals(event.getDescription())) {
            popupContent.getCellFormatter().addStyleName(2, 0, "calendar-popup-description");
            popupContent.setText(2, 0, event.getDescription());
        } else {
            popupContent.setText(2, 0, "");
        }

        if (event.getParent().isEditable()
                && ProfileUtils.isGranted(authentication, GlobalPermissionEnum.EDIT_PROJECT_AGENDA)) {

            final Anchor editAnchor = new Anchor(I18N.CONSTANTS.calendarEditEvent());
            editAnchor.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent clickEvent) {
                    delegate.edit(event, CalendarWidget.this);
                }
            });

            final Anchor deleteAnchor = new Anchor(I18N.CONSTANTS.calendarDeleteEvent());
            deleteAnchor.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent clickEvent) {
                    delegate.delete(event, CalendarWidget.this);
                    refresh();
                    detailPopup.hide();
                }
            });

            final Anchor deleteChainAnchor = new Anchor("Delete all recurrences");
            deleteChainAnchor.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent clickEvent) {
                    delegate.deleteChain(event, CalendarWidget.this);
                    refresh();
                    detailPopup.hide();

                }
            });

            popupContent.setWidget(3, 0, editAnchor);
            popupContent.setWidget(4, 0, deleteAnchor);
            if (!event.getEventType().contains("O")) {
                popupContent.setWidget(5, 0, deleteChainAnchor);
            }
        }

        detailPopup.setWidget(popupContent);

        flowPanel.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                final int left = flowPanel.getAbsoluteLeft() - 10;
                final int bottom = Window.getClientHeight() - flowPanel.getAbsoluteTop();

                detailPopup.setWidth((getCellWidth(CELL_DEFAULT_WIDTH) + 20) + "px");

                // Show the popup
                detailPopup.setPopupPositionAndShow(new PositionCallback() {

                    @Override
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        detailPopup.getElement().getStyle().setPropertyPx("left", left);
                        detailPopup.getElement().getStyle().setProperty("top", "");
                        detailPopup.getElement().getStyle().setPropertyPx("bottom", bottom);
                    }
                });
            }
        });
        return flowPanel;
    }

    /**
     * Calculates the height of each day cell in the Month grid. It excludes the
     * height of each day's header, as well as the overall header that shows the
     * weekday labels.
     */
//	private void calculateCellHeight() {
//
//		int gridHeight = monthCalendarGrid.getOffsetHeight();
//		int weekdayRowHeight = monthCalendarGrid.getRowFormatter()
//				.getElement(0).getOffsetHeight();
//		int dayHeaderHeight = dayLabels.get(0).getOffsetHeight(); 
//
//		calculatedCellOffsetHeight = (float) (gridHeight - weekdayRowHeight)
//				/ monthViewRequiredRows;
//		calculatedCellHeight = calculatedCellOffsetHeight - dayHeaderHeight;
//		calculatedWeekDayHeaderHeight = weekdayRowHeight;
//		calculatedDayHeaderHeight = dayHeaderHeight;
//	}
    private TreeSet<Event> createSortedEventsSet() {

        final TreeSet<Event> sortedEvents = new TreeSet<Event>(new Comparator<Event>() {

            @Override
            public int compare(Event o1, Event o2) {
                int compare = 0;

                if (o1 == null && o2 == null) {
                    return 0;
                } else if (o2 == null) {
                    return 1;
                } else if (o1 == null) {
                    return -1;
                }

                if (compare == 0 && o1.getDtstart() != null && o2.getDtstart() != null) {
                    long o1Start = o1.getDtstart().getTime();
                    long o2Start = o2.getDtstart().getTime();

                    if (o1Start < o2Start) {
                        compare = -1;
                    } else if (o1Start > o2Start) {
                        compare = 1;
                    }
                }

                if (compare == 0 && o1.getSummary() != null && o2.getSummary() != null) {
                    compare = o1.getSummary().compareTo(o2.getSummary());
                }

                return compare;
            }
        });
        return sortedEvents;
    }

    /**
     * Display full day events with started <code>date</code>
     *
     * @param row
     * @param column
     * @param date
     */
    private void drawFullDayEventsForWeek(int row, int column, final Date date,
            List<Event> alreadyShownWeekViewEvents,
            int[][] theShiftVecorOfFullDayEvents) {

        int theLastFDPanelInCellCounter = getLastFDPanelInCellCounter(theShiftVecorOfFullDayEvents, column);
        // Window.alert("START column = " + column + "; theLastFullRaw =" + theLastFDPanelInCellCounter);
        TreeSet<Event> sortedFullDayEvents = createSortedEventsSet();
        int eventCounter = 0;
        int longFullDayEventShownCounter = 0;

        for (final Calendar calendar : calendars) {
            if (!(calendar.getIdentifier() instanceof ActivityCalendarIdentifier)) {
                if (calendar.getFullDayEvents() != null) {
                    final Map<Date, List<Event>> fullDayEventMap = normalize(calendar.getFullDayEvents());
                    for (Map.Entry<Date, List<Event>> entry : fullDayEventMap.entrySet()) {
                        Date key = entry.getKey();
                        List<Event> value = entry.getValue();
                        for (Iterator<Event> iterator = value.iterator(); iterator.hasNext();) {
                            Event next = iterator.next();
                            if (next.getEventType().contains("F")) {
                                if (!alreadyShownWeekViewEvents.contains(next)) {
                                    Date theStartEventDate = next.getDtstart();
                                    Date theEndEventDate = next.getDtend();
                                    if (date.after(theStartEventDate) && date.before(theEndEventDate)
                                            || date.equals(theStartEventDate)
                                            || date.equals(theEndEventDate)) {
                                        // In between
                                        eventCounter++;
                                        sortedFullDayEvents.add(next);
                                        alreadyShownWeekViewEvents.add(next);
                                    }
                                } else {
                                    Date theStartEventDate = next.getDtstart();
                                    Date theEndEventDate = next.getDtend();
                                    if (date.after(theStartEventDate) && date.before(theEndEventDate)
                                            || date.equals(theStartEventDate)
                                            || date.equals(theEndEventDate)) {

                                        longFullDayEventShownCounter++;
//                                                sortedEvents.add(next);
//                                                alreadyShownWeekViewEvents.add(next);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        final Iterator<Event> iterator = sortedFullDayEvents.iterator();

        int eventLimit = 100;//999
        int counterIvent = 0;
        boolean foundRowToPlace = false;
        int theRowToPlace = 0;
        int last = theLastFDPanelInCellCounter;
        for (int i = 0; iterator.hasNext() && i < eventLimit; i++) {
            last = last + 1;
            final Event event = iterator.next();

            final ClickableFlowPanel fullDayFlowPanel = createFullDayPanel(event);
            final FlexTable grid = (FlexTable) getWidget();
            ///ssss1 grid.setWidget(row, column, flowPanelFullDayFirst);
            //  RootPanel.get().add(flowPanelFullDayFirst);
            // grid.add(flowPanelFullDayFirst);

            Date theStartEventDate = event.getDtstart();
            long startDTForWeekleView = 0;
            long diff = event.getDtend().getTime() - event.getDtstart().getTime();
            if (date.after(theStartEventDate)) {
                //diff = theStartEventDate.getTime() - event.getDtstart().getTime();
                diff = event.getDtend().getTime() - date.getTime();
            }

            long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
            int daysdiff = (int) diffDays;

            fullDayFlowPanel.setTitle(createTitleForFullDayEvent(daysdiff, event));
            //to show full day long event if it longer then 1 week
            while (column + daysdiff - 1 >= 8) {
                daysdiff--;
            }

            if (column + daysdiff - 1 < 8) {
                for (int day = column; day < column + daysdiff; day++) {
                    theShiftVecorOfFullDayEvents[day][last] += 1;
                }
                longFullDayEventShownCounter = theRowToPlace;// + counterIvent;

                int theLastFullDayLinePlace44 = getLastFDPanelInCellCounter(theShiftVecorOfFullDayEvents, column);

                placeItemInGridWeek(fullDayFlowPanel, column, column + daysdiff, counterIvent, theLastFDPanelInCellCounter);

            }
            counterIvent++;
        }

    }

    ///////////////
    /**
     * Display full day events with started <code>date</code>
     *
     * @param row
     * @param column
     * @param date
     */
    private void drawHourMultiDayEventsForWeek(int row, int column, final Date date,
            List<Event> alreadyShownWeekViewEvents,
            int[][] theShiftVecorOfFullDayEvents) {

        int theLastFDPanelInCellCounter = getLastFDPanelInCellCounter(theShiftVecorOfFullDayEvents, column);
        // Window.alert("START column = " + column + "; theLastFullRaw =" + theLastFDPanelInCellCounter);
        TreeSet<Event> sortedFullDayEvents = createSortedEventsSet();
        int eventCounter = 0;
        int longFullDayEventShownCounter = 0;

        for (final Calendar calendar : calendars) {
            if (!(calendar.getIdentifier() instanceof ActivityCalendarIdentifier)) {
                if (calendar.getFullDayEvents() != null) {
                    //final Map<Date, List<Event>> fullDayEventMap = normalize(calendar.getHourMultiDayEvents());//EEE
                    final Map<Date, List<Event>> fullDayEventMap = normalizeHourEvents(calendar.getHourMultiDayEvents());
                    for (Map.Entry<Date, List<Event>> entry : fullDayEventMap.entrySet()) {
                        Date key = entry.getKey();
                        List<Event> value = entry.getValue();
                        for (Iterator<Event> iterator = value.iterator(); iterator.hasNext();) {
                            Event next = iterator.next();
                            ///---->
                            Date theStartEventDateWithTime = next.getDtstart();
                            Date theEndEventDateWithTime = next.getDtend();

                            Date theStartEventDate = new Date(date.getTime());
                            Date theEndEventDate = new Date(date.getTime());

                            theStartEventDate.setYear(theStartEventDateWithTime.getYear());
                            theStartEventDate.setMonth(theStartEventDateWithTime.getMonth());
                            theStartEventDate.setDate(theStartEventDateWithTime.getDate());

                            theEndEventDate.setYear(theEndEventDateWithTime.getYear());
                            theEndEventDate.setMonth(theEndEventDateWithTime.getMonth());
                            theEndEventDate.setDate(theEndEventDateWithTime.getDate());
                            //<----
                            if (next.getEventType().contains("H")) {
                                if (!alreadyShownWeekViewEvents.contains(next)) {
//                                    Date theStartEventDate = next.getDtstart();
//                                    Date theEndEventDate = next.getDtend();
                                    if (date.after(theStartEventDate) && date.before(theEndEventDate)
                                            || date.equals(theStartEventDate)
                                            || date.equals(theEndEventDate)) {
                                        // In between
                                        eventCounter++;
                                        sortedFullDayEvents.add(next);
                                        alreadyShownWeekViewEvents.add(next);
                                    }
                                } else {
//                                    Date theStartEventDate = next.getDtstart();
//                                    Date theEndEventDate = next.getDtend();
                                    if (date.after(theStartEventDate) && date.before(theEndEventDate)
                                            || date.equals(theStartEventDate)
                                            || date.equals(theEndEventDate)) {

                                        longFullDayEventShownCounter++;
//                                                sortedEvents.add(next);
//                                                alreadyShownWeekViewEvents.add(next);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        final Iterator<Event> iterator = sortedFullDayEvents.iterator();

        int eventLimit = 100;//999
        int counterIvent = 0;
        boolean foundRowToPlace = false;
        int theRowToPlace = 0;
        int last = theLastFDPanelInCellCounter;
        for (int i = 0; iterator.hasNext() && i < eventLimit; i++) {
            last = last + 1;
            final Event event = iterator.next();

            final ClickableFlowPanel fullDayFlowPanel = createFullDayPanel(event);
            final FlexTable grid = (FlexTable) getWidget();
            ///ssss1 grid.setWidget(row, column, flowPanelFullDayFirst);
            //  RootPanel.get().add(flowPanelFullDayFirst);
            // grid.add(flowPanelFullDayFirst);

            Date theStartEventDate = event.getDtstart();
            Date theEndEventDate = event.getDtend();

            long startDTForWeekleView = 0;
            Date newDateStart = new Date(date.getTime());//date.getTime()
            Date newDateEnd = new Date(date.getTime());

            newDateStart.setYear(theStartEventDate.getYear());
            newDateStart.setMonth(theStartEventDate.getMonth());//+1
            newDateStart.setDate(theStartEventDate.getDate());

            newDateEnd.setYear(theEndEventDate.getYear());
            newDateEnd.setMonth(theEndEventDate.getMonth());//+1
            newDateEnd.setDate(theEndEventDate.getDate());

            long diff = newDateEnd.getTime() - newDateStart.getTime();
            //  Window.alert("! diff=" + diff);
//            if (date.after(newDateStart)) {
//               //-->> diff = newDateStart.getTime() - newDateEnd.getTime();
//               diff = 8 - diff;
//                Window.alert("date.after(newDateStart)  2 diff=" + diff);
//            }


            /*           
            long diff = event.getDtend().getTime() - event.getDtstart().getTime();
            if (date.after(theStartEventDate)) {
                diff = theStartEventDate.getTime() - event.getDtstart().getTime();
            }
             */
            if (date.after(newDateStart)) {
                //-->> diff = newDateStart.getTime() - newDateEnd.getTime();
                diff = newDateEnd.getTime() - date.getTime();
                //Window.alert("date.after(newDateStart)");
            }
            long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
            int daysdiff = (int) diffDays;
            //Window.alert("date.after(newDateStart)  2 daysdiff=" + daysdiff);

            //  Window.alert("3 daysdiff=" + daysdiff);
            //---->>     
            //-->> daysdiff = calculateEventDurationInDays(event);
            //Window.alert("@@@333 daysdiff=" + daysdiff + " column=" + column);

            fullDayFlowPanel.setTitle(createTitleForHourMultiDayEvent(daysdiff, event));
            //to show full day long event if it longer then 1 week
            while (column + daysdiff - 1 >= 8) {
                //  Window.alert("4 daysdiff=" + daysdiff);
                daysdiff--;
            }
            //Window.alert("5 daysdiff=" + daysdiff);
            if (column + daysdiff - 1 < 8) {
                for (int day = column; day < column + daysdiff; day++) {
                    theShiftVecorOfFullDayEvents[day][last] += 1;
                }
                longFullDayEventShownCounter = theRowToPlace;// + counterIvent;

                int theLastFullDayLinePlace44 = getLastFDPanelInCellCounter(theShiftVecorOfFullDayEvents, column);
                // Window.alert("6 column=" + column);
                placeItemInGridWeek(fullDayFlowPanel, column, column + daysdiff, counterIvent, theLastFDPanelInCellCounter);

            } else {
                for (int day = 1; day < 1 + daysdiff - (8 - column); day++) {
                    theShiftVecorOfFullDayEvents[day][last] += 1;
                }
                longFullDayEventShownCounter = theRowToPlace;// + counterIvent;

                int theLastFullDayLinePlace44 = getLastFDPanelInCellCounter(theShiftVecorOfFullDayEvents, 1);
                // Window.alert("6 column=" + column);
                placeItemInGridWeek(fullDayFlowPanel, 1, 1 + daysdiff - (8 - column), counterIvent, theLastFDPanelInCellCounter);

            }
            counterIvent++;
        }

    }
    ////////////

    private void placeItemInGridWeek(Widget panel, int colStart, int colEnd,
            int row, int cellPosition) {
        final FlexTable grid = (FlexTable) getWidget();
        //sss1FlowPanel cell = (FlowPanel) grid.getWidget(1, 1);
        FlowPanel cell = (FlowPanel) grid.getWidget(1, colStart);//row
        cell.add(panel);//sssss 111
//        calculatedCellOffsetHeight = cell.getElement().getClientHeight();//OffsetHeight();
//        float calculatedCellOffsetWidth = cell.getElement().getOffsetWidth();;
//        NumberFormat percFormat = NumberFormat.getFormat(".");
//
//        int paddingTop = appointmentPaddingTop() + 3;
        int height = appointmentHeight();//12
        //float left = (float) colStart / (float) DAYS_IN_A_WEEK * 100f + .5f
        //float left = (float) colStart / (float) DAYS_IN_A_WEEK * 50f + .5f + cellPosition * 5;
        //float width = ((float) (colEnd - colStart) / (float) DAYS_IN_A_WEEK) * 100f - 1f;
        float width = (colEnd - colStart) * 100f;
//        float top = (row * (height + paddingTop)) + cellPosition * 3;
        float top = cellPosition * height + cellPosition * 2;

        setFullDayEventStyle(panel, top, width, height);
    }

    private void setFullDayEventStyle(Widget panel, float top, float width, int height) {
        //final FlexTable grid = (FlexTable) getWidget();
        //cell = (FlowPanel) grid.getWidget(row, colStart);
        //cell = (FlowPanel) grid.getWidget(1, colStart);

        DOM.setStyleAttribute(panel.getElement(), "position", "relative");
        DOM.setStyleAttribute(panel.getElement(), "top", top + "px");
        DOM.setStyleAttribute(panel.getElement(), "width", width + "%");
        DOM.setStyleAttribute(panel.getElement(), "height", height + "px");
        DOM.setStyleAttribute(panel.getElement(), "border-radius", "20px 20px 20px 20px");
        DOM.setStyleAttribute(panel.getElement(), "border-color", "#1c61d9");
        DOM.setStyleAttribute(panel.getElement(), "border-style", "solid");
        DOM.setStyleAttribute(panel.getElement(), "border-width", "1px");
        DOM.setStyleAttribute(panel.getElement(), "background-color", "rgba(28,97,217)");
        //   DOM.setStyleAttribute(panel.getElement(), "background", "linear-gradient(-90deg, #0000, #1c61d9)");
    }
}
