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

import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.google.inject.Singleton;
import com.google.gwt.user.client.Window;

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

    private Radio yearlySameDayOfWeekRB;
    private Radio yearlySameDateRB;

    private Radio onceRepeatRB;
    private Radio dailyRepeatRB;
    private Radio weeklyRepeatRB;
    private Radio monthlyRepeatRB;
    private Radio yearlyRepeatRB;
    private Radio radioMonthlySameDate;
    private Radio radioMonthlySameDayOfWeek;
    // private CheckBoxGroup allDayCheckboxGr;

    private RadioGroup RepeatEventPeriodRG;
    //private boolean showAddEventView = true;

//    public boolean isShowAddEventView() {
//        return showAddEventView;
//    }

//    /**
//     * {@inheritDoc}
//     */
//    // @Override
//    public void setShowAddEventView(boolean showAddEventView) {
//        this.showAddEventView = showAddEventView;
//    }

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
        //setShowAddEventView(Forms.panel().getTitle().equalsIgnoreCase(I18N.CONSTANTS.calendarAddEvent()));  
//        if (form.getTitle() != null) {
//            //   Window.alert( form.getTitle()+ " !!");
//            setShowAddEventView(Forms.panel().getTitle().equalsIgnoreCase(I18N.CONSTANTS.calendarAddEvent()));
//        } else {
//            Window.alert("setShowAddEventView(false)");
//            setShowAddEventView(false);
//        }
        // Window.alert("111");
        //      if(isShowAddEventView()){
       // Window.alert(isShowAddEventView() + "isShowAddEventView 3");
        createYearlyPanel();
        createMonthlyPanel();
        createRepeatEventPeriodRadioGroup();
        //       }

//        CheckBoxGroup allDayCheckboxGr = 
//                createAllDayCheckboxGroup();
        eventSummaryField = Forms.text(I18N.CONSTANTS.calendarEventObject(), true);
        eventSummaryField.setName(Event.SUMMARY);

        eventDateStartField = Forms.date(I18N.CONSTANTS.calendarEventDate()+ " start", true);
        eventDateStartField.setName(Event.DATE);
        //eventDateStartField.setAllowBlank(true);

        eventStartTimeField = Forms.time(I18N.CONSTANTS.calendarEventStartHour(), false);
        eventStartTimeField.setName(Event.START_TIME);
        eventStartTimeField.setTriggerAction(TriggerAction.ALL);

        eventEndTimeField = Forms.time(I18N.CONSTANTS.calendarEventEndHour(), false);
        eventEndTimeField.setName(Event.END_TIME);
        eventEndTimeField.setTriggerAction(TriggerAction.ALL);

        eventDateEndField = Forms.date(I18N.CONSTANTS.calendarEventDate() + " end", false);
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
        //form.add(allDayCheckbox);
        form.add(createAllDayCheckbox());

        //form.add(allDayCheckboxGr);
        form.add(eventStartTimeField);
        form.add(eventEndTimeField);
//if(isShowAddEventView()){
        form.add(RepeatEventPeriodRG);
        form.add(panelYearly);
        form.add(panelMonthly);
//}
        form.add(eventDescriptionField);

        form.addButton(cancelButton);
        form.addButton(saveButton);

        initPopup(form);
    }

    private CheckBox createAllDayCheckbox() {
        // private CheckBoxGroup createAllDayCheckboxGroup() {
        allDayCheckbox = Forms.checkbox(I18N.CONSTANTS.calendar_addEvent_isAllDayCB_boxLabel(), "All day name", false);
        allDayCheckbox.setFieldLabel(I18N.CONSTANTS.calendar_addEvent_allDayCbGr_label());
        // allDayCheckbox.setBoxLabel(I18N.CONSTANTS.calendar_addEvent_isAllDayCB_boxLabel());
        allDayCheckbox.validate(false);
        allDayCheckbox.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
//                        eventStartTimeField.setVisible(((Boolean) event.getValue()).booleanValue());
//                        eventEndTimeField.setVisible(((Boolean) event.getValue()).booleanValue());
//checkbox.getValue()
                eventStartTimeField.setVisible(!allDayCheckbox.getValue());
                eventEndTimeField.setVisible(!allDayCheckbox.getValue());
                Window.alert(getAllDayCheckbox().getBoxLabel() + " is checked");
            }
        });
//        CheckBoxGroup allDayCheckboxGr = Forms.checkBoxGroup(I18N.CONSTANTS.calendar_addEvent_allDayCbGr_label(), allDayCheckbox);
////           allDayCheckbox.addHandler(new ValueChangeHandler<Boolean>() {
////          @Override
////          public void onValueChange(ValueChangeEvent<Boolean> event) {
////             eventStartTimeField.setVisible(event.getValue());
////             eventEndTimeField.setVisible(event.getValue());
////          }
////        }, ValueChangeEvent.getType()); 
//        return allDayCheckboxGr;
//    }
        return allDayCheckbox;
    }

    private void createRepeatEventPeriodRadioGroup() {
        //final Radio
        onceRepeatRB = Forms.radio("once", "onceRepeatRBname", Boolean.FALSE);
        onceRepeatRB.setOriginalValue(Boolean.TRUE);
        onceRepeatRB.setToolTip(I18N.CONSTANTS.calendar_addEvent_repeatOnceRB_toolTip());
        //onceRepeatRB = new Radio();
//		onceRepeatRB.setBoxLabel("once");
//		onceRepeatRB.setName("once");
//		onceRepeatRB.setValue(true);
//                onceRepeatRB.focus();
//if(isShowAddEventView()){
        onceRepeatRB.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {

                getPanelYearly().setVisible(!onceRepeatRB.getValue());
                getPanelMonthly().setVisible(!onceRepeatRB.getValue());
                eventStartTimeField.setVisible(!dailyRepeatRB.getValue());
                eventEndTimeField.setVisible(!dailyRepeatRB.getValue());
//                eventDateStartField.setAllowBlank(false);
//                eventDateStartField.setAllowBlank(true);
                ((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(" The Once event.");
                Window.alert(getOnceRepeatRB().getBoxLabel() + " is checked  Listener OnChange on ONCE radio");
            }
        });
//}

//final Radio
//dailyRepeatRB = new Radio();
        dailyRepeatRB = Forms.radio("daily", "daily", Boolean.FALSE);
//		dailyRepeatRB.setBoxLabel("daily");
//		dailyRepeatRB.setName("daily");
//		dailyRepeatRB.setValue(false);
//if(isShowAddEventView()){
        dailyRepeatRB.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                ((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(" The Daily event.");
//                eventStartTimeField.setVisible(!dailyRepeatRB.getValue());
//                eventEndTimeField.setVisible(!dailyRepeatRB.getValue());
//                eventDateStartField.setAllowBlank(false);
                getPanelYearly().setVisible(!dailyRepeatRB.getValue());
                getPanelMonthly().setVisible(!dailyRepeatRB.getValue());
                Window.alert(getDailyRepeatRB().getBoxLabel() + " is checked  Listener OnChange on DAILY radio");
            }
        });
//}
//final Radio
//       weeklyRepeatRB = new Radio();
        weeklyRepeatRB = Forms.radio("weekly", "weekly", Boolean.FALSE);
        weeklyRepeatRB.setToolTip(I18N.CONSTANTS.calendar_addEvent_repeatWeeklyRB_toolTip());
//		weeklyRepeatRB.setBoxLabel("weekly");
//		weeklyRepeatRB.setName("weekly");
//		weeklyRepeatRB.setValue(false);
//if(isShowAddEventView()){
        weeklyRepeatRB.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                //eventStartTimeField.setVisible(!yearlyRepeatRB.getValue());
                // eventEndTimeField.setVisible(!yearlyRepeatRB.getValue());
                ((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(" The Weekly event.");
                getPanelYearly().setVisible(!weeklyRepeatRB.getValue());
                getPanelMonthly().setVisible(!weeklyRepeatRB.getValue());

                eventDateStartField.setAllowBlank(false);
                Window.alert(getWeeklyRepeatRB().getBoxLabel() + " is checked Listener OnChange on weekly radio");
            }
        });
//}
//final Radio
// monthlyRepeatRB = new Radio();
        monthlyRepeatRB = Forms.radio("monthly", "monthly", Boolean.FALSE);
        monthlyRepeatRB.setToolTip(I18N.CONSTANTS.calendar_addEvent_repeatMonthlyRB_toolTip());
//		monthlyRepeatRB.setBoxLabel("monthly");
//		monthlyRepeatRB.setName("monthly");
//		monthlyRepeatRB.setValue(false);
//if(isShowAddEventView()){
        monthlyRepeatRB.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                //eventStartTimeField.setVisible(!yearlyRepeatRB.getValue());
                // eventEndTimeField.setVisible(!yearlyRepeatRB.getValue());
                ((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(" The Monthly event.");
                getPanelYearly().setVisible(!monthlyRepeatRB.getValue());
                getPanelMonthly().setVisible(getMonthlyRepeatRB().getValue());
                eventDateStartField.setAllowBlank(false);
                getRadioMonthlySameDate().show();
                getRadioMonthlySameDayOfWeek().show();
                getRadioMonthlySameDayOfWeek().setValue(true);
                Window.alert(getMonthlyRepeatRB().getBoxLabel() + " is checked Listener OnChange on monthly radio");
            }
        });
//}
//final Radio
// yearlyRepeatRB = Forms.radio("yearlyRepeatRB label", "yearlyRepeatRB name", Boolean.FALSE);
//yearlyRepeatRB = new Radio();
        yearlyRepeatRB = Forms.radio("yearly", "yearly", Boolean.FALSE);
        yearlyRepeatRB.setToolTip(I18N.CONSTANTS.calendar_addEvent_repeatYearlyRB_toolTip());
//		yearlyRepeatRB.setBoxLabel("yearly");
//		yearlyRepeatRB.setName("yearly");
//		yearlyRepeatRB.setValue(false);
//if(isShowAddEventView()){
        yearlyRepeatRB.addListener(Events.OnChange, new Listener<FieldEvent>() {
            @Override
            public void handleEvent(FieldEvent event) {
                //eventStartTimeField.setVisible(!yearlyRepeatRB.getValue());
                // eventEndTimeField.setVisible(!yearlyRepeatRB.getValue());
                ((TextArea) form.getItemByItemId(Event.DESCRIPTION)).setValue(" The Yearly event.");
                getPanelYearly().setVisible(getYearlyRepeatRB().getValue());
                getPanelMonthly().setVisible(!yearlyRepeatRB.getValue());
                
                getYearlySameDateRB().show();
                getYearlySameDayOfWeekRB().show();
                getYearlySameDateRB().setValue(true);
                eventDateStartField.setAllowBlank(false);
                Window.alert(getYearlyRepeatRB().getBoxLabel() + " is checked Listener OnChange on Yearly radio");
            }
        });
//}
//YEARLY END
//final RadioGroup
        RepeatEventPeriodRG = Forms.radioGroup(I18N.CONSTANTS.calendar_addEvent_repeatPeriodRG_label(),
                "Repeat event 999",
                Style.Orientation.HORIZONTAL,
                onceRepeatRB,
                dailyRepeatRB,
                weeklyRepeatRB,
                monthlyRepeatRB,
                yearlyRepeatRB);
        RepeatEventPeriodRG.setSelectionRequired(Boolean.TRUE);
        RepeatEventPeriodRG.enable();
    }

    private void createMonthlyPanel() {
        panelMonthly = new FieldSet();
        panelMonthly.setExpanded(true);
        //panelMonthly.setTitle("Montly panel title");
        panelMonthly.setBorders(true);
        panelMonthly.setHeadingHtml("Montly repeats settings");
        // panelMonthly.setCollapsible(true);
        panelMonthly.setAutoHeight(true);
        //panelMonthly.setToolTip("Set Monthly event repeats options.");
        panelMonthly.setVisible(false);

        //Radio radioMonthlySameDayOfWeek = new Radio();
        //Radio
        radioMonthlySameDayOfWeek = Forms.radio("Same day of week");
        //radioMonthlySameDayOfWeek.setBoxLabel("Same day of week");
        radioMonthlySameDayOfWeek.setName("radioMonthlySameDayOfWeek");
        radioMonthlySameDayOfWeek.setValue(Boolean.FALSE);
        radioMonthlySameDayOfWeek.setOriginalValue(Boolean.FALSE);

        //Radio radioMonthlySameDate = new Radio();
        //Radio
        radioMonthlySameDate = Forms.radio("Same date");
        radioMonthlySameDate.setBoxLabel("Same date");
        radioMonthlySameDate.setName("radioMonthlySameDate");
        radioMonthlySameDate.setValue(Boolean.FALSE);

        //montlyVariantRG= new RadioGroup();
        montlyVariantRG = Forms.radioGroup("Montly settings 999");
        //montlyVariantRG.setSelectionRequired(Boolean.TRUE);
        montlyVariantRG.setSelectionRequired(Boolean.FALSE);
        montlyVariantRG.setOrientation(Style.Orientation.HORIZONTAL);
        //montlyVariantRG.setFieldLabel("Montly settings 999");
        montlyVariantRG.add(radioMonthlySameDayOfWeek);
        montlyVariantRG.add(radioMonthlySameDate);

        panelMonthly.add(montlyVariantRG);
    }

    private void createYearlyPanel() {
        panelYearly = new FieldSet();
        panelYearly.setExpanded(true);
        //panelYearly.setTitle("Yearly panel title");
        panelYearly.setBorders(true);
        panelYearly.setHeadingHtml("Yearly repeats settings");
        // panelYearly.setCollapsible(true);
        panelYearly.setAutoHeight(true);
        // panelYearly.setToolTip("Set Yearly event repeats options");
        panelYearly.setVisible(false);

        yearlySameDayOfWeekRB = Forms.radio("Same day of week", Boolean.FALSE);
        yearlySameDayOfWeekRB.setToolTip("Repeat Calendar event at the same DAY of week annually");
        yearlySameDayOfWeekRB.setOriginalValue(Boolean.TRUE);
        //yearlySameDayOfWeekRB.setBoxLabel("Same day of week");
        yearlySameDayOfWeekRB.setName("radioYearlySameDayOfWeek");
        yearlySameDayOfWeekRB.setValue(Boolean.TRUE);

        //yearlySameDateRB = new Radio();
        yearlySameDateRB = Forms.radio("Same date", Boolean.FALSE);
        //yearlySameDateRB.setBoxLabel("Same date");
        yearlySameDateRB.setName("radioYearlySameDate");
        //yearlySameDateRB.setValue(false);

        yearlyVariantRG = Forms.radioGroup("Yearly settings 888", "Yearly settings name",
                Style.Orientation.HORIZONTAL,
                yearlySameDayOfWeekRB,
                yearlySameDateRB);
        yearlyVariantRG.setSelectionRequired(Boolean.FALSE);

        panelYearly.add(yearlyVariantRG);
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

    public Radio getRadioMonthlySameDate() {
        return radioMonthlySameDate;
    }

    public Radio getRadioMonthlySameDayOfWeek() {
        return radioMonthlySameDayOfWeek;
    }

}