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

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import org.sigmah.client.ui.view.zone.MessageBannerView;
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
		WEEK(7, 1) {

			@Override
			public Date getStartDate(Date date, int firstDay) {
				return getFirstDateOfWeek(date, firstDay);
			}

			@Override
			public void nextDate(Date currentDate) {
				currentDate.setDate(currentDate.getDate() + 7);
			}

			@Override
			public void previousDate(Date currentDate) {
				currentDate.setDate(currentDate.getDate() - 7);
			}

			@Override
			public void firstDay(Date currentDate, Date today, int firstDay) {
				int decal = (today.getDay() + 7 - firstDay) % 7;
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
		grid.addStyleName("calendar");
		grid.addStyleName(displayMode.getStyleName());

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
		this.calendars = calendars;
		refresh();
	}

	/**
	 * Defines the formatter used to display the title of the calendar.<br>
	 * <br>
	 * The default format is "<code>MonthName</code> <code>FullYear</code> " (pattern : "N y").
	 * 
	 * @param titleFormatter
	 *          The formatter to use to display the title of the calendar.
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
	 * @param headerFormatter
	 *          The formatter to use to display the title of each column.
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
	 * @param dayFormatter
	 *          The formatter to use to display the title of each cell.
	 */
	public void setDayFormatter(DateTimeFormat dayFormatter) {
		this.dayFormatter = dayFormatter;
		refresh();
	}

	/**
	 * Defines the display mode of the calendar and perform a redraw.
	 * 
	 * @param displayMode
	 *          Style of the calendar (day, week or month).
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
	 * @param firstDayOfWeek
	 *          The first day of the week as an int (Sunday = 0, Saturday = 6)
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
	 * Removes all rows. Must be when the structure of the calendar has been changed (display mode)
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
	 * Normalizes the given {@code calendar}'s events map (needed particularly when there is a timezone difference between
	 * the client and the server).
	 * 
	 * @param calendar
	 *          The calendar instance.
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

	/**
	 * Calculates the number of events that can be displayed in a cell.
	 */
	public void calibrateCalendar() {
		final FlexTable grid = (FlexTable) getWidget();

		final Element row = grid.getRowFormatter().getElement(displayHeaders);
		row.setId("calendar-row-calibration");

		final Element cell = grid.getCellFormatter().getElement(displayHeaders, displayWeekNumber ? 1 : 0);
		cell.setId("calendar-cell-calibration");

		eventLimit = (getCellHeight(CELL_DEFAULT_HEIGHT) / EVENT_HEIGHT) - 2;
		if (eventLimit < 0)
			eventLimit = 0;
	}

	/**
	 * Calculates the height of the cell identified by "calendar-cell-calibration".
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
	 * Calculates the width of the cell identified by "calendar-cell-calibration".
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
                            //
              final FlexTable grid = (FlexTable) getWidget();
                grid.removeAllRows();
                //
		drawEmptyCells();

		if (isAttached()) {
			calibrateCalendar();
                        
   /*                     
	//	final FlexTable grid = (FlexTable) getWidget();
        Window.alert("create pageMessagePanel2");
        final FlowPanel cell = (FlowPanel) grid.getWidget(1, 1);
        ///
                final FlowPanel pageMessagePanel2 = new FlowPanel();
		pageMessagePanel2.addStyleName("calendar-fullday-event-1");
		pageMessagePanel2.setVisible(true);
                pageMessagePanel2.setWidth(String.valueOf(cell.getOffsetWidth()*5) + "px");
               // pageMessagePanel.setWidth(String.valueOf(cell.getOffsetHeight()/2) + "px");
                //pageMessagePanel.setHeight("10");
		HTML pageMessageLabel2 = new HTML();
		pageMessageLabel2.addStyleName("calendar-event-label");
                pageMessageLabel2.setHTML("==calendar-event-label==>");
		pageMessagePanel2.add(pageMessageLabel2);
                //RootPanel.getBodyElement().appendChild(pageMessagePanel2);
                RootPanel.get().add(pageMessagePanel2);
                grid.setWidget(1, 1, pageMessagePanel2);
                ////
            // Window.alert("call placeItemInGrid2");   
  //----------- ORABGE TEST             
  placeItemInGrid2(pageMessagePanel2, 1, 5,2, 2);
   */             
			drawEvents();
		}
		if (listener != null)
			listener.afterRefresh();
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
                
		for (int y = displayHeaders; y < rows; y++) {
			for (int x = displayWeekNumber ? 1 : 0; x < columns; x++) {

				drawEvents(y, x, date);
                            if (displayMode.equals(displayMode.MONTH)){
                                drawFullDayEvents(y, x, date);
                            }
				date.setDate(date.getDate() + 1);
			}
		}
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
			// New cell
			cell = new FlowPanel();
			cell.setWidth("100%");

			grid.setWidget(row, column, cell);

		} else {
			// Reusing an existing cell
			cell.clear();
		}

		if (currentMonth != date.getMonth())
			grid.getCellFormatter().addStyleName(row, column, "calendar-cell-other-month");
		if (date.equals(today))
			grid.getCellFormatter().addStyleName(row, column, "calendar-cell-today");

		cell.add(header);
	}
	private void placeItemInGrid(Widget panel, int colStart, int colEnd,
			int row, int cellPosition) {
		int paddingTop = 10 + 3;
		int height = 20;

		float left = (float) colStart / (float) 7 * 100f + .5f;

		float width = ((float) (colEnd - colStart + 1) / (float) 7) * 100f - 1f;

		float top = 100
				+ (row * 5)
				+ 25 + paddingTop
				+ (cellPosition * (height + paddingTop));
//		 System.out.println( calculatedWeekDayHeaderHeight + " + (" + row +
//		 " * " + calculatedCellOffsetHeight + ") + " +
//		 calculatedDayHeaderHeight + " + " + paddingTop + " + (" +
//		 cellPosition+"*("+height+"+"+paddingTop + "));");

		DOM.setStyleAttribute(panel.getElement(), "position", "absolute");
		DOM.setStyleAttribute(panel.getElement(), "top", top + "px");
		DOM.setStyleAttribute(panel.getElement(), "left", left + "%");
		DOM.setStyleAttribute(panel.getElement(), "width", width + "%");
	}
	/**
	 * Display the events for the cell located at <code>column</code>, <code>row</code>
	 * 
	 * @param row
	 * @param column
	 * @param date
	 * @param currentMonth
	 */
	private void drawEvents(int row, int column, final Date date) {
		final FlexTable grid = (FlexTable) getWidget();

		// final VerticalPanel cell = (VerticalPanel) grid.getWidget(row,
		// column);
		final FlowPanel cell = (FlowPanel) grid.getWidget(row, column);

		if (cell == null)
			throw new NullPointerException("The specified cell (" + row + ',' + column + ") doesn't exist.");

		// Displaying events
		final TreeSet<Event> sortedHourEvents = new TreeSet<Event>(new Comparator<Event>() {

			@Override
			public int compare(Event o1, Event o2) {
				int compare = 0;

				if (o1 == null && o2 == null)
					return 0;
				else if (o2 == null)
					return 1;
				else if (o1 == null)
					return -1;

				if (compare == 0 && o1.getDtstart() != null && o2.getDtstart() != null) {
					long o1Start = o1.getDtstart().getTime();
					long o2Start = o2.getDtstart().getTime();

					if (o1Start < o2Start)
						compare = -1;
					else if (o1Start > o2Start)
						compare = 1;
				}

				if (compare == 0 && o1.getSummary() != null && o2.getSummary() != null)
					compare = o1.getSummary().compareTo(o2.getSummary());

				return compare;
			}
		});

                // Displaying full day events
		final TreeSet<Event> sortedFullDayEvents = new TreeSet<Event>(new Comparator<Event>() {

			@Override
			public int compare(Event o1, Event o2) {
				int compare = 0;

				if (o1 == null && o2 == null)
					return 0;
				else if (o2 == null)
					return 1;
				else if (o1 == null)
					return -1;

				if (compare == 0 && o1.getDtstart() != null && o2.getDtstart() != null) {
					long o1Start = o1.getDtstart().getTime();
					long o2Start = o2.getDtstart().getTime();

					if (o1Start < o2Start)
						compare = -1;
					else if (o1Start > o2Start)
						compare = 1;
				}

				if (compare == 0 && o1.getSummary() != null && o2.getSummary() != null)
					compare = o1.getSummary().compareTo(o2.getSummary());

				return compare;
			}
		});
                
		for (final Calendar calendar : calendars) {
                        if (!(calendar.getIdentifier() instanceof ActivityCalendarIdentifier)) {
                            if(calendar.getEvents()!=null){
                                final Map<Date, List<Event>> eventMap = normalize(calendar.getEvents());
                                final List<Event> events = eventMap.get(date);
                                if (events != null) {
                                        sortedHourEvents.addAll(events);
                                }
                            }
                            if(calendar.getFullDayEvents()!=null){
                                final Map<Date, List<Event>> fullDayEventMap = normalize(calendar.getFullDayEvents());
                                final List<Event> fullDayEvents = fullDayEventMap.get(date);
                                if (fullDayEvents != null) {
                                        sortedFullDayEvents.addAll(fullDayEvents);
                                }
                            }
                        }

		}

		final Iterator<Event> iterator = sortedHourEvents.iterator();
                
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

				if (event.getDtstart().getDate() != event.getDtend().getDate()
					|| event.getDtstart().getMonth() != event.getDtend().getMonth()
					|| event.getDtstart().getYear() != event.getDtend().getYear()) {
					fullDayEvent = true;
					flowPanel.addStyleName("calendar-fullday-event");
				}
			}

			final InlineLabel dateLabel = new InlineLabel(eventDate.toString());
			dateLabel.addStyleName("calendar-event-date");

			//final InlineLabel eventLabel = new InlineLabel(event.getReferenceId()==null?event.getSummary():"--->>");
			final InlineLabel eventLabel = new InlineLabel(row + "; " + event.getReferenceId()==null?event.getSummary():event.getSummary() + "->>");
                        eventLabel.addStyleName("calendar-event-label");

			if (fullDayEvent)
				flowPanel.addStyleName("calendar-fullday-event-" + event.getParent().getStyle());
			else
				eventLabel.addStyleName("calendar-event-" + event.getParent().getStyle());

			if (!fullDayEvent)
				flowPanel.add(dateLabel);
			flowPanel.add(eventLabel);

			final DecoratedPopupPanel detailPopup = new DecoratedPopupPanel(true);

			final Grid popupContent = new Grid(event.getParent().isEditable() ? 6 : 3, 1);
			popupContent.setText(0, 0, event.getSummary());
			popupContent.getCellFormatter().addStyleName(0, 0, "calendar-popup-header");

			if (!fullDayEvent) {
				popupContent.getCellFormatter().addStyleName(1, 0, "calendar-popup-date");
				popupContent.getCellFormatter().addStyleName(1, 0, "calendar-event-" + event.getParent().getStyle());
				popupContent.setText(1, 0, eventDate.toString());
			} else
				popupContent.setText(1, 0, "");

			if (event.getDescription() != null && !"".equals(event.getDescription())) {
				popupContent.getCellFormatter().addStyleName(2, 0, "calendar-popup-description");
				popupContent.setText(2, 0, event.getDescription());
			} else
				popupContent.setText(2, 0, "");

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

				final Anchor deleteChainAnchor = new Anchor("Delete chain");
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
                                popupContent.setWidget(5, 0, deleteChainAnchor);
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

			cell.add(flowPanel);        
		}

		if (eventLimit != UNDEFINED && sortedHourEvents.size() > eventLimit) {
			final Anchor eventLabel = new Anchor("\u25BC +" + (sortedHourEvents.size()-1) + " events");
                        eventLabel.setTitle("Click to view all events of the day.");
			final Date thisDate = new Date(date.getTime());
			eventLabel.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					startDate = thisDate;
					setDisplayMode(DisplayMode.WEEK);
				}
			});
			eventLabel.addStyleName("calendar-event-limit");
			cell.add(eventLabel);
		}
	}

	/**
	 * Returns the first date of the week that includes the given date.
	 * 
	 * @param day
	 *          A date
	 * @param firstDay
	 *          The first day of the week (such as {@link #SUNDAY}, {@link #MONDAY} or anything else).
	 * @return The first date of the week that includes <code>day</day>, as a {@link Date}.
	 */
	private static Date getFirstDateOfWeek(Date day, int firstDay) {
		final int decal = (day.getDay() + 7 - firstDay) % 7;
		return new Date(day.getYear(), day.getMonth(), day.getDate() - decal);
	}

	/**
	 * Calculates the number of the week that includes the given date.
	 * 
	 * @param date
	 *          A date
	 * @param firstDay
	 *          The first day of the week (such as {@link #SUNDAY}, {@link #MONDAY} or anything else).
	 * @return The number of the week that includes <code>date</code>.
	 */
	private static int getWeekNumber(Date date, int firstDay) {
		int daysToThursday = 4 - date.getDay();

		if (date.getDay() < firstDay)
			daysToThursday -= 7;

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
        
        	private void placeItemInGrid2(Widget panel, int colStart, int colEnd,
			int row, int cellPosition) {
                    final FlexTable grid = (FlexTable) getWidget();
                     FlowPanel cell = (FlowPanel) grid.getWidget(1, 1);
                    calculatedCellOffsetHeight = cell.getElement().getClientHeight();//OffsetHeight();
                    float calculatedCellOffsetWidth = cell.getElement().getOffsetWidth();;
                    NumberFormat percFormat = NumberFormat.getFormat(".");
                    
		int paddingTop = appointmentPaddingTop() + 3;
		int height = appointmentHeight();
//float left = (float) colStart / (float) DAYS_IN_A_WEEK * 100f + .5f
		float left = (float) colStart / (float) DAYS_IN_A_WEEK * 50f + .5f + cellPosition*5;

		float width = ((float) (colEnd - colStart) / (float) DAYS_IN_A_WEEK) * 100f - 1f;
                float width33 = (colEnd - colStart)* 100f;
 //////////////////////////              
int cellLeft= cell.getElement().getAbsoluteLeft();
int cellRight= cell.getElement().getAbsoluteRight();
int cellTop= cell.getElement().getAbsoluteTop();
int cellBottom= cell.getElement().getAbsoluteBottom();
int cellWidth = cell.getElement().getOffsetWidth();
/*
Window.alert("cellWidth=" + cellWidth+ "; cellRight=" +  cellRight + ";cellLeft=" +  cellLeft  
        +";cellTop=" +  cellTop);
*/
int width2 = ((colEnd - colStart)+1) * cellWidth/7;//(cellRight-cellLeft);
int left2 = colStart * cellLeft;

        ////////////////////
//		float top = calculatedWeekDayHeaderHeight
//				+ (row * calculatedCellOffsetHeight)
//				+ calculatedDayHeaderHeight + paddingTop
//				+ (cellPosition * (height + paddingTop));
float top = (row * (height + paddingTop))+ cellPosition * 3;

//		final FlexTable grid = (FlexTable) getWidget();

cell = (FlowPanel) grid.getWidget(row, colStart);
float topNew = ( (cellBottom - cellTop)/6 * row );
 top = cell.getAbsoluteTop();// x row
top = 10;
//Window.alert("panel.getElement().getId()" + panel.getElement().getId());
//Window.alert("panel.getElement().getId()" + panel.getElement().getClassName());
/*
Window.alert("row=" + row+ "; top=" +  top + ";left=" +  percFormat.format(left)  +";width=" +   percFormat.format(width));
Window.alert("width33=" + width33+ "; topNew=" +  topNew + ";left2=" +  left2  +";width2=" +  width2);
*/
//Window.alert("calculatedWeekDayHeaderHeight=" +  calculatedWeekDayHeaderHeight 
//        + "; -->calculatedCellOffsetHeight=" +  calculatedCellOffsetHeight  
//        +"; height=" +  height);

//Window.alert( calculatedWeekDayHeaderHeight + " + (" + row +
//" * " + calculatedCellOffsetHeight + ") + " +
//calculatedDayHeaderHeight + " + " + paddingTop + " + (" +
//cellPosition+"*("+height+"+"+paddingTop + "));");


//		final FlexTable grid = (FlexTable) getWidget();
//
//		final FlowPanel cell = (FlowPanel) grid.getWidget(row, column);
                
		//DOM.setStyleAttribute(panel.getElement(), "position", "absolute");
                DOM.setStyleAttribute(panel.getElement(), "position", "relative");
		DOM.setStyleAttribute(panel.getElement(), "top", top + "px");
               // DOM.setStyleAttribute(panel.getElement(), "top", topNew + "%");
//		DOM.setStyleAttribute(panel.getElement(), "left", left + "%");
		DOM.setStyleAttribute(panel.getElement(), "width", width33 + "%");
		//DOM.setStyleAttribute(panel.getElement(), "left2", left2 + "px");
		//DOM.setStyleAttribute(panel.getElement(), "width2", width2 + "px");
                DOM.setStyleAttribute(panel.getElement(), "border-radius" ,"20px 20px 20px 20px");
                DOM.setStyleAttribute(panel.getElement(), "border-color" ,"#EE9329");
                DOM.setStyleAttribute(panel.getElement(), "border-style" ,"solid");
                DOM.setStyleAttribute(panel.getElement(), "border-width" ,"2px");
         
	}
        private static int appointmentPaddingTop() {
		return 1 + (Math.abs(FormattingUtil.getBorderOffset()) * 3);
	}

	private static int appointmentHeight() {
		// TODO: calculate appointment height dynamically
		return 40;
	}
            /**
     * Multiple calculated (&quot;cached&quot;) values reused during
     * laying out the month view elements.
     */
	private int DAYS_IN_A_WEEK = 7;
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
        
        /**
	 * Display full day events with started <code>date</code>
	 * 
	 * @param row
	 * @param column
	 * @param date
	 */
	private void drawFullDayEvents(int row, int column,final Date date) {

                // Displaying full day events
		final TreeSet<Event> sortedFullDayEvents = new TreeSet<Event>(new Comparator<Event>() {

			@Override
			public int compare(Event o1, Event o2) {
				int compare = 0;

				if (o1 == null && o2 == null)
					return 0;
				else if (o2 == null)
					return 1;
				else if (o1 == null)
					return -1;

				if (compare == 0 && o1.getDtstart() != null && o2.getDtstart() != null) {
					long o1Start = o1.getDtstart().getTime();
					long o2Start = o2.getDtstart().getTime();

					if (o1Start < o2Start)
						compare = -1;
					else if (o1Start > o2Start)
						compare = 1;
				}

				if (compare == 0 && o1.getSummary() != null && o2.getSummary() != null)
					compare = o1.getSummary().compareTo(o2.getSummary());

				return compare;
			}
		});
                
		for (final Calendar calendar : calendars) {
                        if (!(calendar.getIdentifier() instanceof ActivityCalendarIdentifier)) {
                            if(calendar.getFullDayEvents()!=null){
                                final Map<Date, List<Event>> fullDayEventMap = normalize(calendar.getFullDayEvents());
                                final List<Event> fullDayEvents = fullDayEventMap.get(date);
                                if (fullDayEvents != null) {
                                        sortedFullDayEvents.addAll(fullDayEvents);
                                }
                            }
                        }

		}

		final Iterator<Event> iterator = sortedFullDayEvents.iterator();
               //ak 
               eventLimit = 1;//999
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

//--				if (event.getDtstart().getDate() != event.getDtend().getDate()
//--					|| event.getDtstart().getMonth() != event.getDtend().getMonth()
//--					|| event.getDtstart().getYear() != event.getDtend().getYear()) {
					fullDayEvent = true;
					flowPanel.addStyleName("calendar-fullday-event");
//--				}
			}

			final InlineLabel dateLabel = new InlineLabel(eventDate.toString());
			dateLabel.addStyleName("calendar-event-date");

			final InlineLabel eventLabel = new InlineLabel(event.getSummary());
			//final InlineLabel eventLabel = new InlineLabel(event.getDtstart().getDate() + "; " + (event.getReferenceId()==null?event.getSummary():event.getSummary()) + "+>>");
                        eventLabel.addStyleName("calendar-event-label");

			if (fullDayEvent)
				flowPanel.addStyleName("calendar-fullday-event-" + event.getParent().getStyle());
			else
				eventLabel.addStyleName("calendar-event-" + event.getParent().getStyle());

			if (!fullDayEvent)
				flowPanel.add(dateLabel);
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
			} else
				popupContent.setText(1, 0, "");

			if (event.getDescription() != null && !"".equals(event.getDescription())) {
				popupContent.getCellFormatter().addStyleName(2, 0, "calendar-popup-description");
				popupContent.setText(2, 0, event.getDescription());
			} else
				popupContent.setText(2, 0, "");

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

				final Anchor deleteChainAnchor = new Anchor("Delete chain");
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
                                popupContent.setWidget(5, 0, deleteChainAnchor);
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

//---			cell.add(flowPanel); 
               // RootPanel.get().add(pageMessagePanel2);
              final FlexTable grid = (FlexTable) getWidget();
            grid.setWidget(row, column, flowPanel);
           //  RootPanel.get().add(flowPanel);
            // grid.add(flowPanel);
                

                long diff = event.getDtend().getTime() - event.getDtstart().getTime();
                long diffDays = diff / (24 * 60 * 60 * 1000)+1;
                int daysdiff = (int) diffDays;
 //--+ Window.alert("eventLabel=" + eventLabel.getTitle() +"; " + eventLabel.getText() + ";COLUMN=" +column+ "; DAYS = " + daysdiff);
 //Window.alert("column =" + column + "; daysdiff=" + daysdiff );
 if(column + daysdiff-1 < 8){
     //--+ Window.alert("Short");
     placeItemInGrid2(flowPanel, column, column + daysdiff, row, i);
 }else{
    //--+  Window.alert("Long1");
    //  Window.alert("daysdiff - (7-column)="+ (daysdiff - (7-column)));
     placeItemInGrid2(flowPanel, column, 8, row, i);
      ClickableFlowPanel flowPanel2 = new ClickableFlowPanel();
      flowPanel2.addStyleName("calendar-fullday-event-2");// + event.getParent().getStyle());
     //flowPanel2 = flowPanel;
     final InlineLabel dateLabel2 = new InlineLabel("<<--");// + event.getSummary());
     flowPanel2.add(dateLabel2);
     grid.setWidget(row+1, 1, flowPanel2);
    //--+  Window.alert("Long2");
    //--+ Window.alert("daysdiff - (8-column)="+ (daysdiff - (8-column)));
     placeItemInGrid2(flowPanel2, 1,1+ daysdiff - (8-column), row+1, i);
 }
// private void placeItemInGrid2(Widget panel, int colStart, int colEnd,int row, int cellPosition) {
		}
/*
		if (eventLimit != UNDEFINED && sortedHourEvents.size() > eventLimit) {
			final Anchor eventLabel = new Anchor("\u25BC +" + (sortedHourEvents.size()-1) + " events");
                        eventLabel.setTitle("Click to view all events of the day.");
			final Date thisDate = new Date(date.getTime());
			eventLabel.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					startDate = thisDate;
					setDisplayMode(DisplayMode.WEEK);
				}
			});
			eventLabel.addStyleName("calendar-event-limit");
			cell.add(eventLabel);
		}
                */
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

}
