package org.sigmah.client.ui.presenter.calendar;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.calendar.CalendarEventView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.calendar.CalendarWrapper;
import org.sigmah.shared.dto.calendar.Event;
import org.sigmah.shared.dto.calendar.PersonalEventDTO;
import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.google.gwt.user.client.Window;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import static java.lang.Integer.parseInt;

/**
 * Calendar event presenter which manages the {@link CalendarEventView}.
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class CalendarEventPresenter extends AbstractPagePresenter<CalendarEventPresenter.View> {

    private Event event;
    private CalendarWrapper calendarWrapper;

    /**
     * Description of the view managed by this presenter.
     */
    @ImplementedBy(CalendarEventView.class)
    public static interface View extends ViewInterface {

        /**
         *
         * @return
         */
        FormPanel getForm();

        /**
         *
         * @return
         */
        TextField<String> getEventSummaryField();

        /**
         *
         * @return
         */
        DateField getEventDateStartField();

        /**
         *
         * @return
         */
        DateField getEventDateEndField();

        /**
         *
         * @return
         */
        TimeField getEventStartTimeField();

        /**
         *
         * @return
         */
        TimeField getEventEndTimeField();

        /**
         *
         * @return
         */
        TextArea getEventDescriptionField();

        /**
         *
         * @return
         */
        Button getSaveButton();

        /**
         *
         * @return
         */
        Button getCancelButton();

        /**
         *
         * @return
         */
        CheckBox getAllDayCheckbox();

        /**
         *
         * @return
         */
        FieldSet getPanelYearly();

        /**
         *
         * @return
         */
        FieldSet getPanelMonthly();

        /**
         *
         * @return
         */
        FieldSet getPanelWeekly();

        /**
         *
         * @return
         */
        FieldSet getPanelDaily();

        /**
         *
         * @return
         */
        FieldSet getMonthlyRepSettings();

        /**
         *
         * @return
         */
        FieldSet getYearlyRepSettings();

        /**
         *
         * @return
         */
        Radio getOnceRepeatRB();

        /**
         *
         * @return
         */
        Radio getDailyRepeatRB();

        /**
         *
         * @return
         */
        Radio getWeeklyRepeatRB();

        /**
         *
         * @return
         */
        Radio getMonthlyRepeatRB();

        /**
         *
         * @return
         */
        Radio getYearlyRepeatRB();

        /**
         *
         * @return
         */
        RadioGroup getYearlyVariantRG();

        /**
         *
         * @return
         */
        RadioGroup getMontlyVariantRG();

        /**
         *
         * @return
         */
        Radio getYearlySameDayOfWeekRB();

        /**
         *
         * @return
         */
        Radio getYearlySameDateRB();

        /**
         *
         * @return
         */
        RadioGroup getRepeatEventPeriodRG();

        /**
         *
         * @return
         */
        RadioGroup getRepeatMultiEventPeriodRG();

        /**
         *
         * @return
         */
        Radio getRadioMonthlySameDate();

        /**
         *
         * @return
         */
        Radio getRadioMonthlySameDayOfWeek();

        /**
         *
         * @return
         */
        Radio getRadioRepetitionEndDate();

        /**
         *
         * @return
         */
        Radio getRadioNumberOfRepetitions();

        /**
         *
         * @return
         */
        Radio getDailyRadioRepetitionEndDate();

        /**
         *
         * @return
         */
        Radio getDailyRadioNumberOfRepetitions();

        /**
         *
         * @return
         */
        Radio getWeeklyRadioRepetitionEndDate();

        /**
         *
         * @return
         */
        Radio getWeeklyRadioNumberOfRepetitions();

        /**
         *
         * @return
         */
        Radio getYearlyRadioRepetitionEndDate();

        /**
         *
         * @return
         */
        Radio getYearlyRadioNumberOfRepetitions();

        /**
         *
         * @return
         */
        TextArea getNumberOfRepetitions();

        /**
         *
         * @return
         */
        DateField getRepetitionEndDate();

        /**
         *
         * @return
         */
        TextArea getWeeklyNumberOfRepetitions();

        /**
         *
         * @return
         */
        DateField getWeeklyRepetitionEndDate();

        /**
         *
         * @return
         */
        TextArea getYearlyNumberOfRepetitions();

        /**
         *
         * @return
         */
        DateField getYearlyRepetitionEndDate();

        /**
         *
         * @return
         */
        TextArea getDailyNumberOfRepetitions();

        /**
         *
         * @return
         */
        DateField getDailyRepetitionEndDate();

        // void setShowAddEventView(boolean showAddEventView);
    }

    /**
     * The edited calendar event, or {@code null} if creation.
     */
    /**
     * Presenters's initialization.
     *
     * @param view Presenter's view interface.
     * @param injector Injected client injector.
     */
    @Inject
    public CalendarEventPresenter(final View view, final Injector injector) {
        super(view, injector);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page getPage() {
        return Page.CALENDAR_EVENT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBind() {

        // --
        // Cancel button listener.
        // --
        view.getCancelButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(final ButtonEvent ce) {
                hideView();
            }
        });

        // --
        // Save button listener.
        // --
        view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(final ButtonEvent ce) {
                onSaveAction();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPageRequest(final PageRequest request) {

        view.getForm().clearAll();

        // --
        // Reading inputs into request.
        // --
        event = request.getData(RequestParameter.DTO);
        final boolean creation = event == null;

        final List<CalendarWrapper> calendars = request.getData(RequestParameter.CONTENT);

        if (ClientUtils.isEmpty(calendars)) {
            hideView();
            throw new IllegalArgumentException("Provided calendars list is invalid.");
        }

        setPageTitle(creation ? I18N.CONSTANTS.calendarAddEvent() : I18N.CONSTANTS.calendarEditEvent());

        // --
        // Loading received calendars.
        // --
        for (final CalendarWrapper calendarWrapper : calendars) {
            if (calendarWrapper.getCalendar().isEditable()) {
                this.calendarWrapper = calendarWrapper;
            }
        }

        // --
        // Loading event on view (if edition).
        // --
        if (creation) {
            prepareAddView();
        } else {
            prepareEditView();
        }
    }

    /**
     * Create Edit Calendar Event View
     */
    private void prepareEditView() {
        this.calendarWrapper = new CalendarWrapper(event.getParent());

        view.getForm().clearState();
        view.getEventSummaryField().setValue(event.getSummary());
        view.getEventDateStartField().setValue(event.getKey());
        view.getEventDateEndField().setValue(event.getDtend());
        if (view.getRepeatEventPeriodRG() != null) {
            view.getRepeatEventPeriodRG().clear();
            view.getRepeatEventPeriodRG().clearInvalid();
            view.getRepeatEventPeriodRG().clearState();
            view.getRepeatEventPeriodRG().setSelectionRequired(false);
            view.getRepeatEventPeriodRG().hide();

            view.getPanelMonthly().hide();
            view.getMontlyVariantRG().hide();

            view.getPanelYearly().hide();
            view.getYearlyVariantRG().hide();

            view.getPanelWeekly().hide();

            view.getPanelDaily().hide();

            view.getYearlyVariantRG().hide();
            view.getMontlyVariantRG().hide();
            view.getRepeatEventPeriodRG().hide();
            view.getMonthlyRepeatRB().hide();
            view.getYearlySameDateRB().hide();
            view.getYearlySameDayOfWeekRB().hide();
            view.getRadioMonthlySameDate().hide();
            view.getRadioMonthlySameDayOfWeek().hide();

            view.getRadioNumberOfRepetitions().setValue(true);
            view.getNumberOfRepetitions().enable();
            view.getRepetitionEndDate().disable();
            view.getNumberOfRepetitions().setValue("1");
            view.getRepetitionEndDate().setValue(new Date());

            view.getYearlyRadioNumberOfRepetitions().setValue(true);
            view.getYearlyNumberOfRepetitions().enable();
            view.getYearlyRepetitionEndDate().disable();
            view.getYearlyNumberOfRepetitions().setValue("1");
            view.getYearlyRepetitionEndDate().setValue(new Date());

            view.getWeeklyRadioNumberOfRepetitions().setValue(true);
            view.getWeeklyNumberOfRepetitions().enable();
            view.getWeeklyRepetitionEndDate().disable();
            view.getWeeklyNumberOfRepetitions().setValue("1");
            view.getWeeklyRepetitionEndDate().setValue(new Date());

            view.getDailyRadioNumberOfRepetitions().setValue(true);
            view.getDailyNumberOfRepetitions().enable();
            view.getDailyRepetitionEndDate().disable();
            view.getDailyNumberOfRepetitions().setValue("1");
            view.getDailyRepetitionEndDate().setValue(new Date());
        }
        if (event.getEventType().contains("F")) {
            view.getAllDayCheckbox().setValue(true);
            view.getEventStartTimeField().hide();
            view.getEventEndTimeField().hide();
        } else {
            view.getAllDayCheckbox().setValue(false);
            view.getEventStartTimeField().show();
            view.getEventEndTimeField().show();
        }
        if (!event.getEventType().contains("F")) {

            final Time startTime = event.getDtstart() != null ? view.getEventStartTimeField().findModel(event.getDtstart()) : null;
            view.getEventStartTimeField().setValue(startTime);

            final Time endTime = event.getDtend() != null ? view.getEventEndTimeField().findModel(event.getDtend()) : null;
            view.getEventEndTimeField().setValue(endTime);

        }

        view.getEventDescriptionField().setValue(event.getDescription());
    }

    /**
     * Create Add Calendar Event View
     */
    private void prepareAddView() {
        if (view.getRepeatEventPeriodRG() != null) {
            view.getRepeatEventPeriodRG().enable();
            view.getRepeatEventPeriodRG().show();
            view.getOnceRepeatRB().setValue(true);
            view.getPanelMonthly().hide();
            view.getPanelYearly().hide();
            view.getPanelWeekly().hide();
            view.getPanelDaily().hide();
            //  view.getMonthlyRepSettings().hide();
            //  view.getYearlyRepSettings().hide();
        }
        view.getEventStartTimeField().show();
        view.getEventEndTimeField().show();

        view.getYearlyVariantRG().enable();
        view.getMontlyVariantRG().enable();
        view.getRepeatEventPeriodRG().enable();
        view.getMonthlyRepeatRB().enable();
        view.getYearlySameDateRB().enable();
        view.getYearlySameDayOfWeekRB().enable();
        view.getRadioMonthlySameDate().enable();
        view.getRadioMonthlySameDayOfWeek().enable();

        view.getRepeatMultiEventPeriodRG().enable();
        view.getRadioNumberOfRepetitions().enable();
        view.getRadioRepetitionEndDate().enable();
        view.getNumberOfRepetitions().enable();
        view.getRepetitionEndDate().enable();

        view.getRepeatMultiEventPeriodRG().show();
        view.getRadioNumberOfRepetitions().show();
        view.getRadioRepetitionEndDate().show();
        view.getNumberOfRepetitions().show();
        view.getRepetitionEndDate().show();

        view.getYearlyVariantRG().show();
        view.getMontlyVariantRG().show();
        view.getRepeatEventPeriodRG().show();
        view.getMonthlyRepeatRB().show();
        view.getYearlySameDateRB().show();
        view.getYearlySameDayOfWeekRB().show();
        view.getRadioMonthlySameDate().show();
        view.getRadioMonthlySameDayOfWeek().show();
        view.getEventDateEndField().show();
        view.getAllDayCheckbox().setValue(true);
        view.getEventStartTimeField().hide();
        view.getEventEndTimeField().hide();

        view.getRadioNumberOfRepetitions().setValue(true);
        view.getNumberOfRepetitions().enable();
        view.getRepetitionEndDate().disable();
        view.getNumberOfRepetitions().setValue("1");
        view.getRepetitionEndDate().setValue(new Date());

        view.getYearlyRadioNumberOfRepetitions().setValue(true);
        view.getYearlyNumberOfRepetitions().enable();
        view.getYearlyRepetitionEndDate().disable();
        view.getYearlyNumberOfRepetitions().setValue("1");
        view.getYearlyRepetitionEndDate().setValue(new Date());

        view.getWeeklyRadioNumberOfRepetitions().setValue(true);
        view.getWeeklyNumberOfRepetitions().enable();
        view.getWeeklyRepetitionEndDate().disable();
        view.getWeeklyNumberOfRepetitions().setValue("1");
        view.getWeeklyRepetitionEndDate().setValue(new Date());

        view.getDailyRadioNumberOfRepetitions().setValue(true);
        view.getDailyNumberOfRepetitions().enable();
        view.getDailyRepetitionEndDate().disable();
        view.getDailyNumberOfRepetitions().setValue("1");
        view.getDailyRepetitionEndDate().setValue(new Date());
    }

    // ---------------------------------------------------------------------------------------------------------------
    //
    // UTILITY METHODS.
    //
    // ---------------------------------------------------------------------------------------------------------------
    /**
     * Method executed on save button action.
     */
    @SuppressWarnings("deprecation")
    private void onSaveAction() {
       // Window.alert("onSaveAction");
        if (!view.getForm().isValid()) {
            return;
        }
        // --
        // Building properties map.
        // --
        final Map<String, Serializable> properties = new HashMap<String, Serializable>();
        properties.put(Event.CALENDAR_ID, calendarWrapper);
        properties.put(Event.SUMMARY, view.getEventSummaryField().getValue());

        String eventSummary = view.getEventSummaryField().getValue();
        String eventDescription = view.getEventDescriptionField().getValue();

        final Date beginEventIntervalDate = view.getEventDateStartField().getValue();
        properties.put(Event.DATE, beginEventIntervalDate);

        Date endEventIntervalDate = (view.getEventDateEndField() != null ? view.getEventDateEndField().getValue() : null);
        if (endEventIntervalDate == null) {
            endEventIntervalDate = beginEventIntervalDate;
        }
        properties.put(Event.DATE_END, endEventIntervalDate);
//Window.alert("Date start: " +beginEventIntervalDate.getYear()+"." +beginEventIntervalDate.getMonth()
        //              + "." + beginEventIntervalDate.getDate() + "    |    Date end: "  +endEventIntervalDate.getYear()+"." +endEventIntervalDate.getMonth()
        //            + "." + endEventIntervalDate.getDate());//temp for checker
        Boolean isFullDayEvent = view.getAllDayCheckbox().getValue();

        //   Window.alert("isAllDayEvent=" + isFullDayEvent);//temp for checker
        Date startDateTime = createStartDateTimeProperty(isFullDayEvent, beginEventIntervalDate, properties);
        Date endDateTime = createEndDateTimeProperty(isFullDayEvent, beginEventIntervalDate, properties);
        int daysdiff = calculateEventDurationInDays(beginEventIntervalDate, endEventIntervalDate);
        properties.put(Event.DESCRIPTION, view.getEventDescriptionField().getValue());

        if (event == null) {
            processAddEvent(endEventIntervalDate, beginEventIntervalDate, startDateTime, endDateTime, properties, eventSummary, eventDescription);
        } else {

            properties.put(Event.EVENT_TYPE, event.getEventType());
            properties.put(Event.REFERENCE_ID, event.getReferenceId());

            if (isFullDayEvent) {
                //     Window.alert("#3 isFullDayEvent event.getEventType() = " + event.getEventType());
                if (event.getEventType() != null) {
                    if ("O".equals(event.getEventType())
                            || "OH".equals(event.getEventType())) {
                        properties.put(Event.EVENT_TYPE, "OF");
                    } else if ("D".equals(event.getEventType())
                            || "DH".equals(event.getEventType())) {
                        properties.put(Event.EVENT_TYPE, "DF");
                    } else if ("W".equals(event.getEventType())
                            || "WH".equals(event.getEventType())) {
                        properties.put(Event.EVENT_TYPE, "WF");
                    } else if ("M".equals(event.getEventType())
                            || "MH".equals(event.getEventType())) {
                        properties.put(Event.EVENT_TYPE, "MF");
                    } else if ("Y".equals(event.getEventType())
                            || "YH".equals(event.getEventType())) {
                        properties.put(Event.EVENT_TYPE, "YF");
                    }
                }
            } else {
//                Window.alert("#4 not full day event.getEventType() = " + event.getEventType()
//                        + " daysdiff=" + daysdiff);
                if (event.getEventType() != null) {
                    if ("OF".equals(event.getEventType())) {
                        properties.put(Event.EVENT_TYPE, ("O" + (daysdiff > 1 ? "H" : "")));
                        //properties.put(Event.EVENT_TYPE, "O");
                    } else if ("DF".equals(event.getEventType())) {
                        properties.put(Event.EVENT_TYPE, ("D" + (daysdiff > 1 ? "H" : "")));
                        //properties.put(Event.EVENT_TYPE, "D");
                    } else if ("WF".equals(event.getEventType())) {
                        properties.put(Event.EVENT_TYPE, ("W" + (daysdiff > 1 ? "H" : "")));
                        //properties.put(Event.EVENT_TYPE, "W" );
                    } else if ("MF".equals(event.getEventType())) {
                        properties.put(Event.EVENT_TYPE, ("M" + (daysdiff > 1 ? "H" : "")));
                        //properties.put(Event.EVENT_TYPE, "M");
                    } else if ("YF".equals(event.getEventType())) {
                        properties.put(Event.EVENT_TYPE, ("Y" + (daysdiff > 1 ? "H" : "")));
                        //properties.put(Event.EVENT_TYPE, "Y");
                    }
                }
            }
            editPersonalEvent(event, properties);
        }

    }

    /**
     * Add new calendar events processing.
     *
     * @param endEventIntervalDate the value of endEventIntervalDate
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param startDate the value of startDateTime
     * @param endDate the value of endDateTime
     * @param properties the value of properties
     * @param eventSummary the value of eventSummary
     * @param eventDescription the value of eventDescription
     */
    private void processAddEvent(Date endEventIntervalDate, final Date beginEventIntervalDate, Date startDate, Date endDate, final Map<String, Serializable> properties, String eventSummary, String eventDescription) {
        long milisPerDay = 86400000; //24 * 60 * 60 * 1000)
        long milisPerWeek = 7 * milisPerDay; //7 days * (24hour * 60minutes * 60seconds * 1000mili seconds)
        long diffInMilis = endEventIntervalDate.getTime() - beginEventIntervalDate.getTime();
        Boolean isDailyRepeatEvent = view.getDailyRepeatRB() != null ? view.getDailyRepeatRB().getValue() : Boolean.FALSE;
        Boolean isWeeklyRepeatEvent = view.getWeeklyRepeatRB().getValue();
        Boolean isMonthlyRepeatEvent = view.getMonthlyRepeatRB().getValue();
        Boolean isYearlyRepeatEvent = view.getYearlyRepeatRB().getValue();

        Boolean isMonthlySameDayOfWeek = view.getRadioMonthlySameDayOfWeek().getValue();

        Boolean isYearlySameDayOfWeek = view.getYearlySameDayOfWeekRB().getValue();
        // Date endEventIntervalDate = (view.getEventDateEndField() != null ? view.getEventDateEndField().getValue() : null);
        Date endEventIntervalDateRadio = view.getEventDateStartField().getValue();

        if (isDailyRepeatEvent) {
            long count = 0;
            long lengthDailyEvent = ((endEventIntervalDate.getTime() - beginEventIntervalDate.getTime()) / milisPerDay) + 1;
            if (view.getDailyRadioNumberOfRepetitions().getValue()) {
                count = parseInt(view.getDailyNumberOfRepetitions().getValue());
                //  Window.alert("Count1 = "+count);
            } else {
                endEventIntervalDateRadio = view.getDailyRepetitionEndDate().getValue();
                count = (((endEventIntervalDateRadio.getTime() - beginEventIntervalDate.getTime()) / milisPerDay) + 1) / lengthDailyEvent;
            }
            diffInMilis = endEventIntervalDateRadio.getTime() - beginEventIntervalDate.getTime();
            if (count <= 0) {
                count = 1;
            }
            //Window.alert("Count2 = "+count);            
            processDailyEvents(count, lengthDailyEvent, milisPerDay, beginEventIntervalDate, endEventIntervalDateRadio, endEventIntervalDate, startDate, endDate, properties, eventSummary, eventDescription);
        } else if (isWeeklyRepeatEvent) {
            if (view.getWeeklyRadioNumberOfRepetitions().getValue()) {
                endEventIntervalDateRadio.setDate(beginEventIntervalDate.getDate() + parseInt(view.getWeeklyNumberOfRepetitions().getValue()) * 7);
            } else {
                endEventIntervalDateRadio = view.getWeeklyRepetitionEndDate().getValue();
            }
            diffInMilis = endEventIntervalDateRadio.getTime() - beginEventIntervalDate.getTime();
            processWeeklyEvents(beginEventIntervalDate, endEventIntervalDate, endEventIntervalDateRadio, startDate, endDate, properties, eventSummary, eventDescription);
        } else if (isMonthlyRepeatEvent) {
            if (view.getRadioNumberOfRepetitions().getValue()) {
                endEventIntervalDateRadio.setMonth(beginEventIntervalDate.getMonth() + parseInt(view.getNumberOfRepetitions().getValue()));
            } else {
                endEventIntervalDateRadio = view.getRepetitionEndDate().getValue();
            }
            processMonthlyEvents(beginEventIntervalDate, endEventIntervalDate, endEventIntervalDateRadio, properties, isMonthlySameDayOfWeek, startDate, endDate, eventSummary, eventDescription);
        } else if (isYearlyRepeatEvent) {
            if (view.getYearlyRadioNumberOfRepetitions().getValue()) {
                endEventIntervalDateRadio.setYear(beginEventIntervalDate.getYear() + parseInt(view.getYearlyNumberOfRepetitions().getValue()));
            } else {
                endEventIntervalDateRadio = view.getYearlyRepetitionEndDate().getValue();
            }
            processYearEvents(beginEventIntervalDate, endEventIntervalDate, endEventIntervalDateRadio, properties, isYearlySameDayOfWeek, startDate, endDate, eventSummary, eventDescription);
        } else {
            processOnceEvent(beginEventIntervalDate, diffInMilis, milisPerDay, startDate, endDate, properties, eventSummary, eventDescription);
        }
    }

    /**
     * Create Start event date time property
     *
     * @param isFullDayEvent the value of isFullDayEvent
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param properties the value of properties
     */
    private Date createStartDateTimeProperty(Boolean isFullDayEvent, final Date beginEventIntervalDate, final Map<String, Serializable> properties) {

        Date startDate = null;
        if (!isFullDayEvent) {
            startDate = view.getEventStartTimeField().getDateValue();
        }
        if (startDate != null) {
            startDate.setYear(beginEventIntervalDate.getYear());
            startDate.setMonth(beginEventIntervalDate.getMonth());
            startDate.setDate(beginEventIntervalDate.getDate());
            properties.put(Event.START_TIME, startDate.getTime());
        } else {
            properties.put(Event.START_TIME, null);
        }
        return startDate;
    }

    /**
     * Create End event date time property
     *
     * @param isFullDayEvent the value of isFullDayEvent
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param properties the value of properties
     */
    private Date createEndDateTimeProperty(Boolean isFullDayEvent, final Date beginEventIntervalDate, final Map<String, Serializable> properties) {

        Date endDate = null;
        if (!isFullDayEvent) {
            endDate = view.getEventEndTimeField().getDateValue();
        }
        if (endDate != null) {
            endDate.setYear(beginEventIntervalDate.getYear());
            endDate.setMonth(beginEventIntervalDate.getMonth());
            endDate.setDate(beginEventIntervalDate.getDate());
            properties.put(Event.END_TIME, endDate.getTime());
        } else {
            properties.put(Event.END_TIME, null);
        }
        return endDate;
    }

    private void processOnceEvent(final Date beginEventIntervalDate1, long diffInMilis, long milisPerDay, final Date startDate, final Date endDate, final Map<String, Serializable> properties, String eventSummary, String eventDescription) {
        long daysDiff = diffInMilis / milisPerDay + 1;
        long beginEventIntervalDate = beginEventIntervalDate1.getTime();
        properties.put(Event.SUMMARY, (String) properties.get(Event.SUMMARY));
        properties.put(Event.DESCRIPTION, (String) properties.get(Event.DESCRIPTION));
        // Window.alert("DaysDiff = "+daysDiff);
        if (view.getAllDayCheckbox().getValue()) {
            properties.put(Event.EVENT_TYPE, "OF");
        } else {
            properties.put(Event.EVENT_TYPE, ("O" + (daysDiff > 1 ? "H" : "")));
            //properties.put(Event.EVENT_TYPE, "O");
        }
        if (daysDiff == 1) {
            addPersonalEvent(properties);
        } else {
            properties.put(Event.SUMMARY, (String) properties.get(Event.SUMMARY));// + " (Once event)");
            properties.put(Event.DESCRIPTION, (String) properties.get(Event.DESCRIPTION));// + " (Once event)");
//            properties.put(Event.EVENT_TYPE, "O");
//            if (view.getAllDayCheckbox().getValue()) {
//                properties.put(Event.EVENT_TYPE, "OF");
//            }
            addPersonalEventOnce(properties, daysDiff, beginEventIntervalDate, milisPerDay, startDate, endDate, eventSummary, eventDescription);
        }
    }

    private void addPersonalEventOnce(final Map<String, Serializable> properties, final long daysInterval, final long calBeginNextEventDateLong, final long milisPerDay, final Date startDate, final Date endDate, final String eventSummary, final String eventDescription) {

        final CreateEntity createEntity = new CreateEntity(PersonalEventDTO.ENTITY_NAME, properties);

        dispatch.execute(createEntity, new CommandResultHandler<CreateResult>() {

            @Override
            public void onCommandFailure(final Throwable caught) {
                processAddEventError(caught);
            }

            @Override
            public void onCommandSuccess(final CreateResult result) {

                // Creating events.
                final Event event = new Event();
                event.setIdentifier((Integer) result.getEntity().getId());
                properties.put(Event.REFERENCE_ID, (Integer) result.getEntity().getId());
                updateEvent(event, properties);
                // addOnceSeriesEvent((Integer) result.getEntity().getId(), daysInterval, calBeginNextEventDateLong, milisPerDay, startDateTime, endDateTime, eventSummary, eventDescription);
            }
        }, view.getCancelButton(), view.getSaveButton());
    }

    /**
     * Add new Daily calendar events processing.
     *
     * @param diffInMilis the value of diffInMilis
     * @param milisPerDay the value of milisPerDay
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param startDate the value of startDateTime
     * @param endDate the value of endDateTime
     * @param properties the value of properties
     * @param eventSummary the value of eventSummary
     * @param eventDescription the value of eventDescription
     */
    private void processDailyEvents(long count, long lengthDailyEvent, long milisPerDay, final Date beginEventIntervalDate, final Date endEventIntervalDateRadio, final Date endEventIntervalDate, final Date startDate, final Date endDate, final Map<String, Serializable> properties, String eventSummary, String eventDescription) {
        long calBeginNextEventDateLong = beginEventIntervalDate.getTime();
        //Date calBeginNextEventDate = beginEventIntervalDate;
        long daysInterval = ((endEventIntervalDate.getTime() - beginEventIntervalDate.getTime()) / milisPerDay) + 1;

        properties.put(Event.SUMMARY, (String) properties.get(Event.SUMMARY));// + " (Daily event 1 of " + count + ")");
        properties.put(Event.DESCRIPTION, (String) properties.get(Event.DESCRIPTION));// + " (Daily event 1 of " + count + ")");
        if (view.getAllDayCheckbox().getValue()) {
            properties.put(Event.EVENT_TYPE, "DF");
        } else {
            properties.put(Event.EVENT_TYPE, ("D" + (daysInterval > 1 ? "H" : "")));
            //properties.put(Event.EVENT_TYPE, "D");
        }
        properties.put(Event.DATE_END, new Date(calBeginNextEventDateLong + milisPerDay * (lengthDailyEvent - 1)));
        addPersonalEventDaily(properties, count, lengthDailyEvent, calBeginNextEventDateLong, milisPerDay, startDate, endDate, eventSummary, eventDescription);

    }

    private void addPersonalEventDaily(final Map<String, Serializable> properties, final long daysInterval, final long lengthDailyEvent, final long calBeginNextEventDateLong, final long milisPerDay, final Date startDate, final Date endDate, final String eventSummary, final String eventDescription) {

        final CreateEntity createEntity = new CreateEntity(PersonalEventDTO.ENTITY_NAME, properties);

        dispatch.execute(createEntity, new CommandResultHandler<CreateResult>() {

            @Override
            public void onCommandFailure(final Throwable caught) {
                processAddEventError(caught);
            }

            @Override
            public void onCommandSuccess(final CreateResult result) {

                // Creating events.
                final Event event = new Event();
                event.setIdentifier((Integer) result.getEntity().getId());
                properties.put(Event.REFERENCE_ID, (Integer) result.getEntity().getId());
                updateEvent(event, properties);
                addDailySeriesEvent((Integer) result.getEntity().getId(), daysInterval, lengthDailyEvent, calBeginNextEventDateLong, milisPerDay, startDate, endDate, eventSummary, eventDescription);
                //  if (view.getAllDayCheckbox().getValue() == false) {
                //      addDailySeriesEventNew((Integer) result.getEntity().getId(), daysInterval, lengthDailyEvent, calBeginNextEventDateLong, milisPerDay, startDateTime, endDateTime, eventSummary, eventDescription);
                //  }
            }
        }, view.getCancelButton(), view.getSaveButton());
    }

    private void processAddEventError(final Throwable caught) {
        if (Log.isErrorEnabled()) {
            Log.error(I18N.CONSTANTS.calendarAddEventError(), caught);
        }
        N10N.error(I18N.CONSTANTS.error(), I18N.CONSTANTS.calendarAddEventError());
    }

    private void addDailySeriesEvent(Integer ids, long daysInterval, long lengthDailyEvent, long calBeginNextEventDateLong1, long milisPerDay, final Date startDate, final Date endDate, String eventSummary, String eventDescription) {
        //Date calBeginNextEventDate;
        long calBeginNextEventDateLong = calBeginNextEventDateLong1;
        for (int i = 1; i < daysInterval; i++) {

            calBeginNextEventDateLong += (milisPerDay * lengthDailyEvent);
            long calEndNextEventDateLong = calBeginNextEventDateLong + milisPerDay * (lengthDailyEvent - 1);
            //calBeginNextEventDate = new Date(calBeginNextEventDateLong);

            Map<String, Serializable> dailyProperties = new HashMap<String, Serializable>();
            dailyProperties.put(Event.CALENDAR_ID, calendarWrapper);
            dailyProperties.put(Event.SUMMARY, view.getEventSummaryField().getValue());

            dailyProperties.put(Event.DATE, new Date(calBeginNextEventDateLong));
            if (view.getAllDayCheckbox().getValue()) {
                dailyProperties.put(Event.EVENT_TYPE, "DF");
            } else {
                dailyProperties.put(Event.EVENT_TYPE, ("D" + (daysInterval > 1 ? "H" : "")));
                // dailyProperties.put(Event.EVENT_TYPE, "D");
            }
            dailyProperties.put(Event.DATE_END, new Date(calEndNextEventDateLong));
            setFullDayEvent(startDate, endDate, new Date(calBeginNextEventDateLong), dailyProperties);

            String newSummary = eventSummary;
            String newDescription = eventDescription;
//            newSummary += " (Daily event " + (i + 1) + " of " + daysInterval + ")";
//            newDescription += " (Daily event " + (i + 1) + " of " + daysInterval + ")";
            dailyProperties.put(Event.SUMMARY, newSummary);
            dailyProperties.put(Event.DESCRIPTION, newDescription);
            dailyProperties.put(Event.REFERENCE_ID, ids);

            addPersonalEvent(dailyProperties);
        }
    }

    /**
     * Add new Weekly calendar events processing.
     *
     * @param diffInMilis the value of diffInMilis
     * @param milisPerWeek the value of milisPerWeek
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param startDate the value of startDateTime
     * @param endDate the value of endDateTime
     * @param properties the value of properties
     * @param eventSummary the value of eventSummary
     * @param eventDescription the value of eventDescription
     */
    private void processWeeklyEvents(final Date beginEventIntervalDate, final Date endEventIntervalDate, final Date endEventIntervalDateRadio, final Date startDate, final Date endDate, final Map<String, Serializable> properties, String eventSummary, String eventDescription) {
      //  Window.alert("processWeeklyEvents");
        long milisPerDay = 86400000;
        long weekDiff = ((endEventIntervalDateRadio.getTime() - beginEventIntervalDate.getTime() + milisPerDay) / (milisPerDay * 7));
        int weeksInterval = (int) weekDiff;
        if ((((endEventIntervalDateRadio.getTime() - beginEventIntervalDate.getTime() + milisPerDay) % (milisPerDay * 7)) >= ((endEventIntervalDate.getTime() - beginEventIntervalDate.getTime() + milisPerDay)))
                && (view.getWeeklyRadioNumberOfRepetitions().getValue())) {
            weeksInterval++;
        }
        if (weeksInterval <= 0) {
            weeksInterval = 1;
        }
        if (endEventIntervalDate.getTime() - beginEventIntervalDate.getTime() == 0) {
            weeksInterval--;
        }

        long calBeginNextEventDateLong = beginEventIntervalDate.getTime();
        long calEndNextEventDateLong = endEventIntervalDate.getTime();
        long daysInterval = ((endEventIntervalDate.getTime() - beginEventIntervalDate.getTime()) / milisPerDay) + 1;

        properties.put(Event.SUMMARY, (String) properties.get(Event.SUMMARY));
        properties.put(Event.DESCRIPTION, (String) properties.get(Event.DESCRIPTION));// + " (Weekly event 1 of " + weeksInterval + ")");
//        Window.alert("#5 processWeeklyEvents event.getEventType() = " + (event!=null?event.getEventType():"NULL")
//                + " daysInterval=" + daysInterval);
        if (view.getAllDayCheckbox().getValue()) {
            properties.put(Event.EVENT_TYPE, "WF");
        } else {
            properties.put(Event.EVENT_TYPE, ("W" + (daysInterval > 1 ? "H" : "")));
            //properties.put(Event.EVENT_TYPE, "W");
        }
        addPersonalEventWeekly(properties, weeksInterval, calBeginNextEventDateLong, calEndNextEventDateLong, daysInterval, startDate, endDate, eventSummary, eventDescription);

    }

    private void addPersonalEventWeekly(final Map<String, Serializable> properties, final int weeksInterval, final long calBeginNextEventDateLong, final long calEndNextEventDateLong, final long daysInterval, final Date startDate, final Date endDate, final String eventSummary, final String eventDescription) {

        final CreateEntity createEntity = new CreateEntity(PersonalEventDTO.ENTITY_NAME, properties);

        dispatch.execute(createEntity, new CommandResultHandler<CreateResult>() {

            @Override
            public void onCommandFailure(final Throwable caught) {
                processAddEventError(caught);
            }

            @Override
            public void onCommandSuccess(final CreateResult result) {

                // Creating events.
                final Event event = new Event();
                event.setIdentifier((Integer) result.getEntity().getId());
                updateEvent(event, properties);
                properties.put(Event.REFERENCE_ID, (Integer) result.getEntity().getId());
                long milisPerDay = 86400000;
                addDailySeriesEventNew2((Integer) properties.get(Event.REFERENCE_ID), weeksInterval, calBeginNextEventDateLong, calEndNextEventDateLong, milisPerDay * 7, startDate, endDate, eventSummary, eventDescription);
            }
        }, view.getCancelButton(), view.getSaveButton());
    }

    private void addDailySeriesEventNew2(Integer ids, int daysInterval, long calBeginNextEventDateLong, long calEndNextEventDateLong, long milisPerWeek, final Date startDate, final Date endDate, String eventSummary, String eventDescription) {

        for (int i = 1; i < daysInterval; i++) {

            calBeginNextEventDateLong += milisPerWeek;
            calEndNextEventDateLong += milisPerWeek;

            Map<String, Serializable> dailyProperties = new HashMap<String, Serializable>();
            dailyProperties.put(Event.CALENDAR_ID, calendarWrapper);
            dailyProperties.put(Event.SUMMARY, view.getEventSummaryField().getValue());

            dailyProperties.put(Event.DATE, new Date(calBeginNextEventDateLong));

            dailyProperties.put(Event.DATE_END, new Date(calEndNextEventDateLong));

            setFullDayEvent(startDate, endDate, new Date(calBeginNextEventDateLong), dailyProperties);

            String newSummary = eventSummary;
            String newDescription = eventDescription;
            //           newDescription += " (Weekly event " + (i + 1) + " of " + daysInterval + ")";
            dailyProperties.put(Event.SUMMARY, newSummary);
            dailyProperties.put(Event.DESCRIPTION, newDescription);
            if (view.getAllDayCheckbox().getValue()) {
                dailyProperties.put(Event.EVENT_TYPE, "WF");
            } else {
//                Window.alert("#1 addDailySeriesEventNew2 event.getEventType() = " + (event!=null?event.getEventType():"null")
//                        + " daysInterval=" + daysInterval);
                dailyProperties.put(Event.EVENT_TYPE, ("W" + (daysInterval > 1 ? "H" : "")));
            }
            dailyProperties.put(Event.REFERENCE_ID, ids);

            addPersonalEvent(dailyProperties);
        }
    }

    /**
     * Set date and time values for Full day events.
     *
     * @param startDate the value of startDateTime
     * @param endDate the value of endDateTime
     * @param calBeginNextEventDate the value of calBeginNextEventDate
     * @param thePeriodProperties the value of thePeriodProperties
     */
    private void setFullDayEvent(final Date startDate, final Date endDate, Date calBeginNextEventDate, Map<String, Serializable> thePeriodProperties) {
        if (startDate != null) {
            calBeginNextEventDate.setHours(startDate.getHours());
            calBeginNextEventDate.setMinutes(startDate.getMinutes());
            thePeriodProperties.put(Event.START_TIME, calBeginNextEventDate.getTime());
        } else {
            thePeriodProperties.put(Event.START_TIME, null);
        }

        if (endDate != null) {
            Date endD = calBeginNextEventDate;
            endD.setHours(endDate.getHours());
            endD.setMinutes(endDate.getMinutes());
            thePeriodProperties.put(Event.END_TIME, endD.getTime());
        } else {
            thePeriodProperties.put(Event.END_TIME, null);
        }
    }

    /**
     * Add new Monthly calendar events processing.
     *
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param endEventIntervalDate the value of endEventIntervalDate
     * @param properties the value of properties
     * @param isMonthlySameDayOfWeek the value of isMonthlySameDayOfWeek
     * @param startDate the value of startDateTime
     * @param endDate the value of endDateTime
     * @param eventSummary the value of eventSummary
     * @param eventDescription the value of eventDescription
     */
    private void processMonthlyEvents(final Date beginEventIntervalDate, Date endEventIntervalDate, final Date endEventIntervalDateRadio, final Map<String, Serializable> properties, Boolean isMonthlySameDayOfWeek, final Date startDate, final Date endDate, String eventSummary, String eventDescription) {
        long milisPerDay = 86400000; //24 * 60 * 60 * 1000)
        long daysInterval = ((endEventIntervalDate.getTime() - beginEventIntervalDate.getTime()) / milisPerDay) + 1;

        if (view.getAllDayCheckbox().getValue()) {
            properties.put(Event.EVENT_TYPE, "MF");
        } else {
            properties.put(Event.EVENT_TYPE, ("M" + (daysInterval > 1 ? "H" : "")));
            // properties.put(Event.EVENT_TYPE, "M");
        }
        properties.put(Event.DATE_END, endEventIntervalDate);
        Date endEventIntervalDate1 = endEventIntervalDate;
        long milisDiff = endEventIntervalDate.getTime() - beginEventIntervalDate.getTime();
        endEventIntervalDate = endEventIntervalDateRadio;
        int yearStart = beginEventIntervalDate.getYear();
        int yearEnd = endEventIntervalDate.getYear();
        int yearInterval = yearEnd - yearStart;

        int monthStart = beginEventIntervalDate.getMonth();//0 Jan 11 Dec
        int monthEnd = endEventIntervalDate.getMonth();
        int monthInterval = 0;
        if (yearInterval > 0) {
            monthInterval = (yearInterval - 1) * 12 + (12 - monthStart) + (monthEnd + 1);
        } else {
            monthInterval = monthEnd - monthStart + 1;
        }
        if (view.getRadioNumberOfRepetitions().getValue()) {
            monthInterval--;
        }
        if (monthInterval <= 0) {
            monthInterval = 1;
        }
        properties.put(Event.SUMMARY, (String) properties.get(Event.SUMMARY));// + " (Monthly event 1 of " + monthInterval + ")");
        properties.put(Event.DESCRIPTION, (String) properties.get(Event.DESCRIPTION));// + " (Monthly event 1 of " + monthInterval + ")");
        addPersonalEventMonthly(endEventIntervalDate1, properties, monthInterval, beginEventIntervalDate, milisDiff, startDate, endDate, eventSummary, eventDescription, isMonthlySameDayOfWeek);

    }

    private void addPersonalEventMonthly(final Date endEventIntervalDate, final Map<String, Serializable> properties, final int monthInterval, final Date beginEventIntervalDate, final long milisDiff, final Date startDate, final Date endDate, final String eventSummary, final String eventDescription, final boolean isMonthlySameDayOfWeek) {
        final CreateEntity createEntity = new CreateEntity(PersonalEventDTO.ENTITY_NAME, properties);

        dispatch.execute(createEntity, new CommandResultHandler<CreateResult>() {

            @Override
            public void onCommandFailure(final Throwable caught) {
                processAddEventError(caught);
            }

            @Override
            public void onCommandSuccess(final CreateResult result) {

                // Creating events.
                final Event event = new Event();
                event.setIdentifier((Integer) result.getEntity().getId());
                updateEvent(event, properties);
                properties.put(Event.REFERENCE_ID, (Integer) result.getEntity().getId());
//                long milisPerDay = 86400000;
//                long daysInterval = milisDiff / milisPerDay + 1;
                addMonthlySeriesEvent(endEventIntervalDate, (String) result.getEntity().getId(), monthInterval, beginEventIntervalDate, startDate, endDate, eventSummary, eventDescription, isMonthlySameDayOfWeek);
            }
        }, view.getCancelButton(), view.getSaveButton());
    }

    private void addMonthlySeriesEvent(Date endEventIntervalDate, String ids, int monthInterval, Date beginEventIntervalDate, final Date startDate, final Date endDate, String eventSummary, String eventDescription, final boolean isMonthlySameDayOfWeek) {
        Date calBeginNextEventDate = beginEventIntervalDate;
        long diff = endEventIntervalDate.getTime() - beginEventIntervalDate.getTime();
        long milisPerDay = 86400000; //24 * 60 * 60 * 1000)
        long daysInterval = ((endEventIntervalDate.getTime() - beginEventIntervalDate.getTime()) / milisPerDay) + 1;

        for (int i = 1; i < monthInterval; i++) {
            calBeginNextEventDate = getMonthlySameDayOfWeek2(beginEventIntervalDate, calBeginNextEventDate, i, isMonthlySameDayOfWeek);

            Map<String, Serializable> monthlyProperties = new HashMap<String, Serializable>();
            monthlyProperties.put(Event.CALENDAR_ID, calendarWrapper);
            monthlyProperties.put(Event.SUMMARY, view.getEventSummaryField().getValue());

            monthlyProperties.put(Event.DATE, calBeginNextEventDate);
//            monthlyProperties.put(Event.EVENT_TYPE, "M");

            monthlyProperties.put(Event.DATE_END, new Date(calBeginNextEventDate.getTime() + diff));
            if (view.getAllDayCheckbox().getValue()) {
                monthlyProperties.put(Event.EVENT_TYPE, "MF");
            } else {
                monthlyProperties.put(Event.EVENT_TYPE, ("M" + (daysInterval > 1 ? "H" : "")));
                //monthlyProperties.put(Event.EVENT_TYPE, "M");
            }

            setFullDayEvent(startDate, endDate, calBeginNextEventDate, monthlyProperties);

            String newSummary = eventSummary;
            String newDescription = eventDescription;
            //           newSummary += " (Monthly event " + (i + 1) + " of " + monthInterval + ")";
            //           newDescription += " (Monthly " + (isMonthlySameDayOfWeek ? "same Day of a week " : "same Date ") + "event " + (i + 1) + " of " + monthInterval + ")";
            monthlyProperties.put(Event.SUMMARY, newSummary);
            monthlyProperties.put(Event.DESCRIPTION, newDescription);

            monthlyProperties.put(Event.REFERENCE_ID, ids);
            addPersonalEvent(monthlyProperties);
        }
    }

    /**
     * Add new Yearly calendar events processing.
     *
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param endEventIntervalDate the value of endEventIntervalDate
     * @param properties the value of properties
     * @param isYearlySameDayOfWeek the value of isYearlySameDayOfWeek
     * @param startDate the value of startDateTime
     * @param endDate the value of endDateTime
     * @param eventSummary the value of eventSummary
     * @param eventDescription the value of eventDescription
     */
    private void processYearEvents(final Date beginEventIntervalDate, Date endEventIntervalDate, final Date endEventIntervalDateRadio, final Map<String, Serializable> properties, Boolean isYearlySameDayOfWeek, final Date startDate, final Date endDate, String eventSummary, String eventDescription) {

        properties.put(Event.DATE_END, endEventIntervalDate);
        long milisDiff = endEventIntervalDate.getTime() - beginEventIntervalDate.getTime();
        long milisPerDay = 86400000; //24 * 60 * 60 * 1000)
        long daysInterval = ((endEventIntervalDate.getTime() - beginEventIntervalDate.getTime()) / milisPerDay) + 1;
              
        endEventIntervalDate = endEventIntervalDateRadio;
        int yearStart = beginEventIntervalDate.getYear();
        int yearEnd = endEventIntervalDate.getYear();
        int yearInterval = yearEnd - yearStart + 1;
        if (view.getYearlyRadioNumberOfRepetitions().getValue()) {
            yearInterval--;
        }
        if (yearInterval <= 0) {
            yearInterval = 1;
        }
              //  properties.put(Event.EVENT_TYPE, "Y");
        if (view.getAllDayCheckbox().getValue()) {
            properties.put(Event.EVENT_TYPE, "YF");
        } else {
            properties.put(Event.EVENT_TYPE, ("Y" + (daysInterval > 1 ? "H" : "")));
           // properties.put(Event.EVENT_TYPE, "Y");
        }
        properties.put(Event.SUMMARY, (String) properties.get(Event.SUMMARY));// + " (Yearly event 1 of " + yearInterval + ")");
        properties.put(Event.DESCRIPTION, (String) properties.get(Event.DESCRIPTION));// + " (Yearly " + (isYearlySameDayOfWeek ? "same Day of a week " : "same Date ") + "event 1 of " + yearInterval + ")");

        addPersonalEventYearly(milisDiff, properties, yearInterval, beginEventIntervalDate, endEventIntervalDate, startDate, endDate, eventSummary, eventDescription, isYearlySameDayOfWeek);

    }

    private void addPersonalEventYearly(final long milisDiff, final Map<String, Serializable> properties, final int yearInterval, final Date beginEventIntervalDate, final Date endEventIntervalDate, final Date startDate, final Date endDate, final String eventSummary, final String eventDescription, final boolean isYearlySameDayOfWeek) {

        final CreateEntity createEntity = new CreateEntity(PersonalEventDTO.ENTITY_NAME, properties);

        dispatch.execute(createEntity, new CommandResultHandler<CreateResult>() {

            @Override
            public void onCommandFailure(final Throwable caught) {
                processAddEventError(caught);
            }

            @Override
            public void onCommandSuccess(final CreateResult result) {

                final Event event = new Event();
                event.setIdentifier((Integer) result.getEntity().getId());
                properties.put(Event.REFERENCE_ID, (Integer) result.getEntity().getId());
                updateEvent(event, properties);
                long milisPerDay = 86400000;
                long daysInterval = milisDiff / milisPerDay + 1;

                addYearlySeriesEvent(milisDiff, (String) result.getEntity().getId(), yearInterval, beginEventIntervalDate, endEventIntervalDate, startDate, endDate, eventSummary, eventDescription, isYearlySameDayOfWeek);
            }
        }, view.getCancelButton(), view.getSaveButton());
    }

    private void addYearlySeriesEvent(final long milisDiff, String ids, int yearInterval, Date beginEventIntervalDate, Date endEventIntervalDate, final Date startDate, final Date endDate, String eventSummary, String eventDescription, final boolean isYearlySameDayOfWeek) {
        long milisPerDay = 86400000; //24 * 60 * 60 * 1000)
        long daysInterval = ((endEventIntervalDate.getTime() - beginEventIntervalDate.getTime()) / milisPerDay) + 1;

        
        Date calBeginNextEventDate = beginEventIntervalDate;
        for (int i = 1; i < yearInterval; i++) {
            calBeginNextEventDate = getYearlySameDayOfWeek(beginEventIntervalDate, calBeginNextEventDate, i, isYearlySameDayOfWeek);

            Map<String, Serializable> yearlyProperties = new HashMap<String, Serializable>();
            yearlyProperties.put(Event.CALENDAR_ID, calendarWrapper);
            yearlyProperties.put(Event.SUMMARY, view.getEventSummaryField().getValue());

            yearlyProperties.put(Event.DATE, calBeginNextEventDate);

            if (view.getAllDayCheckbox().getValue()) {
                yearlyProperties.put(Event.EVENT_TYPE, "YF");
            } else {
                yearlyProperties.put(Event.EVENT_TYPE, ("Y" + (daysInterval > 1 ? "H" : "")));
                //yearlyProperties.put(Event.EVENT_TYPE, "Y");
            }

            yearlyProperties.put(Event.DATE_END, new Date(calBeginNextEventDate.getTime() + milisDiff));

            setFullDayEvent(startDate, endDate, calBeginNextEventDate, yearlyProperties);

            String newSummary = eventSummary;
            String newDescription = eventDescription;
            //           newSummary += " (Yearly event " + (i + 1) + " of " + yearInterval + ")";
            //           newDescription += " (Yearly " + (isYearlySameDayOfWeek ? "same Day of a week " : "same Date ") + "event " + (i + 1) + " of " + yearInterval + ")";
            yearlyProperties.put(Event.SUMMARY, newSummary);
            yearlyProperties.put(Event.DESCRIPTION, newDescription);

            yearlyProperties.put(Event.REFERENCE_ID, ids);
            addPersonalEvent(yearlyProperties);
        }
    }

    /**
     * Creates a new "Personal" calendar event.
     *
     * @param properties Properties of the new event.
     */
    private void addPersonalEvent(final Map<String, Serializable> properties) {

        final CreateEntity createEntity = new CreateEntity(PersonalEventDTO.ENTITY_NAME, properties);

        dispatch.execute(createEntity, new CommandResultHandler<CreateResult>() {

            @Override
            public void onCommandFailure(final Throwable caught) {
                processAddEventError(caught);
            }

            @Override
            public void onCommandSuccess(final CreateResult result) {

                // Creating events.
                final Event event = new Event();
                event.setIdentifier((Integer) result.getEntity().getId());
                updateEvent(event, properties);
            }
        }, view.getCancelButton(), view.getSaveButton());
    }

    /**
     * Edits the events.
     *
     * @param properties Properties of the new event.
     */
    private void editPersonalEvent(final Event event, final Map<String, ?> properties) {

        @SuppressWarnings("unchecked")
        final UpdateEntity updateEntity = new UpdateEntity(PersonalEventDTO.ENTITY_NAME, event.getIdentifier(), (Map<String, Object>) properties);

        dispatch.execute(updateEntity, new CommandResultHandler<VoidResult>() {

            @Override
            public void onCommandFailure(final Throwable caught) {
                processAddEventError(caught);
            }

            @Override
            public void onCommandSuccess(final VoidResult result) {

                final Calendar calendar = event.getParent();

                final List<Event> oldEventList = calendar.getEvents().get(event.getKey());
                if (oldEventList != null && oldEventList.contains(event)) {
                    oldEventList.remove(event);
                }
                final List<Event> oldFullDayEventList = calendar.getFullDayEvents().get(event.getKey());
                if (oldFullDayEventList != null && oldFullDayEventList.contains(event)) {
                    oldFullDayEventList.remove(event);
                }
                updateEvent(event, properties);
            }
        }, view.getCancelButton(), view.getSaveButton());
    }

    /**
     * Updates the given {@code event} with the given {@code properties}.
     *
     * @param event The event to update.
     * @param properties The properties.
     */
    @SuppressWarnings("deprecation")
    private void updateEvent(final Event event, final Map<String, ?> properties) {

        // --
        // Updates the event.
        // --
        event.setSummary((String) properties.get(Event.SUMMARY));
        event.setDescription((String) properties.get(Event.DESCRIPTION));
        event.setEventType((String) properties.get(Event.EVENT_TYPE));

        event.setReferenceId((Integer) properties.get(Event.REFERENCE_ID));

        final Date day = (Date) properties.get(Event.DATE);
        final Date dayEnd = (Date) properties.get(Event.DATE_END);
        final Object startHourSerialized = properties.get(Event.START_TIME);
        final Object endHourSerialized = properties.get(Event.END_TIME);

        if (startHourSerialized != null) {
            final Date startHour = new Date((Long) startHourSerialized);
            event.setDtstart(startHour);
            if (endHourSerialized != null) {
                final Date endHour = new Date((Long) endHourSerialized + ((dayEnd.getTime() - day.getTime())));
                //86400000
                event.setDtend(endHour);
            } else {
                event.setDtend(null);
            }

        } else {
            event.setDtstart(new Date(day.getYear(), day.getMonth(), day.getDate()));
            event.setDtend(new Date(dayEnd.getYear(), dayEnd.getMonth(), dayEnd.getDate()));
        }

        // Adding the new event to the calendar
        final CalendarWrapper wrapper = (CalendarWrapper) properties.get(Event.CALENDAR_ID);
        final Calendar calendar = wrapper.getCalendar();

        event.setParent(calendar);
//ak
        if (calendar.getEvents() != null && !event.getEventType().contains("F")) {
            List<Event> events = calendar.getEvents().get(day);
            if (events == null) {
                events = new ArrayList<Event>();
                calendar.getEvents().put(day, events);
            }
            events.add(event);
        }
        if (calendar.getFullDayEvents() != null && (event.getEventType().contains("F")
                || (event.getDtstart().getHours() == event.getDtend().getHours()
                && event.getDtstart().getMinutes() == event.getDtend().getMinutes()))) {
            List<Event> fullDayEvents = calendar.getFullDayEvents().get(day);
            if (fullDayEvents == null) {
                fullDayEvents = new ArrayList<Event>();
                calendar.getFullDayEvents().put(day, fullDayEvents);
            }
            fullDayEvents.add(event);
        }
        // --
        // Sends an update event on the event bus.
        // --
        eventBus.fireEvent(new UpdateEvent(UpdateEvent.CALENDAR_EVENT_UPDATE, event));
        // calendarWidget.refresh();

        // --
        // Hides the view.
        // --
        hideView();
    }

    /**
     * Returns if the given {@code event} is a full day event.
     *
     * @param event The event.
     * @return {@code true} if the given {@code event} is a full day event.
     */
    @SuppressWarnings("deprecation")
    private static boolean isFullDayEvent(final Event event) {

        if (event == null) {
            return false;
        }

        return event.getDtend() != null
                && (event.getDtstart().getDate() != event.getDtend().getDate() || event.getDtstart().getMonth() != event.getDtend().getMonth() || event.getDtstart()
                .getYear() != event.getDtend().getYear());
    }

    /**
     *
     * @param firstDate
     * @param nextDateOld
     * @param numberMonths
     * @return the java.util.Date
     */
    @SuppressWarnings({"deprecation", "empty-statement"})
    public Date getMonthlySameDate(Date firstDate, Date nextDateOld, int numberMonths) {
        Date nextDateNew = new Date();
        Date newDate = new Date();
        nextDateNew.setYear(nextDateOld.getYear());
        nextDateNew.setMonth(nextDateOld.getMonth() + 1);
        nextDateNew.setDate(1);// to prevent month slip past (f.e. 31 jan -> 3 march instead of 29 febr)
        newDate.setYear(nextDateOld.getYear());

        int daysInNextDate = getDaysInMonth(nextDateNew.getYear(), nextDateNew.getMonth());

        if (firstDate.getDate() <= daysInNextDate) {//if last day of the month
            newDate.setMonth(0);
            newDate.setDate(firstDate.getDate());
            newDate.setMonth(nextDateOld.getMonth() + 1);

        } else {
            newDate.setDate(daysInNextDate);
            newDate.setMonth(nextDateOld.getMonth() + 1);

        }

        return newDate;
    }

    /**
     * Calculates same date or same day of week in next month.
     *
     * @param firstDate
     * @param nextDateOld
     * @param numberMonths
     * @param isSameDayOfWeek if the value is true then will be calculated Date
     * of the nearest same day of week
     * @return next month date
     */
    public Date getMonthlySameDayOfWeek2(Date firstDate, Date nextDateOld, int numberMonths, boolean isSameDayOfWeek) {

        Date nextDateNew = new Date();
        Date newDate = new Date();
        nextDateNew.setDate(1);// to prevent month slip past (f.e. 31 jan -> 3 march instead of 29 febr)
        newDate.setDate(1);
        nextDateNew.setYear(nextDateOld.getYear());
        nextDateNew.setMonth(nextDateOld.getMonth() + 1);
        newDate.setYear(nextDateOld.getYear());

        int daysInNextDate = getDaysInMonth(nextDateNew.getYear(), nextDateNew.getMonth());

        if (firstDate.getDate() <= daysInNextDate) {//if last day of the month
            newDate.setMonth(0);
            newDate.setDate(firstDate.getDate());
            newDate.setMonth(nextDateOld.getMonth() + 1);

        } else {
            newDate.setDate(daysInNextDate);
            newDate.setMonth(nextDateOld.getMonth() + 1);

        }
        if (isSameDayOfWeek) {
            Date newDate2 = getSameWeekDay(firstDate, newDate); //getSameWeekDay22

            newDate = newDate2;
        }
        return newDate;
    }

    /**
     * Calculates same date or same day of week in next year.
     *
     * @param firstDate
     * @param nextDateOld
     * @param numberMonths
     * @param isSameDayOfWeek if the value is true then will be calculated Date
     * of the nearest same day of week
     * @return
     */
    @SuppressWarnings({"deprecation"})
    public Date getYearlySameDayOfWeek(Date firstDate, Date nextDateOld, int numberMonths, boolean isSameDayOfWeek) {

        Date nextDateNew = new Date();
        nextDateNew.setDate(1);
        nextDateNew.setYear(nextDateOld.getYear());
        nextDateNew.setMonth(nextDateOld.getMonth());
        nextDateNew.setYear(nextDateOld.getYear() + 1);

        int daysInNextDate = getDaysInMonth(nextDateNew.getYear(), nextDateNew.getMonth());

        if (firstDate.getDate() >= daysInNextDate) {//if last day of the month

            nextDateNew.setDate(daysInNextDate);

        } else {

            nextDateNew.setDate(firstDate.getDate());

        }

        if (isSameDayOfWeek) {

            Date newDate = getSameWeekDay(firstDate, nextDateNew);//getSameWeekDay2

            nextDateNew = newDate;
        }

        return nextDateNew;
    }

    /**
     * Calculates same week day date(as in firstDate) for nextDate
     *
     * @param firstDate the value of firstDate
     * @param nextDate the value of nextDate
     */
    private Date getSameWeekDay(Date firstDate, Date nextDate) {
        //Calculate same day of week
        int dayOfFirst = firstDate.getDay();
        int dayOfCurrent = nextDate.getDay();
        Date newDate = new Date();
        Date curDate = nextDate;
        int milSecInDay = 86400000; //24 * 60 * 60 * 1000
        if (dayOfFirst > dayOfCurrent) {
            newDate = new Date(curDate.getTime() + (dayOfFirst - dayOfCurrent) * milSecInDay);
            if (newDate.getMonth() != curDate.getMonth()) { //>
                newDate = new Date(curDate.getTime() - (7 - (dayOfFirst - dayOfCurrent)) * milSecInDay);
            }

        } else if (dayOfFirst < dayOfCurrent) {//<
            newDate = new Date(curDate.getTime() - (dayOfCurrent - dayOfFirst) * milSecInDay);
            if (newDate.getMonth() != curDate.getMonth()) {
                newDate = new Date(curDate.getTime() + (7 - (dayOfCurrent - dayOfFirst)) * milSecInDay);
            }
        } else {
            newDate = new Date(curDate.getTime());
        }
        return newDate;
    }

    /**
     * Returns numver od days in the month of the year
     *
     * @param year the value of year
     * @param month the value of month
     * @return the int
     */
    private int getDaysInMonth(int year, int month) {
        int daysInMonth = 31;

        switch (month) {
            case 3:
            case 5:
            case 8:
            case 10:
                return daysInMonth = 30;
            case 1:
                return daysInMonth = (isLeapYear(year)) ? 29 : 28;
            default:
                return daysInMonth;
        }
    }

    /**
     * Validate if the year is leap year.
     *
     * @param year the value of year
     * @return the boolean
     */
    private boolean isLeapYear(int year) {
        return (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0));
    }

    private static int calculateEventDurationInDays(final Date startDateTime, Date endDateTime) {
        long diff = endDateTime.getTime() - startDateTime.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000) + 1;
        int daysdiff = (int) diffDays;
        return daysdiff;
    }
}
