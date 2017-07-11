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
//import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.user.client.Window;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import static com.google.gwt.core.ext.TreeLogger.NULL;
import com.google.gwt.user.client.Timer;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Calendar event presenter which manages the {@link CalendarEventView}.
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class CalendarEventPresenter extends AbstractPagePresenter<CalendarEventPresenter.View> {

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
        Radio getRadioMonthlySameDate();

        /**
         *
         * @return
         */
        Radio getRadioMonthlySameDayOfWeek();


       // void setShowAddEventView(boolean showAddEventView);
    }

    /**
     * The edited calendar event, or {@code null} if creation.
     */
    private Event event;

    private CalendarWrapper calendarWrapper;

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

            view.getYearlyVariantRG().hide();
            view.getMontlyVariantRG().hide();
            view.getRepeatEventPeriodRG().hide();
            view.getMonthlyRepeatRB().hide();
            view.getYearlySameDateRB().hide();
            view.getYearlySameDayOfWeekRB().hide();
            view.getRadioMonthlySameDate().hide();
            view.getRadioMonthlySameDayOfWeek().hide();
            
            view.getEventDateEndField().clear();
            view.getEventDateEndField().hide();
        }

        view.getEventStartTimeField().show();
        view.getEventEndTimeField().show();

        if (!isFullDayEvent(event)) {

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

        view.getYearlyVariantRG().show();
        view.getMontlyVariantRG().show();
        view.getRepeatEventPeriodRG().show();
        view.getMonthlyRepeatRB().show();
        view.getYearlySameDateRB().show();
        view.getYearlySameDayOfWeekRB().show();
        view.getRadioMonthlySameDate().show();
        view.getRadioMonthlySameDayOfWeek().show();
        view.getEventDateEndField().show();
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

        Boolean isFullDayEvent = view.getAllDayCheckbox().getValue();

        Window.alert("isAllDayEvent=" + isFullDayEvent);//temp for checker

        Date startDate = createStartDateProperty(isFullDayEvent, beginEventIntervalDate, properties);
        Date endDate = createEndDateProperty(isFullDayEvent, beginEventIntervalDate, properties);

        properties.put(Event.DESCRIPTION, view.getEventDescriptionField().getValue());
      

        if (event == null) {
            processAddEvent(endEventIntervalDate, beginEventIntervalDate, startDate, endDate, properties, eventSummary, eventDescription);
        } else {
        properties.put(Event.EVENT_TYPE, event.getEventType());
        properties.put(Event.REFERENCE_ID, event.getReferenceId());  
            editPersonalEvent(event, properties);
        }
    }

    /**
     * Add new calendar events processing.
     * @param endEventIntervalDate the value of endEventIntervalDate
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param startDate the value of startDate
     * @param endDate the value of endDate
     * @param properties the value of properties
     * @param eventSummary the value of eventSummary
     * @param eventDescription the value of eventDescription
     */
    private void processAddEvent(Date endEventIntervalDate, final Date beginEventIntervalDate, Date startDate, Date endDate, final Map<String, Serializable> properties, String eventSummary, String eventDescription) {
        long milisPerDay = 86400000; //24 * 60 * 60 * 1000)
        long milisPerWeek = 7 * milisPerDay; //7 days * (24hour * 60minutes * 60seconds * 1000mili seconds)
        long diffInMilis = endEventIntervalDate.getTime() - beginEventIntervalDate.getTime();

        //Boolean isOnceRepeatEvent = view.getOnceRepeatRB().getValue();
        // Window.alert("isOnceRepeatEvent=" + isOnceRepeatEvent);//temp for checker

        //if(!isOnceRepeatEvent.booleanValue()){
        //Here process properties and define repetable events dates
        /*take parameters from View
        Start Date, get End beginEventIntervalDate, period settings like Once, Daily, Monthly etc
        process values to prepare new properties for new event
        if needed calculate same day of week for Monthly or Yearly
        if needed compare if the Date exist in Month  (like 29 30 31 Febr etc)
        if needed define weekends
        after that in cycle add new events
         */
//            Boolean isFullDayEvent = view.getAllDayCheckbox().getValue();
//
//            Window.alert("isFullDayEvent=" + isFullDayEvent);//temp for checker
        Boolean isDailyRepeatEvent = view.getDailyRepeatRB() != null ? view.getDailyRepeatRB().getValue() : Boolean.FALSE;
        Boolean isWeeklyRepeatEvent = view.getWeeklyRepeatRB().getValue();
        Boolean isMonthlyRepeatEvent = view.getMonthlyRepeatRB().getValue();
        Boolean isYearlyRepeatEvent = view.getYearlyRepeatRB().getValue();

        Boolean isMonthlySameDayOfWeek = view.getRadioMonthlySameDayOfWeek().getValue();
        //Boolean isMonthlySameDate = view.getRadioMonthlySameDate().getValue();

        Boolean isYearlySameDayOfWeek = view.getYearlySameDayOfWeekRB().getValue();
       // Boolean isYearlySameDate = view.getYearlySameDateRB().getValue();

//Window.alert("isYearlySameDayOfWeek=" + isYearlySameDayOfWeek);//temp for checker
//Window.alert("isYearlySameDate=" + isYearlySameDate);//temp for checker
        if (isDailyRepeatEvent) {
            processDailyEvents(diffInMilis, milisPerDay, beginEventIntervalDate, startDate, endDate, properties, eventSummary, eventDescription);
        } else if (isWeeklyRepeatEvent) {
            processWeeklyEvents(diffInMilis, milisPerWeek, beginEventIntervalDate, startDate, endDate, properties, eventSummary, eventDescription);
        } else if (isMonthlyRepeatEvent) {
//    Window.alert("MONTHLY : " + (isMonthlySameDayOfWeek?"Same DAY of a Week":"")
//            +(isMonthlySameDate?"Same DATE":""));//temp for checker
            processMonthlyEvents(beginEventIntervalDate, endEventIntervalDate, properties, isMonthlySameDayOfWeek, startDate, endDate, eventSummary, eventDescription);
        } else if (isYearlyRepeatEvent) {
//    Window.alert("YEARLY : " + (isYearlySameDayOfWeek ? "Same DAY of a Week" : "")
//            + (isYearlySameDate ? "Same DATE" : ""));//temp for checker
            processYearEvents(beginEventIntervalDate, endEventIntervalDate, properties, isYearlySameDayOfWeek, startDate, endDate, eventSummary, eventDescription);
        }
        else{
            processOnceEvent(startDate, endDate, properties, eventSummary, eventDescription);
        }
    }

    /**
     * Create Start event date property
     * @param isFulllDayEvent the value of isFullDayEvent
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param properties the value of properties
     */
    private Date createStartDateProperty(Boolean isFulllDayEvent, final Date beginEventIntervalDate, final Map<String, Serializable> properties) {

        Date startDate = null;
        if (!isFulllDayEvent) {
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
     * Create End event date property
     * @param isFulllDayEvent the value of isFullDayEvent
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param properties the value of properties
     */
    private Date createEndDateProperty(Boolean isFulllDayEvent, final Date beginEventIntervalDate, final Map<String, Serializable> properties) {

        Date endDate = null;
        if (!isFulllDayEvent) {
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

     private void processOnceEvent(final Date startDate, final Date endDate, final Map<String, Serializable> properties, String eventSummary, String eventDescription){
                 properties.put(Event.SUMMARY, (String) properties.get(Event.SUMMARY));
                 properties.put(Event.DESCRIPTION, (String) properties.get(Event.DESCRIPTION));
               //  properties.put(Event.EVENT_TYPE, "O");
             //    properties.put(Event.REFERENCE_ID, 1);
                 addPersonalEvent(properties);
     }
     
       /**
     * Add new Daily calendar events processing.
     * @param diffInMilis the value of diffInMilis
     * @param milisPerDay the value of milisPerDay
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param startDate the value of startDate
     * @param endDate the value of endDate
     * @param properties the value of properties
     * @param eventSummary the value of eventSummary
     * @param eventDescription the value of eventDescription
     */
    private void processDailyEvents(long diffInMilis, long milisPerDay, final Date beginEventIntervalDate, final Date startDate, final Date endDate, final Map<String, Serializable> properties, String eventSummary, String eventDescription) {
        int daysInterval = 0;
        long daysDiff = diffInMilis / milisPerDay + 1;
        daysInterval = (int) daysDiff;

        long calBeginNextEventDateLong = beginEventIntervalDate.getTime();
        Date calBeginNextEventDate = beginEventIntervalDate;

        if (daysInterval > 1) {
            properties.put(Event.SUMMARY, (String) properties.get(Event.SUMMARY) + " ( 1 of " + daysInterval + ")");
            properties.put(Event.DESCRIPTION, (String) properties.get(Event.DESCRIPTION) + " (Daily event 1 of " + daysInterval + ")");
            properties.put(Event.EVENT_TYPE, "D");
        }
       addPersonalEventDaily(properties,daysInterval, calBeginNextEventDateLong, milisPerDay, startDate, endDate, eventSummary, eventDescription);
     
    }
   private void addPersonalEventDaily(final Map<String, Serializable> properties, final int daysInterval, final long calBeginNextEventDateLong, final long milisPerDay, final Date startDate, final Date endDate, final String eventSummary, final String eventDescription) {

        final CreateEntity createEntity = new CreateEntity(PersonalEventDTO.ENTITY_NAME, properties);

        dispatch.execute(createEntity, new CommandResultHandler<CreateResult>() {

            @Override
            public void onCommandFailure(final Throwable caught) {
                if (Log.isErrorEnabled()) {
                    Log.error(I18N.CONSTANTS.calendarAddEventError(), caught);
                }
                N10N.error(I18N.CONSTANTS.error(), I18N.CONSTANTS.calendarAddEventError());
            }

            @Override
            public void onCommandSuccess(final CreateResult result) {

                // Creating events.
                final Event event = new Event();
                event.setIdentifier((Integer) result.getEntity().getId());
                properties.put(Event.REFERENCE_ID, (Integer) result.getEntity().getId());
                updateEvent(event, properties);
              //  properties.put(Event.REFERENCE_ID, (Integer) result.getEntity().getId());
                addDailySeriesEvent((String) result.getEntity().getId(), daysInterval, calBeginNextEventDateLong, milisPerDay, startDate, endDate, eventSummary, eventDescription);
            }
        }, view.getCancelButton(), view.getSaveButton());
    }
     private void addDailySeriesEvent(String ids, int daysInterval, long calBeginNextEventDateLong, long milisPerDay, final Date startDate, final Date endDate, String eventSummary, String eventDescription) {
        Date calBeginNextEventDate;
 
        for (int i = 1; i < daysInterval; i++) {

            calBeginNextEventDateLong += milisPerDay;
            calBeginNextEventDate = new Date(calBeginNextEventDateLong);

            Map<String, Serializable> dailyProperties = new HashMap<String, Serializable>();
            dailyProperties.put(Event.CALENDAR_ID, calendarWrapper);
            dailyProperties.put(Event.SUMMARY, view.getEventSummaryField().getValue());

            dailyProperties.put(Event.DATE, calBeginNextEventDate);
            
            setFullDayEvent(startDate, endDate, calBeginNextEventDate, dailyProperties);

            String newSummary = eventSummary;
            String newDescription = eventDescription;
            newSummary += " (Daily event " + (i + 1) + " of " + daysInterval + ")";
            newDescription += " (Daily event " + (i + 1) + " of " + daysInterval + ")";
            dailyProperties.put(Event.SUMMARY, newSummary);
            dailyProperties.put(Event.DESCRIPTION, newDescription);
            dailyProperties.put(Event.EVENT_TYPE, "D");
            dailyProperties.put(Event.REFERENCE_ID, ids);
            addPersonalEvent(dailyProperties);
        }
    }
    /**
     * Add new Weekly calendar events processing.
     * @param diffInMilis the value of diffInMilis
     * @param milisPerWeek the value of milisPerWeek
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param startDate the value of startDate
     * @param endDate the value of endDate
     * @param properties the value of properties
     * @param eventSummary the value of eventSummary
     * @param eventDescription the value of eventDescription
     */
    private void processWeeklyEvents(long diffInMilis, long milisPerWeek, final Date beginEventIntervalDate, final Date startDate, final Date endDate, final Map<String, Serializable> properties, String eventSummary, String eventDescription) {
        long weekDiff = diffInMilis / milisPerWeek + 1;
        int weeksInterval = (int) weekDiff;

        long calBeginNextEventDateLong = beginEventIntervalDate.getTime();
        Date calBeginNextEventDate = beginEventIntervalDate;

        if (weeksInterval > 1) {
            properties.put(Event.SUMMARY, (String) properties.get(Event.SUMMARY) + " (Weekly event 1 of " + weeksInterval + ")");
            properties.put(Event.DESCRIPTION, (String) properties.get(Event.DESCRIPTION) + " (Weekly event 1 of " + weeksInterval + ")");
            properties.put(Event.EVENT_TYPE, "W");
        }
        addPersonalEventWeekly(properties,weeksInterval, calBeginNextEventDateLong, milisPerWeek, startDate, endDate, eventSummary, eventDescription);

    }
   private void addPersonalEventWeekly(final Map<String, Serializable> properties, final int daysInterval, final long calBeginNextEventDateLong, final long milisPerDay, final Date startDate, final Date endDate, final String eventSummary, final String eventDescription) {

        final CreateEntity createEntity = new CreateEntity(PersonalEventDTO.ENTITY_NAME, properties);

        dispatch.execute(createEntity, new CommandResultHandler<CreateResult>() {

            @Override
            public void onCommandFailure(final Throwable caught) {
                if (Log.isErrorEnabled()) {
                    Log.error(I18N.CONSTANTS.calendarAddEventError(), caught);
                }
                N10N.error(I18N.CONSTANTS.error(), I18N.CONSTANTS.calendarAddEventError());
            }

            @Override
            public void onCommandSuccess(final CreateResult result) {

                // Creating events.
                final Event event = new Event();
                event.setIdentifier((Integer) result.getEntity().getId());
                     properties.put(Event.REFERENCE_ID, (Integer) result.getEntity().getId());
                updateEvent(event, properties);
               // properties.put(Event.REFERENCE_ID, (Integer) result.getEntity().getId());
                addWeeklySeriesEvent((String) result.getEntity().getId(), daysInterval, calBeginNextEventDateLong, milisPerDay, startDate, endDate, eventSummary, eventDescription);
            }
        }, view.getCancelButton(), view.getSaveButton());
    }
     private void addWeeklySeriesEvent(String ids, int weeksInterval, long calBeginNextEventDateLong, long milisPerWeek, final Date startDate, final Date endDate, String eventSummary, String eventDescription) {
        Date calBeginNextEventDate;
 
        for (int i = 1; i < weeksInterval; i++) {

            calBeginNextEventDateLong += milisPerWeek;
            calBeginNextEventDate = new Date(calBeginNextEventDateLong);

            Map<String, Serializable> weeklyProperties = new HashMap<String, Serializable>();
            weeklyProperties.put(Event.CALENDAR_ID, calendarWrapper);
            weeklyProperties.put(Event.SUMMARY, view.getEventSummaryField().getValue());

            weeklyProperties.put(Event.DATE, calBeginNextEventDate);

            setFullDayEvent(startDate, endDate, calBeginNextEventDate, weeklyProperties);

            String newSummary = eventSummary;
            String newDescription = eventDescription;
            newSummary += " (Weekly event " + (i + 1) + " of " + weeksInterval + ")";
            newDescription += " (Weekly event " + (i + 1) + " of " + weeksInterval + ")";
            weeklyProperties.put(Event.SUMMARY, newSummary);
            weeklyProperties.put(Event.DESCRIPTION, newDescription);
            weeklyProperties.put(Event.EVENT_TYPE, "W");
            weeklyProperties.put(Event.REFERENCE_ID, ids);
            addPersonalEvent(weeklyProperties);
        }
    }
    /**
     * Set date and time values for Full day events.
     * @param startDate the value of startDate
     * @param endDate the value of endDate
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
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param endEventIntervalDate the value of endEventIntervalDate
     * @param properties the value of properties
     * @param isMonthlySameDayOfWeek the value of isMonthlySameDayOfWeek
     * @param startDate the value of startDate
     * @param endDate the value of endDate
     * @param eventSummary the value of eventSummary
     * @param eventDescription the value of eventDescription
     */
    private void processMonthlyEvents(final Date beginEventIntervalDate, final Date endEventIntervalDate, final Map<String, Serializable> properties, Boolean isMonthlySameDayOfWeek, final Date startDate, final Date endDate, String eventSummary, String eventDescription) {
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

        if (monthInterval > 1) {
            properties.put(Event.SUMMARY, (String) properties.get(Event.SUMMARY) + " (Monthly event 1 of " + monthInterval + ")");
            properties.put(Event.DESCRIPTION, (String) properties.get(Event.DESCRIPTION) + " (Monthly event 1 of " + monthInterval + ")");
            if(isMonthlySameDayOfWeek) properties.put(Event.EVENT_TYPE, "M1");
            else properties.put(Event.EVENT_TYPE, "M2");
        }
        addPersonalEventMonthly(properties, monthInterval, beginEventIntervalDate, endEventIntervalDate, startDate, endDate, eventSummary, eventDescription, isMonthlySameDayOfWeek);
    }
   private void addPersonalEventMonthly(final Map<String, Serializable> properties, final int monthInterval, final Date beginEventIntervalDate, final Date endEventIntervalDate, final Date startDate, final Date endDate, final String eventSummary, final String eventDescription, final boolean isMonthlySameDayOfWeek) {

        final CreateEntity createEntity = new CreateEntity(PersonalEventDTO.ENTITY_NAME, properties);

        dispatch.execute(createEntity, new CommandResultHandler<CreateResult>() {

            @Override
            public void onCommandFailure(final Throwable caught) {
                if (Log.isErrorEnabled()) {
                    Log.error(I18N.CONSTANTS.calendarAddEventError(), caught);
                }
                N10N.error(I18N.CONSTANTS.error(), I18N.CONSTANTS.calendarAddEventError());
            }

            @Override
            public void onCommandSuccess(final CreateResult result) {

                // Creating events.
                final Event event = new Event();
                event.setIdentifier((Integer) result.getEntity().getId());
                     properties.put(Event.REFERENCE_ID, (Integer) result.getEntity().getId());
                updateEvent(event, properties);
              //  properties.put(Event.REFERENCE_ID, (Integer) result.getEntity().getId());
                addMonthlySeriesEvent((String) result.getEntity().getId(), monthInterval, beginEventIntervalDate, endEventIntervalDate, startDate, endDate, eventSummary, eventDescription,isMonthlySameDayOfWeek);
            }
        }, view.getCancelButton(), view.getSaveButton());
    }
     private void addMonthlySeriesEvent(String ids, int monthInterval, Date beginEventIntervalDate, Date endEventIntervalDate, final Date startDate, final Date endDate, String eventSummary, String eventDescription, final boolean isMonthlySameDayOfWeek) {
     Date calBeginNextEventDate = beginEventIntervalDate;
        for (int i = 1; i < monthInterval; i++) {
            //calBeginNextEventDate = getMonthlySameDate(beginEventIntervalDate, calBeginNextEventDate, i);
            calBeginNextEventDate = getMonthlySameDayOfWeek2(beginEventIntervalDate, calBeginNextEventDate, i, isMonthlySameDayOfWeek);

            Map<String, Serializable> monthlyProperties = new HashMap<String, Serializable>();
            monthlyProperties.put(Event.CALENDAR_ID, calendarWrapper);
            monthlyProperties.put(Event.SUMMARY, view.getEventSummaryField().getValue());

            monthlyProperties.put(Event.DATE, calBeginNextEventDate);

            setFullDayEvent(startDate, endDate, calBeginNextEventDate, monthlyProperties);

            String newSummary = eventSummary;
            String newDescription = eventDescription;
            newSummary += " (Monthly event " + (i + 1) + " of " + monthInterval + ")";
            newDescription += " (Monthly " + (isMonthlySameDayOfWeek ? "same Day of a week " : "same Date ") + "event " + (i + 1) + " of " + monthInterval + ")";
            monthlyProperties.put(Event.SUMMARY, newSummary);
            monthlyProperties.put(Event.DESCRIPTION, newDescription);
            if(isMonthlySameDayOfWeek) monthlyProperties.put(Event.EVENT_TYPE, "M1");
            else monthlyProperties.put(Event.EVENT_TYPE, "M2");
            monthlyProperties.put(Event.REFERENCE_ID, ids);
            addPersonalEvent(monthlyProperties);
        }
    }
    /**
     * Add new Yearly calendar events processing.
     * @param beginEventIntervalDate the value of beginEventIntervalDate
     * @param endEventIntervalDate the value of endEventIntervalDate
     * @param properties the value of properties
     * @param isYearlySameDayOfWeek the value of isYearlySameDayOfWeek
     * @param startDate the value of startDate
     * @param endDate the value of endDate
     * @param eventSummary the value of eventSummary
     * @param eventDescription the value of eventDescription
     */
    private void processYearEvents(final Date beginEventIntervalDate, final Date endEventIntervalDate, final Map<String, Serializable> properties, Boolean isYearlySameDayOfWeek, final Date startDate, final Date endDate, String eventSummary, String eventDescription) {
        int yearStart = beginEventIntervalDate.getYear();
        int yearEnd = endEventIntervalDate.getYear();
        int yearInterval = yearEnd - yearStart + 1;

        if (yearInterval > 1) {
            properties.put(Event.SUMMARY, (String) properties.get(Event.SUMMARY) + " (Yearly event 1 of " + yearInterval + ")");
            properties.put(Event.DESCRIPTION, (String) properties.get(Event.DESCRIPTION) + " (Yearly " + (isYearlySameDayOfWeek ? "same Day of a week " : "same Date ") + "event 1 of " + yearInterval + ")");
            if(isYearlySameDayOfWeek) properties.put(Event.EVENT_TYPE, "Y1");
            else properties.put(Event.EVENT_TYPE, "Y2");
        }
        addPersonalEventYearly(properties, yearInterval, beginEventIntervalDate, endEventIntervalDate, startDate, endDate, eventSummary, eventDescription, isYearlySameDayOfWeek);

    }
   private void addPersonalEventYearly(final Map<String, Serializable> properties, final int yearInterval, final Date beginEventIntervalDate, final Date endEventIntervalDate, final Date startDate, final Date endDate, final String eventSummary, final String eventDescription, final boolean isYearlySameDayOfWeek) {

        final CreateEntity createEntity = new CreateEntity(PersonalEventDTO.ENTITY_NAME, properties);

        dispatch.execute(createEntity, new CommandResultHandler<CreateResult>() {

            @Override
            public void onCommandFailure(final Throwable caught) {
                if (Log.isErrorEnabled()) {
                    Log.error(I18N.CONSTANTS.calendarAddEventError(), caught);
                }
                N10N.error(I18N.CONSTANTS.error(), I18N.CONSTANTS.calendarAddEventError());
            }

            @Override
            public void onCommandSuccess(final CreateResult result) {

                // Creating events.
                final Event event = new Event();
                event.setIdentifier((Integer) result.getEntity().getId());
                     properties.put(Event.REFERENCE_ID, (Integer) result.getEntity().getId());
                updateEvent(event, properties);
              //  properties.put(Event.REFERENCE_ID, (Integer) result.getEntity().getId());
                addYearlySeriesEvent((String) result.getEntity().getId(), yearInterval, beginEventIntervalDate, endEventIntervalDate, startDate, endDate, eventSummary, eventDescription,isYearlySameDayOfWeek);
            }
        }, view.getCancelButton(), view.getSaveButton());
    }
     private void addYearlySeriesEvent(String ids, int yearInterval, Date beginEventIntervalDate, Date endEventIntervalDate, final Date startDate, final Date endDate, String eventSummary, String eventDescription, final boolean isYearlySameDayOfWeek) {
     Date calBeginNextEventDate = beginEventIntervalDate;
        for (int i = 1; i < yearInterval; i++) {
            calBeginNextEventDate = getYearlySameDayOfWeek(beginEventIntervalDate, calBeginNextEventDate, i, isYearlySameDayOfWeek);

            Map<String, Serializable> yearlyProperties = new HashMap<String, Serializable>();
            yearlyProperties.put(Event.CALENDAR_ID, calendarWrapper);
            yearlyProperties.put(Event.SUMMARY, view.getEventSummaryField().getValue());

            yearlyProperties.put(Event.DATE, calBeginNextEventDate);

            setFullDayEvent(startDate, endDate, calBeginNextEventDate, yearlyProperties);

            String newSummary = eventSummary;
            String newDescription = eventDescription;
            newSummary += " (Yearly event " + (i + 1) + " of " + yearInterval + ")";
            newDescription += " (Yearly " + (isYearlySameDayOfWeek ? "same Day of a week " : "same Date ") + "event " + (i + 1) + " of " + yearInterval + ")";
            yearlyProperties.put(Event.SUMMARY, newSummary);
            yearlyProperties.put(Event.DESCRIPTION, newDescription);
            if(isYearlySameDayOfWeek) yearlyProperties.put(Event.EVENT_TYPE, "Y1");
            else yearlyProperties.put(Event.EVENT_TYPE, "Y2");
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
                if (Log.isErrorEnabled()) {
                    Log.error(I18N.CONSTANTS.calendarAddEventError(), caught);
                }
                N10N.error(I18N.CONSTANTS.error(), I18N.CONSTANTS.calendarAddEventError());
            }

            @Override
            public void onCommandSuccess(final CreateResult result) {

                // Creating events.
                final Event event = new Event();
                event.setIdentifier((Integer) result.getEntity().getId());
                   //  properties.put(Event.REFERENCE_ID, result.getEntity().getId());
                    // Window.alert("ID1 = " + result.getEntity().getId());
                    // Window.alert("ID2 = " + properties.get(Event.REFERENCE_ID));
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
                if (Log.isErrorEnabled()) {
                    Log.error(I18N.CONSTANTS.calendarAddEventError(), caught);
                }
                N10N.error(I18N.CONSTANTS.error(), I18N.CONSTANTS.calendarAddEventError());
            }

            @Override
            public void onCommandSuccess(final VoidResult result) {

                final Calendar calendar = event.getParent();

                final List<Event> oldEventList = calendar.getEvents().get(event.getKey());
                oldEventList.remove(event);

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
        final Object startHourSerialized = properties.get(Event.START_TIME);
        final Object endHourSerialized = properties.get(Event.END_TIME);

        if (startHourSerialized != null) {
            final Date startHour = new Date((Long) startHourSerialized);
            event.setDtstart(startHour);
            if (endHourSerialized != null) {
                final Date endHour = new Date((Long) endHourSerialized);
                event.setDtend(endHour);
            } else {
                event.setDtend(null);
            }

        } else {
            event.setDtstart(new Date(day.getYear(), day.getMonth(), day.getDate()));
            event.setDtend(new Date(day.getYear(), day.getMonth(), day.getDate() + 1));
        }

        // Adding the new event to the calendar
        final CalendarWrapper wrapper = (CalendarWrapper) properties.get(Event.CALENDAR_ID);
        final Calendar calendar = wrapper.getCalendar();

        event.setParent(calendar);

        List<Event> events = calendar.getEvents().get(day);
        if (events == null) {
            events = new ArrayList<Event>();
            calendar.getEvents().put(day, events);
        }
        events.add(event);

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
/*
    @SuppressWarnings({"deprecation"})
    private Date getMonthlySameDayOfWeek(Date dateObject, int numberMonths) {
        Date firstDate = new Date();
        firstDate.setYear(dateObject.getYear());
        firstDate.setMonth(dateObject.getMonth());
        firstDate.setDate(dateObject.getDate());

        dateObject.setDate(10);// to prevent month slip past (f.e. 31 jan -> 3 march instead of 29 febr)
        dateObject.setMonth(dateObject.getMonth() + 1);

        int daysInStartDate = getDaysInMonth(firstDate.getYear(), firstDate.getMonth());

        if (firstDate.getDate() == daysInStartDate) {//if last day of the month
            dateObject.setDate(getDaysInMonth(dateObject.getYear(), dateObject.getMonth()));
        } else {
            dateObject.setDate(firstDate.getDate());//??
        }

        Date newDate = getSameWeekDay(firstDate, dateObject);

        Window.alert("Date start: Month:" + firstDate.getMonth()
                + " | " + firstDate.getDay() + "| Date:" + getDaysInMonth(firstDate.getYear(), firstDate.getMonth()) + "  ; " + firstDate.toLocaleString() + " : month " + numberMonths + ": Result " + " | " + dateObject.getDay() + " | " + dateObject.toLocaleString()
                + " || " + newDate.getDay() + " || " + newDate.toLocaleString());//temp for checker

        return newDate;
    }
*/
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

        Window.alert("Date start: Month:" + firstDate.getMonth()
                + " | " + firstDate.getDay() + "| Date:" + getDaysInMonth(nextDateNew.getYear(), nextDateNew.getMonth())
                + "  ; " + firstDate.toLocaleString() + " : month " + numberMonths
                + ": Result " + " | " + newDate.getDay() + " | " + newDate.toLocaleString());//temp for checker

        return newDate;
    }
        
    /**
     * Calculates same date or same day of week in next month.
     * @param firstDate
     * @param nextDateOld
     * @param numberMonths
     * @param isSameDayOfWeek if the value is true then will be calculated Date of the nearest same day of week
     * @return next month date
     */
    public Date getMonthlySameDayOfWeek2(Date firstDate, Date nextDateOld, int numberMonths, boolean isSameDayOfWeek) {

        Date nextDateNew = new Date();
        Date newDate = new Date();
        nextDateNew.setDate(1);// to prevent month slip past (f.e. 31 jan -> 3 march instead of 29 febr)
        newDate.setDate(1);
        nextDateNew.setYear(nextDateOld.getYear());
        nextDateNew.setMonth(nextDateOld.getMonth() + 1);
        //nextDateNew.setDate(1);// to prevent month slip past (f.e. 31 jan -> 3 march instead of 29 febr)
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

            //return newDate;
            newDate = newDate2;
        }
        Window.alert("Monthly " + (isSameDayOfWeek ? "Same week DAY " : "Same DATE ") + "Date start: Month:" + firstDate.getMonth() + " | " + firstDate.getDay()
                + "| " + getDaysInMonth(firstDate.getYear(), firstDate.getMonth())
                + " | " + firstDate.toLocaleString() + " | i= " + numberMonths + " | RESULT  | Prev | "
                + nextDateNew.getDay() + " | " + nextDateNew.toLocaleString()
                + " || New | " + newDate.getDay() + " | " + newDate.toLocaleString());//temp for checker

        return newDate;
    }

    /**
     * Calculates same date or same day of week in next year.
     * @param firstDate
     * @param nextDateOld
     * @param numberMonths
     * @param isSameDayOfWeek if the value is true then will be calculated Date of the nearest same day of week
     * @return
     */
    @SuppressWarnings({"deprecation"})
    public Date getYearlySameDayOfWeek(Date firstDate, Date nextDateOld, int numberMonths, boolean isSameDayOfWeek) {

        Date nextDateNew = new Date();
        nextDateNew.setDate(1);
        nextDateNew.setYear(nextDateOld.getYear());
        nextDateNew.setMonth(nextDateOld.getMonth());
       // nextDateNew.setDate(nextDateOld.getDate());

       // nextDateNew.setDate(1);
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

        Window.alert("Yearly " + (isSameDayOfWeek ? "Same week DAY " : "Same DATE ") + "Date start: Month:" + firstDate.getMonth() + " | " + firstDate.getDay()
                + "| " + getDaysInMonth(firstDate.getYear(), firstDate.getMonth())
                + " | " + firstDate.toLocaleString() + " | i= " + numberMonths + " | RESULT  | Prev | "
                + nextDateOld.getDay() + " | " + nextDateOld.toLocaleString()
                + " || New | " + nextDateNew.getDay() + " | " + nextDateNew.toLocaleString());//temp for checker

        return nextDateNew;
    }

    /**
     * Calculates same week day date(as in firstDate) for nextDate
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
               // newDate = new Date(curDate.getTime() + (7 - (dayOfFirst - dayOfCurrent)) * milSecInDay);
                newDate = new Date(curDate.getTime() + (7 - (dayOfCurrent - dayOfFirst)) * milSecInDay);
            }
        } else {
            newDate = new Date(curDate.getTime());
        }
        return newDate;
    }
/*    
        private Date getSameWeekDay22(Date firstDate, Date nextDate) {
        //Calculate same day of week
        int dayOfFirst = firstDate.getDay();
        int dayOfCurrent = nextDate.getDay();
        Date curDate = nextDate;
        int milSecInDay = 86400000;//24 * 60 * 60 * 1000
        Date newDate = new Date();
        if (dayOfFirst > dayOfCurrent) {
            newDate = new Date(curDate.getTime() + (dayOfFirst - dayOfCurrent) * milSecInDay);
            if (newDate.getMonth() != curDate.getMonth()) { //>
                newDate = new Date(curDate.getTime() - (7 - (dayOfFirst - dayOfCurrent)) * milSecInDay);
            }
            
        } else if (dayOfFirst < dayOfCurrent) {//<
            newDate = new Date(curDate.getTime() - (dayOfCurrent - dayOfFirst) * milSecInDay);
            if (newDate.getMonth() != curDate.getMonth()) {
                //newDate2 = new Date(curDate.getTime() + (7 - (dayOfFirst - dayOfCurrent)) * milSecInDay);
                newDate = new Date(curDate.getTime() + (7 - (dayOfCurrent - dayOfFirst)) * milSecInDay);
            }
        } else {
            newDate = new Date(curDate.getTime());
        }
        return newDate;
    }
    private Date getSameWeekDay2(Date firstDate, Date nextDate) {
        //Calculate same day of week
        int dayOfFirst = firstDate.getDay();
        int dayOfCurrent = nextDate.getDay();
        Date newDate = new Date();
        Date curDate = nextDate;
        int milSecInDay = 86400000;//24 * 60 * 60 * 1000
        if (dayOfFirst > dayOfCurrent) {
            newDate = new Date(curDate.getTime() + (dayOfFirst - dayOfCurrent)
                    * milSecInDay);
            if (newDate.getMonth() != curDate.getMonth()) { // >
                newDate = new Date(curDate.getTime()
                        - (7 - (dayOfFirst - dayOfCurrent)) * milSecInDay);
            }

        } else if (dayOfFirst < dayOfCurrent) {// <
            newDate = new Date(curDate.getTime() - (dayOfCurrent - dayOfFirst)
                    * milSecInDay);
            if (newDate.getMonth() != curDate.getMonth()) {
                int dif = (dayOfFirst - dayOfCurrent) < 0 ? -(dayOfFirst - dayOfCurrent) : (dayOfFirst - dayOfCurrent);
                newDate = new Date(curDate.getTime()
                        + (7 - dif) * milSecInDay);
            }
        } else {
            newDate = new Date(curDate.getTime());
        }
        return newDate;
    }
 */       

    /**
     * Returns numver od days in the month of the year
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
                return daysInMonth;// = 31;
        }
    }
                
    /**
     * Validate if the year is leap year.
     * @param year the value of year
     * @return the boolean
     */
    private boolean isLeapYear(int year) {
        return (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0));
    }
}