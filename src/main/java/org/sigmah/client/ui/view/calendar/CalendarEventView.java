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
import com.extjs.gxt.ui.client.data.ChangeListener;
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

import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;
import com.google.inject.Singleton;

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
    
    private Radio yearlySameDayOfWeekRB;
    private Radio yearlySameDateRB;

    private Radio onceRepeatRB;
    private Radio dailyRepeatRB;
    private Radio weeklyRepeatRB;
    private Radio monthlyRepeatRB;
    private Radio yearlyRepeatRB;
    private Radio radioMonthlySameDate;
    private Radio radioMonthlySameDayOfWeek;
    
    private ListBox listBoxWeekly;
    private ListBox listBoxMonthly1;
    private ListBox listBoxMonthly2;
    private ListBox listBoxYearly1;
    private ListBox listBoxYearly2;   
    // private CheckBoxGroup allDayCheckboxGr;

    private RadioGroup RepeatEventPeriodRG;

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
        createRepeatEventPeriodRadioGroup();
        
        listBoxWeekly.setName("listBoxWeekly");
        listBoxMonthly1.setName("listBoxMonthly1");
        listBoxMonthly2.setName("listBoxMonthly2");
        listBoxYearly1.setName("listBoxYearly1");
        listBoxYearly2.setName("listBoxYearly2");
        
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
                ((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(I18N.CONSTANTS.calendar_addEvent_description_textArea_yearly());
                getPanelYearly().setVisible(getYearlyRepeatRB().getValue());
                getPanelWeekly().setVisible(!yearlyRepeatRB.getValue());
                getPanelMonthly().setVisible(!yearlyRepeatRB.getValue());

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
                ((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(I18N.CONSTANTS.calendar_addEvent_description_textArea_monthly());
                getPanelWeekly().setVisible(!monthlyRepeatRB.getValue());
                getPanelYearly().setVisible(!monthlyRepeatRB.getValue());
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
                ((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(I18N.CONSTANTS.calendar_addEvent_description_textArea_weekly());
                getPanelYearly().setVisible(!weeklyRepeatRB.getValue());
                getPanelMonthly().setVisible(!weeklyRepeatRB.getValue());
                getPanelWeekly().setVisible(getWeeklyRepeatRB().getValue());
                Window.alert("Test");
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
                ((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(I18N.CONSTANTS.calendar_addEvent_description_textArea_daily());
                getPanelYearly().setVisible(!dailyRepeatRB.getValue());
                getPanelMonthly().setVisible(!dailyRepeatRB.getValue());
                getPanelWeekly().setVisible(!dailyRepeatRB.getValue());
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
                ((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(I18N.CONSTANTS.calendar_addEvent_description_textArea_once());
            }
        });
    }
   
    
    private void createWeeklyPanel() {
        panelWeekly = new FieldSet();
        panelWeekly.setExpanded(true);
        panelWeekly.setBorders(true);
        panelWeekly.setHeadingHtml("Weekly repeats settings");
        panelWeekly.setAutoHeight(true);
        panelWeekly.setVisible(false);

        listBoxWeekly = new ListBox();
        for(int i=1;i<=7;i++) listBoxWeekly.addItem(""+i);
                               
        panelWeekly.add(listBoxWeekly);
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
        
        listBoxMonthly1 = new ListBox();
        listBoxMonthly1.addItem("day(s)");
        listBoxMonthly1.addItem("week(s)");
        panelMonthly.add(listBoxMonthly1);
        

        
        listBoxMonthly2 = new ListBox();
        for(int i=1;i<=30;i++) listBoxMonthly2.addItem(""+i);                       
        panelMonthly.add(listBoxMonthly2);


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
        
        listBoxYearly1 = new ListBox();
        listBoxYearly1.addItem("day(s)");
        listBoxYearly1.addItem("week(s)");
        listBoxYearly1.addItem("month(s)");
        panelYearly.add(listBoxYearly1);
        

        
        listBoxYearly2 = new ListBox();
        for(int i=1;i<=30;i++) listBoxYearly2.addItem(""+i);                       
        panelYearly.add(listBoxYearly2);
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
    public FieldSet getPanelMonthly() {
        return panelMonthly;
    }

    @Override
    public FieldSet getPanelWeekly() {
        return panelWeekly;
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
    public RadioGroup getMontlyVariantRG() {
        return montlyVariantRG;
    }

    
    
    @Override    
    public ListBox getListBoxWeekly() {
        return listBoxWeekly;
    }
    
        @Override    
    public ListBox getListBoxMonthly1() {
        return listBoxMonthly1;
    }
    
        @Override    
    public ListBox getListBoxMonthly2() {
        return listBoxMonthly2;
    }    
        @Override    
    public ListBox getListBoxYearly1() {
        return listBoxYearly1;
    }
        @Override    
    public ListBox getListBoxYearly2() {
        return listBoxYearly2;
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

}