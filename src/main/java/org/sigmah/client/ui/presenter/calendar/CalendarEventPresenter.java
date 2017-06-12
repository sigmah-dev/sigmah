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

        FormPanel getForm();

        TextField<String> getEventSummaryField();

        DateField getEventDateStartField();

        DateField getEventDateEndField();

        TimeField getEventStartTimeField();

        TimeField getEventEndTimeField();

        TextArea getEventDescriptionField();

        Button getSaveButton();

        Button getCancelButton();

        CheckBox getAllDayCheckbox();

        FieldSet getPanelYearly();

        FieldSet getPanelMonthly();

        Radio getOnceRepeatRB();

        Radio getDailyRepeatRB();

        Radio getWeeklyRepeatRB();

        Radio getMonthlyRepeatRB();

        Radio getYearlyRepeatRB();

        RadioGroup getYearlyVariantRG();

        RadioGroup getMontlyVariantRG();

        Radio getYearlySameDayOfWeekRB();

        Radio getYearlySameDateRB();

        RadioGroup getRepeatEventPeriodRG();

        Radio getRadioMonthlySameDate();

        Radio getRadioMonthlySameDayOfWeek();

        void setShowAddEventView(boolean showAddEventView);
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
            // view.getForm().clearState();
            // view.initialize();
            view.setShowAddEventView(true);
            if (view.getRepeatEventPeriodRG() != null) {
                view.getRepeatEventPeriodRG().enable();
                view.getRepeatEventPeriodRG().show();
                view.getOnceRepeatRB().setValue(true);
                view.getPanelMonthly().hide();
                view.getPanelYearly().hide();
                //view.getMontlyVariantRG().show();
                //view.getYearlyVariantRG().show();
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
            // view.getRepeatEventPeriodRG().show();
            // return;
            //view.getRepeatEventPeriodRG().show();
        } else {// EDIST EXISTING EVENT
            //view.setShowAddEventView(false);
            //view.getRepeatEventPeriodRG().hide();
            //--->>               }

            this.calendarWrapper = new CalendarWrapper(event.getParent());
            view.setShowAddEventView(false);
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

            }
//                if(creation) {
//                    view.getIsAllDayCheckbox().hide();
//                } else {// CREATE NEW EVENT NEW BELOW
//                    view.getRepeatEventPeriodRG().hide();
//                }
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

        final Date date = view.getEventDateStartField().getValue();
        properties.put(Event.DATE, date);
//              properties.put("YYYYYYY", (view.getMontlyVariantRG().getValue().toString()?"true":"false"));
//		Radio getMontlyVariantRG = view.getMontlyVariantRG()!=null? view.getMontlyVariantRG().getValue():null;
//              Boolean getDailyRepeatRB = view.getDailyRepeatRB()!=null? view.getDailyRepeatRB().getValue(): Boolean.FALSE;
//              Boolean getOnceRepeatRB = view.getOnceRepeatRB().getValue();
//              Boolean getYearlyRepeatRB = view.getYearlyRepeatRB().getValue();
//                //properties.put("getYearlyRepeatRB", (view.getYearlyRepeatRB().getValue()?"true":"false"));
//              Boolean getYearlySameDayOfWeekRB = view.getYearlySameDayOfWeekRB().getValue();

        final Date startDate = view.getEventStartTimeField().getDateValue();
        if (startDate != null) {
            startDate.setYear(date.getYear());
            startDate.setMonth(date.getMonth());
            startDate.setDate(date.getDate());
            properties.put(Event.START_TIME, startDate.getTime());
        } else {
            properties.put(Event.START_TIME, null);
        }

        final Date endDate = view.getEventEndTimeField().getDateValue();
        if (endDate != null) {
            endDate.setYear(date.getYear());
            endDate.setMonth(date.getMonth());
            endDate.setDate(date.getDate());
            properties.put(Event.END_TIME, endDate.getTime());
        } else {
            properties.put(Event.END_TIME, null);
        }

        properties.put(Event.DESCRIPTION, view.getEventDescriptionField().getValue());

        if (event == null) {
            addPersonalEvent(properties);
            //Here process properties and define repetable events dates
            /*take parameters from View 
 Start Date, get End date, period settings like Once, Daily, Monthly etc
 process values to prepare new properties for new event
 if needed calculate same day of week for Monthly or Yearly
 if needed compare if the Date exist in Month  (like 29 30 31 Febr etc)
 if needed define weekends
 after that in cycle add new events
             */

            Date theDate = (Date) properties.get(Event.DATE);
//                      long theStartTime =  ((Long)properties.get(Event.START_TIME)).longValue();
//                        long theEndTime = ((Long) properties.get(Event.END_TIME)).longValue();
            Date dayAfter = new Date(theDate.getTime() + (24 * 60 * 60 * 1000));
            long theStartTimeNext = startDate.getTime() + (24 * 60 * 60 * 1000);
            long theEndTimeNext = endDate.getTime() + (24 * 60 * 60 * 1000);
            //LocalDateTime.from(theDate.toInstant().atZone(ZoneId.of("UTC"))).plusDays(1);
            properties.put(Event.DATE, dayAfter);
            properties.put(Event.START_TIME, theStartTimeNext);
            properties.put(Event.END_TIME, theEndTimeNext);
            String newSummary = (String) properties.get(Event.SUMMARY);
            newSummary += " (repeat + 1 day)";
            properties.put(Event.SUMMARY, newSummary);
            properties.put(Event.SUMMARY, properties.get(Event.SUMMARY) + " (This is repeated Daily event.)");
            addPersonalEvent(properties);
//Here process properties and define repetable events dates
        } else {
            editPersonalEvent(event, properties);
//                        properties.remove(Event.START_TIME);
//                        properties.remove(Event.END_TIME);
            //properties.remove(Event.DATE);
        }
//		if (event == null) {
//			addPersonalEvent(properties);
//
//		} else {
//			editPersonalEvent(event, properties);
//		}
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

}