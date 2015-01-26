package org.sigmah.client.ui.view.calendar;

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
import com.google.gwt.user.client.ui.SimplePanel;

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
		calendarColor.setStyle("");
		calendarColor.setRenderer(new GridCellRenderer<CalendarWrapper>() {

			@Override
			public Object render(CalendarWrapper model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<CalendarWrapper> store,
					Grid<CalendarWrapper> grid) {

				final SimplePanel panel = new SimplePanel();
				panel.setPixelSize(14, 14);
				panel.getElement().getStyle().setMarginLeft(3, Unit.PX);

				panel.setStyleName("calendar-fullday-event-" + model.getCalendar().getStyle());
				return panel;
			}
		});

		selectionModel = new CheckBoxSelectionModel<CalendarWrapper>();

		final ColumnModel calendarColumnModel = new ColumnModel(Arrays.asList(selectionModel.getColumn(), calendarName, calendarColor));

		calendarsStore = new ListStore<CalendarWrapper>();

		final Grid<CalendarWrapper> calendarGrid = new Grid<CalendarWrapper>(calendarsStore, calendarColumnModel);
		calendarGrid.setAutoExpandColumn("name");
		calendarGrid.setSelectionModel(selectionModel);
		calendarGrid.addPlugin(selectionModel);

		calendarGrid.getView().setForceFit(true);

		calendarsPanel.add(calendarGrid);
		return calendarsPanel;
	}

	/**
	 * Creates the calendars main panel, place holder for the {@link CalendarWidget}.
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

		calendarView.setTopComponent(toolbar);

		return calendarView;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeCalendarWidget(final CalendarWidget calendarWidget) {

		// Defining the first day of the week
		// LocaleInfo uses 1 for Sunday and 2 for Monday. Substracting 1 since
		// Date starts with 0 for Sunday.
		final DateTimeFormatInfo constants = LocaleInfo.getCurrentLocale().getDateTimeFormatInfo();
		calendarWidget.setFirstDayOfWeek(constants.firstDayOfTheWeek() - 1);

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
		}

		if (addEventButtonEnabled) {
			toolbar.add(addEventButton);
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
