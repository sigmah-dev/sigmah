package org.sigmah.client.ui.view.admin.models.contact;

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

import com.google.inject.Singleton;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.contact.AddContactModelAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.ContactModelTypeField;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.referential.ContactModelType;

@Singleton
public class AddContactModelAdminView extends AbstractPopupView<PopupWidget> implements AddContactModelAdminPresenter.View {

	private FormPanel form;
	private TextField<String> nameField;
	private ContactModelTypeField typeField;
	private Button createButton;

	/**
	 * Popup's initialization.
	 */
	public AddContactModelAdminView() {
		super(new PopupWidget(true), 450);
	}

	@Override
	public void initialize() {

		form = Forms.panel(150);

		nameField = Forms.text(I18N.CONSTANTS.adminContactModelName(), true);

		typeField = new ContactModelTypeField(I18N.CONSTANTS.adminContactModelType(), true, Orientation.VERTICAL);

		createButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());

		form.add(nameField);
		form.add(typeField);
		form.addButton(createButton);

		initPopup(form);
	}

	
	@Override
	public FormPanel getForm() {
		return form;
	}

	@Override
	public Field<String> getNameField() {
		return nameField;
	}

	@Override
	public Field<ContactModelType> getContactModelTypeField() {
		return typeField;
	}

	@Override
	public Button getAddButton() {
		return createButton;
	}

}
