package org.sigmah.client.ui.presenter.admin.models.contact;

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

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.Field;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.ClientFactory;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasForm;
import org.sigmah.client.ui.view.admin.models.contact.AddContactModelAdminView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;


public class AddContactModelAdminPresenter extends AbstractPagePresenter<AddContactModelAdminPresenter.View> implements HasForm {


	public interface View extends ViewPopupInterface {

		FormPanel getForm();

		Field<String> getNameField();

		Field<ContactModelType> getContactModelTypeField();

		Button getAddButton();

	}


	public AddContactModelAdminPresenter(final View view, final ClientFactory factory) {
		super(view, factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_ADD_CONTACT_MODEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		view.getAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onSaveForm();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		view.getForm().clearAll();

		setPageTitle(I18N.CONSTANTS.adminContactModelCreationBox());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel[] getForms() {
		return new FormPanel[] { view.getForm()
		};
	}

	/**
	 * Callback executed on <em>create</em> button action.
	 */
	public void onSaveForm() {

		if (!view.getForm().isValid()) {
			return;
		}

		final String name = view.getNameField().getValue();
		final ContactModelType type = view.getContactModelTypeField().getValue();

		final Map<String, Object> newContactModelProperties = new HashMap<String, Object>();
		newContactModelProperties.put(AdminUtil.PROP_CM_NAME, name);
		newContactModelProperties.put(AdminUtil.PROP_CM_TYPE, type);

		final ContactModelDTO model = new ContactModelDTO();
		model.setStatus(ProjectModelStatus.DRAFT);
		newContactModelProperties.put(AdminUtil.ADMIN_CONTACT_MODEL, model);

		dispatch.execute(new CreateEntity(ContactModelDTO.ENTITY_NAME, newContactModelProperties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminContactModelCreationBox(),
					I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminContactModelStandard() + " '" + name + "'"));
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				final ContactModelDTO contactModel = (ContactModelDTO) result.getEntity();
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.CONTACT_MODEL_ADD, contactModel));

				N10N.infoNotif(I18N.CONSTANTS.adminContactModelCreationBox(),
					I18N.MESSAGES.adminStandardCreationSuccess(I18N.CONSTANTS.adminContactModelStandard() + " '" + name + "'"));

				hideView();
			}
		});
	}
}
