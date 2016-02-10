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
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.TimeField;
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
	private DateField eventDateField;
	private TimeField eventStartTimeField;
	private TimeField eventEndTimeField;
	private TextArea eventDescriptionField;

	private Button saveButton;
	private Button cancelButton;

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

		eventSummaryField = Forms.text(I18N.CONSTANTS.calendarEventObject(), true);
		eventSummaryField.setName(Event.SUMMARY);

		eventDateField = Forms.date(I18N.CONSTANTS.calendarEventDate(), true);
		eventDateField.setName(Event.DATE);

		eventStartTimeField = Forms.time(I18N.CONSTANTS.calendarEventStartHour(), false);
		eventStartTimeField.setName(Event.START_TIME);
		eventStartTimeField.setTriggerAction(TriggerAction.ALL);

		eventEndTimeField = Forms.time(I18N.CONSTANTS.calendarEventEndHour(), false);
		eventEndTimeField.setName(Event.END_TIME);
		eventEndTimeField.setTriggerAction(TriggerAction.ALL);

		eventDescriptionField = Forms.textarea(I18N.CONSTANTS.calendarEventDescription(), false);
		eventDescriptionField.setName(Event.DESCRIPTION);

		saveButton = Forms.button(I18N.CONSTANTS.formWindowSubmitAction(), IconImageBundle.ICONS.save());
		cancelButton = Forms.button(I18N.CONSTANTS.cancel());

		form.add(eventSummaryField);
		form.add(eventDateField);
		form.add(eventStartTimeField);
		form.add(eventEndTimeField);
		form.add(eventDescriptionField);

		form.addButton(cancelButton);
		form.addButton(saveButton);

		initPopup(form);
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
	public DateField getEventDateField() {
		return eventDateField;
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

}
