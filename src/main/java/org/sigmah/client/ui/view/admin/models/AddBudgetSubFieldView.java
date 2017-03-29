package org.sigmah.client.ui.view.admin.models;

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
import org.sigmah.client.ui.presenter.admin.models.AddBudgetSubFieldPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
public class AddBudgetSubFieldView extends AbstractPopupView<PopupWidget> implements AddBudgetSubFieldPresenter.View {

	private TextField<String> nameField;
	private Button saveButton;
	private FormPanel form;

	public AddBudgetSubFieldView() {
		super(new PopupWidget(true), 400);
	}

	@Override
	public void initialize() {

		form = Forms.panel();

		nameField = new TextField<String>();
		nameField.setFieldLabel(I18N.CONSTANTS.adminBudgetSubFieldName());
		nameField.setAllowBlank(false);

		saveButton = new Button(I18N.CONSTANTS.save(), org.sigmah.client.ui.res.icon.IconImageBundle.ICONS.save());

		form.add(nameField);
		form.add(saveButton);

		form.setAutoHeight(true);
		form.setAutoWidth(true);

		initPopup(form);

	}

	@Override
	public FormPanel getForm() {
		return form;
	}

	@Override
	public TextField<String> getNameField() {
		return nameField;
	}

	@Override
	public Button getSaveButton() {
		return saveButton;
	}

}
