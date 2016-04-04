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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.admin.users.UserEditView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetOrgUnits;
import org.sigmah.shared.command.GetProfiles;
import org.sigmah.shared.command.GetUserUnitsByUser;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.UserUnitsResult;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.UserUnitDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO.Mode;
import org.sigmah.shared.dto.profile.ProfileDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Admin user create/edit Presenter
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class UserEditPresenter extends AbstractPagePresenter<UserEditPresenter.View> {

	/**
	 * The view interface managed by this presenter.
	 */
	@ImplementedBy(UserEditView.class)
	public static interface View extends ViewInterface {

		/**
		 * Clears the view form.
		 */
		void clearForm();

		ComboBox<EnumModel<Language>> getLanguageField();

		Field<String> getEmailField();

		Field<Object> getChangePwdLink();

		Field<String> getPwdField();

		Field<String> getFirstNameField();

		Field<String> getNameField();

		FormPanel getForm();

		Button getCreateButton();

		ListStore<UserUnitDTO> getUserUnitStore();

		Button getAddUserUnitButton();

		void setAvailableOrgUnits(List<OrgUnitDTO> orgUnits);

		void setAvailableProfiles(List<ProfileDTO> profiles);

		void attachProfileHandler(UserUnitActionHandler userUnitActionHandler);
	}

	public interface UserUnitActionHandler {
		void onAddProfile(UserUnitDTO userUnitDTO, ProfileDTO profileDTO);

		void onRemoveProfile(UserUnitDTO userUnitDTO, ProfileDTO profileDTO);

		void onRemoveUserUnit(UserUnitDTO userUnitDTO);
	}

	/**
	 * The edited user.<br>
	 * Set to {@code null} in case of creation.
	 */
	private UserDTO user;

	/**
	 * Presenter's initialization.
	 * 
	 * @param view
	 *          The view.
	 * @param injector
	 *          The application injector.
	 */
	@Inject
	protected UserEditPresenter(View view, Injector injector) {
		super(view, injector);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_USER_EDIT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		// --
		// Create button event handler.
		// --

		view.getCreateButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onCreateUser();
			}
		});

		// --
		// Add profile button event handler.
		// --

		view.getAddUserUnitButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent event) {
				UserUnitDTO userUnit = new UserUnitDTO();
				// If this UserUnit is the first one, he is considered as the main UserUnit
				userUnit.setMainUserUnit(view.getUserUnitStore().getCount() == 0);
				userUnit.setProfiles(new ArrayList<ProfileDTO>());
				view.getUserUnitStore().add(userUnit);
			}
		});

		view.attachProfileHandler(new UserUnitActionHandler() {
			@Override
			public void onAddProfile(UserUnitDTO userUnit, ProfileDTO profileDTO) {
				userUnit.getProfiles().add(profileDTO);
			}

			@Override
			public void onRemoveProfile(UserUnitDTO userUnit, ProfileDTO profileDTO) {
				userUnit.getProfiles().remove(profileDTO);
			}

			@Override
			public void onRemoveUserUnit(UserUnitDTO userUnitDTO) {
				view.getUserUnitStore().remove(userUnitDTO);
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		view.clearForm();

		setPageTitle(I18N.CONSTANTS.newUser());

		user = request.getData(RequestParameter.DTO);

		// --
		// Loads org units.
		// --

		view.getUserUnitStore().removeAll();

		dispatch.execute(new GetOrgUnits(auth().getOrgUnitIds(), Mode.WITH_TREE), new CommandResultHandler<ListResult<OrgUnitDTO>>() {
			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminChoiceProblem());
			}

			@Override
			public void onCommandSuccess(final ListResult<OrgUnitDTO> result) {
				List<OrgUnitDTO> orgUnitDTOs = new ArrayList<OrgUnitDTO>();
				for (OrgUnitDTO orgUnitDTO : result.getData()) {
					orgUnitDTOs.addAll(crawlOrgUnits(orgUnitDTO));
				}
				view.setAvailableOrgUnits(orgUnitDTOs);
			}
		});

		// --
		// Loads profiles.
		// --

		dispatch.execute(new GetProfiles(ProfileDTO.Mode.BASE), new CommandResultHandler<ListResult<ProfileDTO>>() {

			@Override
			public void onCommandFailure(final Throwable arg0) {
				N10N.error(I18N.CONSTANTS.adminChoiceProblem());
			}

			@Override
			public void onCommandSuccess(final ListResult<ProfileDTO> result) {
				if (result != null) {
					view.setAvailableProfiles(result.getList());
				}
			}
		});

		if (user == null) {
			// Creation mode.
			return;
		}

		// Edition mode.
		view.getNameField().setValue(user.getName());
		view.getFirstNameField().setValue(user.getFirstName());
		view.getEmailField().setValue(user.getEmail());
		view.getChangePwdLink().setVisible(true);
		view.getLanguageField().setValue(new EnumModel<Language>(Language.fromString(user.getLocale())));

		if (user.getMainOrgUnit() != null && ClientUtils.isNotBlank(user.getMainOrgUnit().getFullName())) {
			final OrgUnitDTO orgUnitDTOLight = new OrgUnitDTO();
			orgUnitDTOLight.setId(user.getMainOrgUnit().getId());
			orgUnitDTOLight.setFullName(user.getMainOrgUnit().getFullName());
		}

		dispatch.execute(new GetUserUnitsByUser(user.getId(), Mode.WITH_TREE), new CommandResultHandler<UserUnitsResult>() {
			@Override
			protected void onCommandSuccess(UserUnitsResult result) {
				if (result.getMainUserUnit() != null) {
					view.getUserUnitStore().add(result.getMainUserUnit());
				}
				if (result.getSecondaryUserUnits() != null) {
					view.getUserUnitStore().add(result.getSecondaryUserUnits());
				}
			}
		});
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Method executed on user creation or edition.
	 */
	private void onCreateUser() {

		if (!view.getForm().isValid()) {
			return;
		}

		// --
		// Collects form fields values.
		// --

		final String name = view.getNameField().getValue();
		final String firstName = view.getFirstNameField().getValue();
		final String email = view.getEmailField().getValue().trim();

		// Gets the value only if the admin wants to change the password.
		final String password = view.getPwdField().isVisible() ? view.getPwdField().getValue() : null;

		final EnumModel<Language> languageValue = view.getLanguageField().getValue();

		// --
		// Validates specific form data.
		// --

		final MatchResult matcher = ClientUtils.EMAIL_CLIENT_REGEXP.exec(email);
		if (matcher == null) {
			N10N.warn(I18N.CONSTANTS.createFormIncomplete(), I18N.MESSAGES.invalidEmailAddress());
			return;
		}

		UserUnitDTO mainUserUnit = view.getUserUnitStore().findModel(UserUnitDTO.MAIN_USER_UNIT, true);
		if (mainUserUnit == null) {
			N10N.warn(I18N.CONSTANTS.createFormIncomplete(), I18N.MESSAGES.createUserFormIncompleteDetails());
			return;
		}

		List<UserUnitDTO> secondaryUserUnits = view.getUserUnitStore().findModels(UserUnitDTO.MAIN_USER_UNIT, false);
		UserUnitsResult userUnitsResult = new UserUnitsResult();
		userUnitsResult.setMainUserUnit(mainUserUnit);
		userUnitsResult.setSecondaryUserUnits(secondaryUserUnits);

		// --
		// Executes command process.
		// --

		final Map<String, Object> userProperties = new HashMap<String, Object>();

		userProperties.put(UserDTO.ID, user != null ? user.getId() : null);
		userProperties.put(UserDTO.NAME, name);
		userProperties.put(UserDTO.FIRST_NAME, firstName);
		userProperties.put(UserDTO.PASSWORD, password);
		userProperties.put(UserDTO.EMAIL, email);
		userProperties.put(UserDTO.LOCALE, languageValue.getEnum());
		userProperties.put(UserDTO.USER_UNITS, userUnitsResult);

		dispatch.execute(new CreateEntity(UserDTO.ENTITY_NAME, userProperties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.warn(I18N.CONSTANTS.adminUserCreationBox(), I18N.MESSAGES.adminUserCreationFailure(firstName + ' ' + name));
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				eventBus.fireEvent(new UpdateEvent(UpdateEvent.USER_UPDATE));

				if (user != null) {
					N10N.infoNotif(I18N.CONSTANTS.adminUserCreationBox(), I18N.MESSAGES.adminUserUpdateSuccess(name));
				} else {
					N10N.infoNotif(I18N.CONSTANTS.adminUserCreationBox(), I18N.MESSAGES.adminUserCreationSuccess(name));
				}

				hideView();
			}
		}, view.getCreateButton());
	}

	/**
	 * Fills recursively the units field with the children of the given org {@code unit}.
	 */
	private List<OrgUnitDTO> crawlOrgUnits(OrgUnitDTO unit) {
		List<OrgUnitDTO> orgUnits = new ArrayList<OrgUnitDTO>();
		orgUnits.add(unit);

		for (final OrgUnitDTO child : unit.getChildrenOrgUnits()) {
			orgUnits.addAll(crawlOrgUnits(child));
		}

		return orgUnits;
	}

}
