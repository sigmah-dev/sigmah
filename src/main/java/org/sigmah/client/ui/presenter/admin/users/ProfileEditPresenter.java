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
import java.util.Map.Entry;
import java.util.Set;

import org.sigmah.client.ClientFactory;
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
import org.sigmah.client.ui.view.admin.users.ProfileEditView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetPrivacyGroups;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Admin Profile Presenter
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class ProfileEditPresenter extends AbstractPagePresenter<ProfileEditPresenter.View> {

	/**
	 * The view interface managed by this presenter.
	 */
	public static interface View extends ViewInterface {

		/**
		 * Clears the view form.
		 */
		void clearForm();

		/**
		 * Adds the given {@code privacyGroup} to the selected PrivacyGroups/Permissions panel.<br>
		 * The {@code privacyGroup}'s {@code code} and {@code title} properties must be set.
		 * 
		 * @param privacyGroup
		 *          The privacyGroup.
		 * @param permission
		 *          The permission.
		 * @param deleteHandler
		 *          The delete handler.
		 */
		void addPrivacyGroup(PrivacyGroupDTO privacyGroup, PrivacyGroupPermissionEnum permission, ClickHandler deleteHandler);

		/**
		 * Sets the given {@code globalPermission} corresponding checkBox value with the new {@code value}.
		 * 
		 * @param globalPermission
		 *          The global permission.
		 * @param value
		 *          The new value.
		 */
		void setPermissionValue(GlobalPermissionEnum globalPermission, Boolean value);

		FormPanel getForm();

		Field<String> getNameField();

		/**
		 * Returns the <b>selected</b> (checked) global permissions.
		 * 
		 * @return The <b>selected</b> (checked) global permissions.
		 */
		Set<GlobalPermissionEnum> getSelectedGlobalPermissions();

		ComboBox<PrivacyGroupDTO> getPrivacyGroupsComboBox();

		ComboBox<EnumModel<PrivacyGroupPermissionEnum>> getPrivacyGroupsPermissionsComboBox();

		Button getPrivacyGroupsAddButton();

		Button getCreateButton();

	}

	/**
	 * The selected privacy groups ids with their corresponding permission.
	 */
	private Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> selectedPrivacyGroups;

	/**
	 * The {@link ProfileDTO} to update.<br>
	 * Set to {@code null} in case of creation of a new profile.
	 */
	private ProfileDTO profile;

	/**
	 * Presenter's initialization.
	 * 
	 * @param view
	 *          The view.
	 * @param injector
	 *          The application injector.
	 */
	public ProfileEditPresenter(View view, ClientFactory factory) {
		super(view, factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_PROFILE_EDIT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Selected privacy groups map initialization.
		// --

		selectedPrivacyGroups = new HashMap<PrivacyGroupDTO, PrivacyGroupPermissionEnum>();

		// --
		// Add {PrivacyGroup ; Permission} button handler.
		// --

		view.getPrivacyGroupsAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {

				final PrivacyGroupDTO privacyGroup = view.getPrivacyGroupsComboBox().getValue();
				final PrivacyGroupPermissionEnum permission = EnumModel.getEnum(view.getPrivacyGroupsPermissionsComboBox().getValue());

				onAddPrivacyGroup(privacyGroup, permission);

				view.getPrivacyGroupsComboBox().clear();
				view.getPrivacyGroupsPermissionsComboBox().clear();
			}
		});

		// --
		// Create button handler.
		// --

		view.getCreateButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {
				onCreateProfile();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(PageRequest request) {

		view.clearForm();
		selectedPrivacyGroups.clear();

		setPageTitle(I18N.CONSTANTS.adminProfileAdd());

		profile = request.getData(RequestParameter.DTO);

		// --
		// Loads privacy groups.
		// --

		loadPrivacyGroups();

		if (profile == null) {
			return;
		}

		// --
		// EDITION CASE : Loads the edited profile.
		// --

		// Initializes name field value.
		view.getNameField().setValue(profile.getName());

		// Initializes permissions checkBoxes.
		for (final GlobalPermissionEnum globalPermission : profile.getGlobalPermissions()) {
			view.setPermissionValue(globalPermission, Boolean.TRUE);
		}

		// Initializes the selected privacyGroups/permission.
		if (profile.getPrivacyGroups() != null) {
			for (final Entry<PrivacyGroupDTO, PrivacyGroupPermissionEnum> entry : profile.getPrivacyGroups().entrySet()) {
				if (entry == null) {
					continue;
				}
				onAddPrivacyGroup(entry.getKey(), entry.getValue());
			}
		}
	}

	// ----------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ----------------------------------------------------------------------------------------------------

	/**
	 * Loads the privacy groups (and permissions) and populates the corresponding form fields.
	 */
	private void loadPrivacyGroups() {

		// --
		// Privacy groups permissions.
		// --

		view.getPrivacyGroupsPermissionsComboBox().getStore().removeAll();

		for (final PrivacyGroupPermissionEnum permission : PrivacyGroupPermissionEnum.values()) {
			if (permission != PrivacyGroupPermissionEnum.NONE) {
				view.getPrivacyGroupsPermissionsComboBox().getStore().add(new EnumModel<PrivacyGroupPermissionEnum>(permission));
			}
		}

		// --
		// Privacy groups.
		// --

		view.getPrivacyGroupsComboBox().getStore().removeAll();

		dispatch.execute(new GetPrivacyGroups(), new CommandResultHandler<ListResult<PrivacyGroupDTO>>() {

			@Override
			public void onCommandFailure(final Throwable arg0) {
				N10N.error(I18N.CONSTANTS.adminChoiceProblem());
			}

			@Override
			public void onCommandSuccess(final ListResult<PrivacyGroupDTO> result) {
				view.getPrivacyGroupsComboBox().getStore().add(result.getList());
				view.getPrivacyGroupsComboBox().getStore().commitChanges();
			}
		});
	}

	/**
	 * Callback executed on privacy group add action.<br>
	 * Does nothing if one of the arguments is {@code null} or if the {@code privacyGroup} is already selected.
	 * 
	 * @param privacyGroup
	 *          The selected privacy group.
	 * @param permission
	 *          The selected permission.
	 */
	private void onAddPrivacyGroup(final PrivacyGroupDTO privacyGroup, final PrivacyGroupPermissionEnum permission) {

		if (privacyGroup == null || permission == null || selectedPrivacyGroups.containsKey(privacyGroup)) {
			return;
		}

		view.addPrivacyGroup(privacyGroup, permission, new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				selectedPrivacyGroups.remove(privacyGroup);
			}
		});

		selectedPrivacyGroups.put(privacyGroup, permission);
	}

	/**
	 * Callback executed on profile creation or update.
	 */
	private void onCreateProfile() {

		if (!view.getForm().isValid()) {
			return;
		}

		final String name = view.getNameField().getValue();

		// --
		// Creates ProfileDTO.
		// --

		final ProfileDTO profileToSave = new ProfileDTO();
		profileToSave.setId(profile != null ? profile.getId() : null);
		profileToSave.setName(name);
		profileToSave.setGlobalPermissions(view.getSelectedGlobalPermissions());
		profileToSave.setPrivacyGroups(selectedPrivacyGroups);

		// --
		// Executes the create command.
		// --

		final Map<String, Object> profileProperties = new HashMap<String, Object>();
		profileProperties.put(ProfileDTO.PROFILE, profileToSave);

		dispatch.execute(new CreateEntity(ProfileDTO.ENTITY_NAME, profileProperties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminProfileCreationBox(),
					I18N.MESSAGES.adminStandardCreationFailure(I18N.MESSAGES.adminStandardProfile() + " '" + name + "'"));
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				eventBus.fireEvent(new UpdateEvent(UpdateEvent.PROFILE_UPDATE));

				if (profile != null) {
					N10N.infoNotif(I18N.CONSTANTS.adminProfileCreationBox(), I18N.MESSAGES.adminStandardUpdateSuccess(I18N.MESSAGES.adminStandardProfile()));

					N10N.confirmation(I18N.CONSTANTS.adminRefreshUsersBox(), new ConfirmCallback() {

						@Override
						public void onAction() {
							eventBus.fireEvent(new UpdateEvent(UpdateEvent.USER_UPDATE));
						}
					});

				} else {
					N10N.infoNotif(I18N.CONSTANTS.adminProfileCreationBox(), I18N.MESSAGES.adminStandardCreationSuccess(I18N.MESSAGES.adminStandardProfile()));
				}

				hideView();
			}
		}, view.getCreateButton());
	}

}
