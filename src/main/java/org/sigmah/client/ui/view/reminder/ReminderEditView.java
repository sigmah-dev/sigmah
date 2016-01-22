package org.sigmah.client.ui.view.reminder;

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
import org.sigmah.client.ui.presenter.reminder.ReminderEditPresenter;
import org.sigmah.client.ui.presenter.reminder.ReminderType;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;

import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.Singleton;

/**
 * Reminder edit frame view used to create/edit a reminder or a monitored point.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ReminderEditView extends AbstractPopupView<PopupWidget> implements ReminderEditPresenter.View {

	// CSS style names.
	private static final String STYLE_HEADER_LABEL = "header-label";

	private FormPanel form;
	private Label headerLabel;
	private TextField<String> labelField;
	private DateField expectedDateField;
	private Button saveButton;
	private Button deleteButton;

	/**
	 * Builds the view.
	 */
	public ReminderEditView() {
		super(new PopupWidget(true), 400);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		form = Forms.panel();

		headerLabel = new Label(I18N.CONSTANTS.reminderUpdateDetails());
		headerLabel.setStyleName(STYLE_HEADER_LABEL);
		labelField = Forms.text(I18N.CONSTANTS.monitoredPointLabel(), true);
		expectedDateField = Forms.date(I18N.CONSTANTS.monitoredPointExpectedDate(), true);

		saveButton = Forms.button(I18N.CONSTANTS.formWindowSubmitAction(), IconImageBundle.ICONS.save());
		deleteButton = Forms.button(I18N.CONSTANTS.formWindowDeleteAction(), IconImageBundle.ICONS.remove());

		form.add(headerLabel);
		form.add(labelField);
		form.add(expectedDateField);

		form.addButton(deleteButton);
		form.addButton(saveButton);

		initPopup(form);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInitializationMode(final ReminderType reminderType, final boolean creation) {

		headerLabel.setHtml(ReminderType.getHeader(reminderType, creation));

		deleteButton.setVisible(!creation);
		deleteButton.setEnabled(!creation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadReminder(final ReminderDTO reminder) {
		labelField.setValue(reminder != null ? reminder.getLabel() : null);
		expectedDateField.setValue(reminder != null ? reminder.getExpectedDate() : null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadMonitoredPoint(final MonitoredPointDTO monitoredPoint) {
		labelField.setValue(monitoredPoint != null ? monitoredPoint.getLabel() : null);
		expectedDateField.setValue(monitoredPoint != null ? monitoredPoint.getExpectedDate() : null);
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
	public Button getDeleteButton() {
		return deleteButton;
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
	public TextField<String> getLabelField() {
		return labelField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateField getExpectedDateField() {
		return expectedDateField;
	}

}
