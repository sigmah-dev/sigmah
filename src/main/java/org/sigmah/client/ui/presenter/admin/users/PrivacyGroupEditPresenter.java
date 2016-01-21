package org.sigmah.client.ui.presenter.admin.users;

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

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.admin.users.PrivacyGroupEditView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * PrivacyGroupEditPresenter.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class PrivacyGroupEditPresenter extends AbstractPagePresenter<PrivacyGroupEditPresenter.View> {

	/**
	 * View interface.
	 */
	@ImplementedBy(PrivacyGroupEditView.class)
	public static interface View extends ViewInterface {

		FormPanel getForm();

		Field<Number> getCodeField();

		Field<String> getNameField();

		Button getCreateButton();

	}

	/**
	 * The {@link PrivacyGroupDTO} to update.<br>
	 * Set to {@code null} in case of a new privacy group creation.
	 */
	private PrivacyGroupDTO privacyGroup;

	/**
	 * Presenter's initialization.
	 * 
	 * @param view
	 *          The view managed by the presenter.
	 * @param injector
	 *          The application injector.
	 */
	@Inject
	protected PrivacyGroupEditPresenter(View view, Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// Save button
		view.getCreateButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {
				onSavePrivacyGroup();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_PRIVACY_GROUP_EDIT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(PageRequest request) {

		view.getForm().clearAll();

		privacyGroup = request.getData(RequestParameter.DTO);

		if (privacyGroup != null) {
			// EDIT
			view.getCodeField().setValue(privacyGroup.getCode());
			view.getNameField().setValue(privacyGroup.getTitle());
		}

		view.getCodeField().setEnabled(privacyGroup == null);

		setPageTitle(I18N.CONSTANTS.adminPrivacyGroupAdd());
	}

	/**
	 * Callback executed on save button action.
	 */
	private void onSavePrivacyGroup() {

		if (!view.getForm().isValid()) {
			N10N.warn(I18N.CONSTANTS.createFormIncomplete(), I18N.MESSAGES.createFormIncompleteDetails(I18N.MESSAGES.adminStandardPrivacyGroup()));
			return;
		}

		if (privacyGroup != null) {
			editPrivacyGroup(privacyGroup);
		} else {
			createPrivacyGroup();
		}
	}

	/**
	 * Creates a new {@link PrivacyGroupDTO}.
	 */
	private void createPrivacyGroup() {

		final String name = view.getNameField().getValue();
		final Number code = view.getCodeField().getValue();

		final Map<String, Object> newPrivacyGroupProperties = new HashMap<String, Object>();
		newPrivacyGroupProperties.put(PrivacyGroupDTO.CODE, code);
		newPrivacyGroupProperties.put(PrivacyGroupDTO.TITLE, name);

		dispatch.execute(new CreateEntity(PrivacyGroupDTO.ENTITY_NAME, newPrivacyGroupProperties), new CommandResultHandler<CreateResult>() {

			@Override
			protected void onCommandFailure(Throwable caught) {

				N10N.warn(I18N.CONSTANTS.adminPrivacyGroupCreationBox(),
					I18N.MESSAGES.adminStandardCreationFailure(I18N.MESSAGES.adminStandardPrivacyGroup() + " '" + name + " '"));
			}

			@Override
			protected void onCommandSuccess(CreateResult result) {

				final PrivacyGroupDTO privacyGroup = (PrivacyGroupDTO) result.getEntity();

				if (privacyGroup.isUpdated()) {

					N10N.infoNotif(I18N.CONSTANTS.adminPrivacyGroupCreationBox(), I18N.MESSAGES.adminStandardUpdateSuccess(I18N.MESSAGES.adminStandardPrivacyGroup()));

					N10N.confirmation(I18N.CONSTANTS.adminRefreshProfilesBox(), new ConfirmCallback() {

						@Override
						public void onAction() {
							eventBus.fireEvent(new UpdateEvent(UpdateEvent.PROFILE_UPDATE));
						}
					});

				} else {
					N10N.infoNotif(I18N.CONSTANTS.adminPrivacyGroupCreationBox(), I18N.MESSAGES.adminStandardCreationSuccess(I18N.MESSAGES.adminStandardPrivacyGroup()));
				}

				eventBus.fireEvent(new UpdateEvent(UpdateEvent.PRIVACY_GROUP_UPDATE));

				hideView();
			}
		}, view.getCreateButton());
	}

	/**
	 * Updates the given {@code privacyGroup} with the form data.
	 * 
	 * @param privacyGroup
	 *          The privacy group to update.
	 */
	private void editPrivacyGroup(final PrivacyGroupDTO privacyGroup) {

		if (privacyGroup == null || privacyGroup.getId() == null) {
			return;
		}

		final String name = view.getNameField().getValue();
		final Number code = view.getCodeField().getValue();

		final Map<String, Object> newPrivacyGroupProperties = new HashMap<String, Object>();
		newPrivacyGroupProperties.put(PrivacyGroupDTO.CODE, code);
		newPrivacyGroupProperties.put(PrivacyGroupDTO.TITLE, name);

		dispatch.execute(new UpdateEntity(PrivacyGroupDTO.ENTITY_NAME, privacyGroup.getId(), newPrivacyGroupProperties), new CommandResultHandler<VoidResult>() {

			@Override
			protected void onCommandFailure(Throwable caught) {

				N10N.warn(I18N.CONSTANTS.adminPrivacyGroupCreationBox(),
					I18N.MESSAGES.adminStandardCreationFailure(I18N.MESSAGES.adminStandardPrivacyGroup() + " '" + name + " '"));
			}

			@Override
			protected void onCommandSuccess(VoidResult result) {

				N10N.infoNotif(I18N.CONSTANTS.adminPrivacyGroupCreationBox(), I18N.MESSAGES.adminStandardUpdateSuccess(I18N.MESSAGES.adminStandardPrivacyGroup()));

				N10N.confirmation(I18N.CONSTANTS.adminRefreshProfilesBox(), new ConfirmCallback() {

					@Override
					public void onAction() {
						eventBus.fireEvent(new UpdateEvent(UpdateEvent.PROFILE_UPDATE));
					}
				});

				eventBus.fireEvent(new UpdateEvent(UpdateEvent.PRIVACY_GROUP_UPDATE));

				hideView();
			}
		}, view.getCreateButton());
	}

}
