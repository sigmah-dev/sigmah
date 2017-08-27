package org.sigmah.client.ui.view.calendar;

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
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.calendar.CalendarEventPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.calendar.Event;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.google.inject.Singleton;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.user.client.ui.HTML;
import java.util.Date;

/*
import com.gwtext.client.data.Record;  
import com.gwtext.client.data.SimpleStore;  
import com.gwtext.client.data.Store;  
import com.gwtext.client.widgets.Panel;  
import com.gwtext.client.widgets.form.ComboBox;  
import com.gwtext.client.widgets.form.FormPanel;  
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
 */
/**
 * Calendar event edit frame view used to create/edit a calendar event.
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class CalendarEventView extends AbstractPopupView<PopupWidget> implements CalendarEventPresenter.View {

    private FormPanel form;
    private TextField<String> eventSummaryField;
    private DateField eventDateStartField;
    private DateField eventDateEndField;
    private TimeField eventStartTimeField;
    private TimeField eventEndTimeField;
    private TextArea eventDescriptionField;

    private Button saveButton;
    private Button cancelButton;

    private CheckBox allDayCheckbox;
    private RadioGroup yearlyVariantRG;
    private RadioGroup montlyVariantRG;
    private FieldSet panelYearly;
    private FieldSet panelMonthly;
    private FieldSet panelWeekly;
    private FieldSet panelDaily;
    private FieldSet monthlyRepSettings;
    private FieldSet yearlyRepSettings;

    private Radio yearlySameDayOfWeekRB;
    private Radio yearlySameDateRB;

    private Radio onceRepeatRB;
    private Radio dailyRepeatRB;
    private Radio weeklyRepeatRB;
    private Radio monthlyRepeatRB;
    private Radio yearlyRepeatRB;
    private Radio radioMonthlySameDate;
    private Radio radioMonthlySameDayOfWeek;
    private Radio radioNumberOfRepetitions;
    private Radio radioRepetitionEndDate;

    private Radio radioWeeklyNumberOfRepetitions;
    private Radio radioWeeklyRepetitionEndDate;
    private Radio radioYearlyNumberOfRepetitions;
    private Radio radioYearlyRepetitionEndDate;
    private Radio radioDailyNumberOfRepetitions;
    private Radio radioDailyRepetitionEndDate;

    private DateField repetitionEndDate;
    private TextArea numberOfRepetitions;
    private DateField dailyRepetitionEndDate;
    private TextArea dailyNumberOfRepetitions;
    private DateField weeklyRepetitionEndDate;
    private TextArea weeklyNumberOfRepetitions;
    private DateField yearlyRepetitionEndDate;
    private TextArea yearlyNumberOfRepetitions;
    // private CheckBoxGroup allDayCheckboxGr;

    private RadioGroup RepeatEventPeriodRG;
    private RadioGroup RepeatMultiEventPeriodRG;
    private RadioGroup RepeatYearlyMultiEventPeriodRG;
    private RadioGroup RepeatWeeklyMultiEventPeriodRG;
    private RadioGroup RepeatDailyMultiEventPeriodRG;

    /**
     * Builds the view.
     */
    public CalendarEventView() {
        super(new PopupWidget(true), 500);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        form = Forms.panel();

        createYearlyPanel();
        createMonthlyPanel();
        createWeeklyPanel();
        createDailyPanel();
        createRepeatEventPeriodRadioGroup();

        eventSummaryField = Forms.text(I18N.CONSTANTS.calendarEventObject(), true);
        eventSummaryField.setName(Event.SUMMARY);

        eventDateStartField = Forms.date(I18N.CONSTANTS.calendar_addEvent_dateStart_label(), true);
        eventDateStartField.setName(Event.DATE);

        eventStartTimeField = Forms.time(I18N.CONSTANTS.calendarEventStartHour(), false);
        eventStartTimeField.setName(Event.START_TIME);
        eventStartTimeField.setTriggerAction(TriggerAction.ALL);

        eventEndTimeField = Forms.time(I18N.CONSTANTS.calendarEventEndHour(), false);
        eventEndTimeField.setName(Event.END_TIME);
        eventEndTimeField.setTriggerAction(TriggerAction.ALL);

        eventDateEndField = Forms.date(I18N.CONSTANTS.calendar_addEvent_dateEnd_label(), false);
        eventDateEndField.setName(Event.DATE + "888end");

        eventDescriptionField = Forms.textarea(I18N.CONSTANTS.calendarEventDescription(), false);
        eventDescriptionField.setName(Event.DESCRIPTION);
        eventDescriptionField.setId(Event.DESCRIPTION);
        eventDescriptionField.setMaxLength(255);

        saveButton = Forms.button(I18N.CONSTANTS.formWindowSubmitAction(), IconImageBundle.ICONS.save());
        cancelButton = Forms.button(I18N.CONSTANTS.cancel());

        form.add(eventSummaryField);
        form.add(eventDateStartField);
        form.add(eventDateEndField);
        form.add(createAllDayCheckbox());

        form.add(eventStartTimeField);
        form.add(eventEndTimeField);
        form.add(RepeatEventPeriodRG);
        form.add(panelYearly);
        form.add(panelMonthly);
        form.add(panelWeekly);
        form.add(panelDaily);
        form.add(eventDescriptionField);

        form.addButton(cancelButton);
        form.addButton(saveButton);

        initPopup(form);
    }

    private CheckBox createAllDayCheckbox() {

        allDayCheckbox = Forms.checkbox(I18N.CONSTANTS.calendar_addEvent_isAllDayCB_boxLabel(), I18N.CONSTANTS.calendar_addEvent_isAllDayCB_allDayName(), false);
        allDayCheckbox.setFieldLabel(I18N.CONSTANTS.calendar_addEvent_allDayCbGr_label());
        allDayCheckbox.validate(false);
        allDayCheckbox.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                eventStartTimeField.setVisible(!allDayCheckbox.getValue());
                eventEndTimeField.setVisible(!allDayCheckbox.getValue());
            }
        });

        return allDayCheckbox;
    }

    private void createRepeatEventPeriodRadioGroup() {
        createOnceRb();
        createDailyRb();
        createWeeklyRb();
        createMonthlyRb();
        createYearlyRb();

        RepeatEventPeriodRG = Forms.radioGroup(I18N.CONSTANTS.calendar_addEvent_repeatPeriodRG_label(),
                I18N.CONSTANTS.calendar_addEvent_repeatPeriodRG_name(),
                Style.Orientation.HORIZONTAL,
                onceRepeatRB,
                dailyRepeatRB,
                weeklyRepeatRB,
                monthlyRepeatRB,
                yearlyRepeatRB);
        RepeatEventPeriodRG.setSelectionRequired(Boolean.TRUE);
        RepeatEventPeriodRG.enable();
    }

    private void createYearlyRb() {
        yearlyRepeatRB = Forms.radio(I18N.CONSTANTS.calendar_addEvent_repeatYearlyRB_label(), I18N.CONSTANTS.calendar_addEvent_repeatYearlyRB_label(), Boolean.FALSE);
        yearlyRepeatRB.setToolTip(I18N.CONSTANTS.calendar_addEvent_repeatYearlyRB_toolTip());
        yearlyRepeatRB.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                //add default description
                //((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(I18N.CONSTANTS.calendar_addEvent_description_textArea_yearly());
                getPanelYearly().setVisible(getYearlyRepeatRB().getValue());
                getPanelWeekly().setVisible(!yearlyRepeatRB.getValue());
                getPanelMonthly().setVisible(!yearlyRepeatRB.getValue());
                getPanelDaily().setVisible(!yearlyRepeatRB.getValue());

                getYearlySameDateRB().show();
                getYearlySameDayOfWeekRB().show();
                getYearlySameDateRB().setValue(true);
                eventDateStartField.setAllowBlank(false);
            }
        });
    }

    private void createMonthlyRb() {
        monthlyRepeatRB = Forms.radio(I18N.CONSTANTS.calendar_addEvent_repeatMonthlyRB_label(), I18N.CONSTANTS.calendar_addEvent_repeatMonthlyRB_label(), Boolean.FALSE);
        monthlyRepeatRB.setToolTip(I18N.CONSTANTS.calendar_addEvent_repeatMonthlyRB_toolTip());
        monthlyRepeatRB.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                //add default description
                //((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(I18N.CONSTANTS.calendar_addEvent_description_textArea_monthly());
                getPanelWeekly().setVisible(!monthlyRepeatRB.getValue());
                getPanelYearly().setVisible(!monthlyRepeatRB.getValue());
                getPanelDaily().setVisible(!monthlyRepeatRB.getValue());
                getPanelMonthly().setVisible(getMonthlyRepeatRB().getValue());
                eventDateStartField.setAllowBlank(false);
                getRadioMonthlySameDate().show();
                getRadioMonthlySameDayOfWeek().show();
                getRadioMonthlySameDayOfWeek().setValue(true);
            }
        });
    }

    private void createWeeklyRb() {
        weeklyRepeatRB = Forms.radio(I18N.CONSTANTS.calendar_addEvent_repeatWeeklyRB_label(), I18N.CONSTANTS.calendar_addEvent_repeatWeeklyRB_label(), Boolean.FALSE);
        weeklyRepeatRB.setToolTip(I18N.CONSTANTS.calendar_addEvent_repeatWeeklyRB_toolTip());
        weeklyRepeatRB.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                //add default description
                //((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(I18N.CONSTANTS.calendar_addEvent_description_textArea_weekly());
                getPanelYearly().setVisible(!weeklyRepeatRB.getValue());
                getPanelMonthly().setVisible(!weeklyRepeatRB.getValue());
                getPanelDaily().setVisible(!weeklyRepeatRB.getValue());
                getPanelWeekly().setVisible(getWeeklyRepeatRB().getValue());
                //   Window.alert("Test");
                eventDateStartField.setAllowBlank(false);
            }
        });
    }

    /**
     *
     */
    private void createDailyRb() {
        dailyRepeatRB = Forms.radio(I18N.CONSTANTS.calendar_addEvent_repeatDailyRB_label(), I18N.CONSTANTS.calendar_addEvent_repeatDailyRB_label(), Boolean.FALSE);
        dailyRepeatRB.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                //add default description
                //((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(I18N.CONSTANTS.calendar_addEvent_description_textArea_daily());
                getPanelYearly().setVisible(!dailyRepeatRB.getValue());
                getPanelMonthly().setVisible(!dailyRepeatRB.getValue());
                getPanelWeekly().setVisible(!dailyRepeatRB.getValue());
                getPanelDaily().setVisible(getDailyRepeatRB().getValue());
            }
        });
    }

    private void createOnceRb() {
        onceRepeatRB = Forms.radio(I18N.CONSTANTS.calendar_addEvent_repeatOnceRB_label(), I18N.CONSTANTS.calendar_addEvent_repeatOnceRB_name(), Boolean.FALSE);
        onceRepeatRB.setOriginalValue(Boolean.TRUE);
        onceRepeatRB.setToolTip(I18N.CONSTANTS.calendar_addEvent_repeatOnceRB_toolTip());

        onceRepeatRB.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {

                getPanelYearly().setVisible(!onceRepeatRB.getValue());
                getPanelMonthly().setVisible(!onceRepeatRB.getValue());
                getPanelWeekly().setVisible(!onceRepeatRB.getValue());
                getPanelDaily().setVisible(!onceRepeatRB.getValue());
                //add default description
                //((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(I18N.CONSTANTS.calendar_addEvent_description_textArea_once());
            }
        });
    }

    private void createDailyPanel() {
        panelDaily = new FieldSet();
        panelDaily.setExpanded(true);
        panelDaily.setBorders(true);
        panelDaily.setHeadingHtml("Daily repetition details");
        panelDaily.setAutoHeight(true);
        panelDaily.setVisible(false);

        radioDailyNumberOfRepetitions = Forms.radio("Number of repetitions");
        radioDailyNumberOfRepetitions.setName("Number of repetitonsYearly");
        radioDailyNumberOfRepetitions.setValue(Boolean.FALSE);
        radioDailyNumberOfRepetitions.setOriginalValue(Boolean.FALSE);
        radioDailyNumberOfRepetitions.setToolTip("Set a number of repetitons (Yearly)");

        dailyNumberOfRepetitions = Forms.textarea("", false);
        dailyNumberOfRepetitions.setName("numberOfRepetitionsyearly");
        dailyNumberOfRepetitions.setId("numberOfRepetitionsyearly");
        dailyNumberOfRepetitions.setMaxLength(255);
        dailyNumberOfRepetitions.setHeight(19);

        radioDailyRepetitionEndDate = Forms.radio("Repetition end date");
        radioDailyRepetitionEndDate.setBoxLabel("Repetition end date");
        radioDailyRepetitionEndDate.setName("Repetition end dateyearly");
        radioDailyRepetitionEndDate.setValue(Boolean.FALSE);
        radioDailyRepetitionEndDate.setToolTip("Set a repetition end date");

        dailyRepetitionEndDate = Forms.date("", true);
        dailyRepetitionEndDate.setName("RepetitionEndDateFieldyearly");
        dailyNumberOfRepetitions.disable();
        dailyRepetitionEndDate.disable();

        radioDailyNumberOfRepetitions.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                dailyNumberOfRepetitions.enable();
                dailyRepetitionEndDate.disable();
            }
        });

        radioDailyRepetitionEndDate.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {

                dailyRepetitionEndDate.enable();
                dailyNumberOfRepetitions.disable();
            }
        });

        RepeatDailyMultiEventPeriodRG = Forms.radioGroup("Multi-event periodRGyearly",
                "Multi-event periodRGyearly",
                Style.Orientation.VERTICAL,
                radioDailyNumberOfRepetitions,
                radioDailyRepetitionEndDate);
        RepeatDailyMultiEventPeriodRG.setSelectionRequired(Boolean.TRUE);
        RepeatDailyMultiEventPeriodRG.enable();

        //panelMonthly.add(RepeatMultiEventPeriodRG);
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(dailyNumberOfRepetitions);
        verticalPanel.add(dailyRepetitionEndDate);
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(RepeatDailyMultiEventPeriodRG);
        horizontalPanel.add(verticalPanel);

        panelDaily.add(horizontalPanel);
    }

    private void createWeeklyPanel() {
        panelWeekly = new FieldSet();
        panelWeekly.setExpanded(true);
        panelWeekly.setBorders(true);
        panelWeekly.setHeadingHtml("Weekly repetition details");
        panelWeekly.setAutoHeight(true);
        panelWeekly.setVisible(false);

        radioWeeklyNumberOfRepetitions = Forms.radio("Number of repetitions");
        radioWeeklyNumberOfRepetitions.setName("Number of repetitonsYearly");
        radioWeeklyNumberOfRepetitions.setValue(Boolean.FALSE);
        radioWeeklyNumberOfRepetitions.setOriginalValue(Boolean.FALSE);
        radioWeeklyNumberOfRepetitions.setToolTip("Set a number of repetitons");

        weeklyNumberOfRepetitions = Forms.textarea("", false);
        weeklyNumberOfRepetitions.setName("numberOfRepetitionsyearly");
        weeklyNumberOfRepetitions.setId("numberOfRepetitionsyearly");
        weeklyNumberOfRepetitions.setMaxLength(255);
        weeklyNumberOfRepetitions.setHeight(19);

        radioWeeklyRepetitionEndDate = Forms.radio("Repetition end date");
        radioWeeklyRepetitionEndDate.setBoxLabel("Repetition end date");
        radioWeeklyRepetitionEndDate.setName("Repetition end dateyearly");
        radioWeeklyRepetitionEndDate.setValue(Boolean.FALSE);
        radioWeeklyRepetitionEndDate.setToolTip("Set a repetition end date");

        weeklyRepetitionEndDate = Forms.date("", true);
        weeklyRepetitionEndDate.setName("RepetitionEndDateFieldyearly");
        weeklyNumberOfRepetitions.disable();
        weeklyRepetitionEndDate.disable();

        radioWeeklyNumberOfRepetitions.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                weeklyNumberOfRepetitions.enable();
                weeklyRepetitionEndDate.disable();
            }
        });

        radioWeeklyRepetitionEndDate.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {

                weeklyRepetitionEndDate.enable();
                weeklyNumberOfRepetitions.disable();
            }
        });

        RepeatWeeklyMultiEventPeriodRG = Forms.radioGroup("Multi-event periodRGyearly",
                "Multi-event periodRGyearly",
                Style.Orientation.VERTICAL,
                radioWeeklyNumberOfRepetitions,
                radioWeeklyRepetitionEndDate);
        RepeatWeeklyMultiEventPeriodRG.setSelectionRequired(Boolean.TRUE);
        RepeatWeeklyMultiEventPeriodRG.enable();

        //panelMonthly.add(RepeatMultiEventPeriodRG);
        VerticalPanel verticalPanel = new VerticalPanel();
        verticalPanel.add(weeklyNumberOfRepetitions);
        verticalPanel.add(weeklyRepetitionEndDate);
        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(RepeatWeeklyMultiEventPeriodRG);
        horizontalPanel.add(verticalPanel);

        panelWeekly.add(horizontalPanel);
    }

    private void createMonthlyPanel() {
        panelMonthly = new FieldSet();
        panelMonthly.setExpanded(true);
        panelMonthly.setBorders(true);
        panelMonthly.setHeadingHtml(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_headingHtml_monthly());
        panelMonthly.setAutoHeight(true);
        panelMonthly.setVisible(false);

        radioMonthlySameDayOfWeek = Forms.radio(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_SameDayOfWeekRB_label());
        radioMonthlySameDayOfWeek.setName(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_SameDayOfWeekRB_monthly_name());
        radioMonthlySameDayOfWeek.setValue(Boolean.FALSE);
        radioMonthlySameDayOfWeek.setOriginalValue(Boolean.FALSE);
        radioMonthlySameDayOfWeek.setToolTip(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_SameDayOfWeekRB_monthly_toolTip());

        radioMonthlySameDate = Forms.radio(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_SameDateRB_label());
        radioMonthlySameDate.setBoxLabel(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_SameDateRB_label());
        radioMonthlySameDate.setName(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_SameDateRB_monthly_name());
        radioMonthlySameDate.setValue(Boolean.FALSE);
        radioMonthlySameDate.setToolTip(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_SameDateRB_monthly_toolTip());

        montlyVariantRG = Forms.radioGroup(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_radioGroup_monthly_settings());
        montlyVariantRG.setSelectionRequired(Boolean.FALSE);
        montlyVariantRG.setOrientation(Style.Orientation.HORIZONTAL);
        montlyVariantRG.add(radioMonthlySameDayOfWeek);
        montlyVariantRG.add(radioMonthlySameDate);
        panelMonthly.add(montlyVariantRG);

        radioNumberOfRepetitions = Forms.radio("Number of repetitions");
        radioNumberOfRepetitions.setName("Number of repetitons");
        radioNumberOfRepetitions.setValue(Boolean.FALSE);
        radioNumberOfRepetitions.setOriginalValue(Boolean.FALSE);
        radioNumberOfRepetitions.setToolTip("Set a number of repetitions");

        radioRepetitionEndDate = Forms.radio("Repetition end date");
        radioRepetitionEndDate.setBoxLabel("Repetition end date");
        radioRepetitionEndDate.setName("Repetition end date");
        radioRepetitionEndDate.setValue(Boolean.FALSE);
        radioRepetitionEndDate.setToolTip("Set a repetition end date");

        RepeatMultiEventPeriodRG = Forms.radioGroup("Multi-event periodRG");
        RepeatMultiEventPeriodRG.setSelectionRequired(Boolean.FALSE);
        RepeatMultiEventPeriodRG.setOrientation(Style.Orientation.VERTICAL);
        RepeatMultiEventPeriodRG.add(radioNumberOfRepetitions);
        RepeatMultiEventPeriodRG.add(radioRepetitionEndDate);
        panelMonthly.add(RepeatMultiEventPeriodRG);

        numberOfRepetitions = Forms.textarea("", false);
        numberOfRepetitions.setName("numberOfRepetitions");
        numberOfRepetitions.setId("numberOfRepetitions");
        numberOfRepetitions.setMaxLength(255);
        numberOfRepetitions.setHeight(19);

        repetitionEndDate = Forms.date("", true);
        repetitionEndDate.setName("RepetitionEndDateField");
        repetitionEndDate.setData("", new Date());

        radioNumberOfRepetitions.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                numberOfRepetitions.enable();
                repetitionEndDate.disable();
            }
        });

        radioRepetitionEndDate.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {

                repetitionEndDate.enable();
                numberOfRepetitions.disable();
            }
        });

        /*  monthlyRepSettings = new FieldSet();
        monthlyRepSettings.setExpanded(true);
        monthlyRepSettings.setBorders(true);
        monthlyRepSettings.setHeadingHtml("End of monthly repetition event");
        monthlyRepSettings.setAutoHeight(true);
        monthlyRepSettings.setVisible(true);*/
        VerticalPanel verticalPanel = new VerticalPanel();
        VerticalPanel verticalPanel1 = new VerticalPanel();
        VerticalPanel verticalPanel2 = new VerticalPanel();
        VerticalPanel verticalPanel3 = new VerticalPanel();
        verticalPanel3.setHeight(5);
        verticalPanel1.add(numberOfRepetitions);
        verticalPanel2.add(repetitionEndDate);

        verticalPanel.add(verticalPanel1);
        verticalPanel.add(verticalPanel3);
        verticalPanel.add(verticalPanel2);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(RepeatMultiEventPeriodRG);
        horizontalPanel.add(verticalPanel);
        panelMonthly.add(new HTML("<hr />"));
        panelMonthly.add(horizontalPanel);
        // verticalPanel1.setHeight(verticalPanel.getHeight()/2);
        //verticalPanel2.setHeight(verticalPanel.getHeight()/2); 
    }

    private void createYearlyPanel() {
        panelYearly = new FieldSet();
        panelYearly.setExpanded(true);
        panelYearly.setBorders(true);
        panelYearly.setHeadingHtml(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_headingHtml_yearly());
        panelYearly.setAutoHeight(true);
        panelYearly.setVisible(false);

        yearlySameDayOfWeekRB = Forms.radio(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_SameDayOfWeekRB_label(), Boolean.FALSE);
        yearlySameDayOfWeekRB.setToolTip(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_SameDayOfWeekRB_yearly_toolTip());
        yearlySameDayOfWeekRB.setOriginalValue(Boolean.TRUE);
        yearlySameDayOfWeekRB.setName(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_SameDayOfWeekRB_yearly_name());
        yearlySameDayOfWeekRB.setValue(Boolean.TRUE);

        yearlySameDateRB = Forms.radio(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_SameDateRB_label(), Boolean.FALSE);
        yearlySameDateRB.setName(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_SameDateRB_yearly_name());
        yearlyVariantRG = Forms.radioGroup(I18N.CONSTANTS.calendar_addEvent_repeatsSettings_radioGroup_yearly_settings(),
                I18N.CONSTANTS.calendar_addEvent_repeatsSettings_radioGroup_yearly_settings_name(),
                Style.Orientation.HORIZONTAL,
                yearlySameDayOfWeekRB,
                yearlySameDateRB);
        yearlyVariantRG.setSelectionRequired(Boolean.FALSE);
        panelYearly.add(yearlyVariantRG);

        radioYearlyNumberOfRepetitions = Forms.radio("Number of repetitions");
        radioYearlyNumberOfRepetitions.setName("Number of repetitionsYearly");
        radioYearlyNumberOfRepetitions.setValue(Boolean.FALSE);
        radioYearlyNumberOfRepetitions.setOriginalValue(Boolean.FALSE);
        radioYearlyNumberOfRepetitions.setToolTip("Set a number of repetitons");

        yearlyNumberOfRepetitions = Forms.textarea("", false);
        yearlyNumberOfRepetitions.setName("numberOfRepetitionsyearly");
        yearlyNumberOfRepetitions.setId("numberOfRepetitionsyearly");
        yearlyNumberOfRepetitions.setMaxLength(255);
        yearlyNumberOfRepetitions.setHeight(19);

        radioYearlyRepetitionEndDate = Forms.radio("Repetition end date");
        radioYearlyRepetitionEndDate.setBoxLabel("Repetition end date");
        radioYearlyRepetitionEndDate.setName("Repetition end dateyearly");
        radioYearlyRepetitionEndDate.setValue(Boolean.FALSE);
        radioYearlyRepetitionEndDate.setToolTip("Set a repetition end date");

        yearlyRepetitionEndDate = Forms.date("", true);
        yearlyRepetitionEndDate.setName("RepetitionEndDateFieldyearly");
        yearlyNumberOfRepetitions.disable();
        yearlyRepetitionEndDate.disable();

        radioYearlyNumberOfRepetitions.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                yearlyNumberOfRepetitions.enable();
                yearlyRepetitionEndDate.disable();
            }
        });

        radioYearlyRepetitionEndDate.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {

                yearlyRepetitionEndDate.enable();
                yearlyNumberOfRepetitions.disable();
            }
        });

        RepeatYearlyMultiEventPeriodRG = Forms.radioGroup("Multi-event periodRGyearly",
                "Multi-event periodRGyearly",
                Style.Orientation.VERTICAL,
                radioYearlyNumberOfRepetitions,
                radioYearlyRepetitionEndDate);
        RepeatYearlyMultiEventPeriodRG.setSelectionRequired(Boolean.TRUE);
        RepeatYearlyMultiEventPeriodRG.enable();

        //panelMonthly.add(RepeatMultiEventPeriodRG);
        VerticalPanel verticalPanel = new VerticalPanel();

        VerticalPanel verticalPanel1 = new VerticalPanel();
        VerticalPanel verticalPanel2 = new VerticalPanel();
        verticalPanel1.setHeight(23);
        verticalPanel1.setWidth(150);
        verticalPanel1.add(yearlyNumberOfRepetitions);
        verticalPanel2.setHeight(23);
        verticalPanel2.setWidth(150);
        verticalPanel2.add(yearlyRepetitionEndDate);
        VerticalPanel verticalPanel3 = new VerticalPanel();
        verticalPanel3.setHeight(5);

        verticalPanel.add(verticalPanel1);
        verticalPanel.add(verticalPanel3);
        verticalPanel.add(verticalPanel2);

        HorizontalPanel horizontalPanel = new HorizontalPanel();
        horizontalPanel.add(RepeatYearlyMultiEventPeriodRG);
        horizontalPanel.add(verticalPanel);

        panelYearly.add(new HTML("<hr />"));
        panelYearly.add(horizontalPanel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FormPanel getForm() {
        return form;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextField<String> getEventSummaryField() {
        return eventSummaryField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateField getEventDateStartField() {
        return eventDateStartField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DateField getEventDateEndField() {
        return eventDateEndField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimeField getEventStartTimeField() {
        return eventStartTimeField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TimeField getEventEndTimeField() {
        return eventEndTimeField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextArea getEventDescriptionField() {
        return eventDescriptionField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Button getSaveButton() {
        return saveButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Button getCancelButton() {
        return cancelButton;
    }

    @Override
    public CheckBox getAllDayCheckbox() {
        return allDayCheckbox;
    }

    @Override
    public FieldSet getPanelYearly() {
        return panelYearly;
    }

    @Override
    public FieldSet getMonthlyRepSettings() {
        return monthlyRepSettings;
    }

    @Override
    public FieldSet getYearlyRepSettings() {
        return yearlyRepSettings;
    }

    @Override
    public FieldSet getPanelMonthly() {
        return panelMonthly;
    }

    @Override
    public FieldSet getPanelWeekly() {
        return panelWeekly;
    }

    @Override
    public FieldSet getPanelDaily() {
        return panelDaily;
    }

    /**
     *
     * @return
     */
    @Override
    public Radio getOnceRepeatRB() {
        return onceRepeatRB;
    }

    @Override
    public Radio getDailyRepeatRB() {
        return dailyRepeatRB;
    }

    @Override
    public Radio getWeeklyRepeatRB() {
        return weeklyRepeatRB;
    }

    @Override
    public Radio getMonthlyRepeatRB() {
        return monthlyRepeatRB;
    }

    @Override
    public Radio getYearlyRepeatRB() {
        return yearlyRepeatRB;
    }

    @Override
    public RadioGroup getYearlyVariantRG() {
        return yearlyVariantRG;
    }

    @Override
    public RadioGroup getRepeatMultiEventPeriodRG() {
        return RepeatMultiEventPeriodRG;
    }

    @Override
    public RadioGroup getMontlyVariantRG() {
        return montlyVariantRG;
    }

    @Override
    public Radio getYearlySameDayOfWeekRB() {
        return yearlySameDayOfWeekRB;
    }

    @Override
    public Radio getYearlySameDateRB() {
        return yearlySameDateRB;
    }

    @Override
    public RadioGroup getRepeatEventPeriodRG() {
        return RepeatEventPeriodRG;
    }

    @Override
    public Radio getRadioMonthlySameDate() {
        return radioMonthlySameDate;
    }

    @Override
    public Radio getRadioMonthlySameDayOfWeek() {
        return radioMonthlySameDayOfWeek;
    }

    @Override
    public Radio getRadioNumberOfRepetitions() {
        return radioNumberOfRepetitions;
    }

    @Override
    public Radio getRadioRepetitionEndDate() {
        return radioRepetitionEndDate;
    }

    @Override
    public Radio getYearlyRadioNumberOfRepetitions() {
        return radioYearlyNumberOfRepetitions;
    }

    @Override
    public Radio getYearlyRadioRepetitionEndDate() {
        return radioYearlyRepetitionEndDate;
    }

    @Override
    public Radio getDailyRadioNumberOfRepetitions() {
        return radioDailyNumberOfRepetitions;
    }

    @Override
    public Radio getDailyRadioRepetitionEndDate() {
        return radioDailyRepetitionEndDate;
    }

    @Override
    public Radio getWeeklyRadioNumberOfRepetitions() {
        return radioWeeklyNumberOfRepetitions;
    }

    @Override
    public Radio getWeeklyRadioRepetitionEndDate() {
        return radioWeeklyRepetitionEndDate;
    }

    @Override
    public TextArea getNumberOfRepetitions() {
        return numberOfRepetitions;
    }

    @Override
    public DateField getRepetitionEndDate() {
        return repetitionEndDate;
    }

    @Override
    public TextArea getWeeklyNumberOfRepetitions() {
        return weeklyNumberOfRepetitions;
    }

    @Override
    public DateField getWeeklyRepetitionEndDate() {
        return weeklyRepetitionEndDate;
    }

    @Override
    public TextArea getDailyNumberOfRepetitions() {
        return dailyNumberOfRepetitions;
    }

    @Override
    public DateField getDailyRepetitionEndDate() {
        return dailyRepetitionEndDate;
    }

    @Override
    public TextArea getYearlyNumberOfRepetitions() {
        return yearlyNumberOfRepetitions;
    }

    @Override
    public DateField getYearlyRepetitionEndDate() {
        return yearlyRepetitionEndDate;
    }
}
