package org.sigmah.client.ui.view.admin.users;

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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.users.UserEditPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.button.ClickableLabel;
import org.sigmah.client.ui.widget.form.ComboboxButtonField;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.form.ListComboBox;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.Language;
import org.sigmah.shared.dto.UserUnitDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.util.EntityConstants;
import org.sigmah.shared.util.Pair;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Singleton;

/**
 * Admin Users View
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class UserEditView extends AbstractPopupView<PopupWidget> implements UserEditPresenter.View {

	// CSS style names.
	private static final String STYLE_FLEXIBILITY_ACTION = "flexibility-action";

	private FormPanel formPanel;
	private TextField<String> nameField;
	private TextField<String> firstNameField;
	private TextField<String> pwdField;
	private TextField<String> checkPwdField;
	private LabelField changePwdLink;
	private TextField<String> emailField;
	private ComboBox<EnumModel<Language>> languageField;
	private Grid<UserUnitDTO> secondaryUserUnitsGrid;
	private List<ListStore<OrgUnitDTO>> orgUnitStores;
	private List<OrgUnitDTO> availableOrgUnits = Collections.emptyList();
	private List<ListStore<ProfileDTO>> profileStores;
	private List<ProfileDTO> availableProfiles = Collections.emptyList();
	private Button addUserUnitButton;
	private UserEditPresenter.UserUnitActionHandler userUnitActionHandler;

	private Button createButton;

	/**
	 * Popup initialization.
	 */
	public UserEditView() {
		super(new PopupWidget(true), 800);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		orgUnitStores = new ArrayList<ListStore<OrgUnitDTO>>();
		profileStores = new ArrayList<ListStore<ProfileDTO>>();

		// --
		// Name field.
		// --

		nameField = Forms.text(I18N.CONSTANTS.adminUsersName(), true);

		// --
		// First name field.
		// --

		firstNameField = Forms.text(I18N.CONSTANTS.adminUsersFirstName(), true);

		// --
		// Email field.
		// --

		emailField = Forms.text(I18N.CONSTANTS.adminUsersEmail(), true);
		emailField.setRegex(EntityConstants.EMAIL_REGULAR_EXPRESSION);
		emailField.getMessages().setRegexText(I18N.MESSAGES.invalidEmailAddress());

		// --
		// Change password field.
		// --

		changePwdLink = Forms.label(null);
		changePwdLink.setValue(I18N.CONSTANTS.editPassword());
		changePwdLink.setHideLabel(true);
		changePwdLink.addStyleName(STYLE_FLEXIBILITY_ACTION);

		changePwdLink.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent event) {
				// Display the password fields when the admin wants to change the user's password.
				final boolean visibilityState = !pwdField.isVisible();
				pwdField.setVisible(visibilityState);
				pwdField.setAllowBlank(!visibilityState);
				checkPwdField.setVisible(visibilityState);
				checkPwdField.setAllowBlank(!visibilityState);

				if (pwdField.isVisible()) {
					checkPwdField.clearInvalid();

				} else if (ClientUtils.isNotBlank(checkPwdField.getValue())) {
					if (!checkPwdField.getValue().equals(pwdField.getValue())) {
						checkPwdField.forceInvalid(I18N.MESSAGES.pwdMatchProblem());
					} else {
						checkPwdField.clearInvalid();
					}
				}
			}
		});

		// --
		// Password field.
		// --

		pwdField = Forms.text(I18N.CONSTANTS.password(), false);
		pwdField.hide();
		pwdField.setPassword(true);
		pwdField.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(final ComponentEvent event) {
				checkPwdField.setAllowBlank(ClientUtils.isBlank(pwdField.getValue()));
			}
		});

		// --
		// Check password field.
		// --

		checkPwdField = Forms.text(I18N.CONSTANTS.confirmPassword(), false);
		checkPwdField.hide();
		checkPwdField.setPassword(true);

		checkPwdField.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyUp(final ComponentEvent event) {

				if (ClientUtils.isNotBlank(checkPwdField.getValue())) {

					pwdField.setAllowBlank(false);

					if (!checkPwdField.getValue().equals(pwdField.getValue())) {
						checkPwdField.forceInvalid(I18N.MESSAGES.pwdMatchProblem());
					} else {
						checkPwdField.clearInvalid();
					}

				} else {
					pwdField.setAllowBlank(true);
				}
			}
		});

		// --
		// Language field.
		// --

		languageField = Forms.combobox(I18N.CONSTANTS.adminUsersLocale(), true, EnumModel.VALUE_FIELD, EnumModel.DISPLAY_FIELD);
		languageField.getStore().add(new EnumModel<Language>(Language.FR));
		languageField.getStore().add(new EnumModel<Language>(Language.EN));
		languageField.getStore().add(new EnumModel<Language>(Language.ES));

		// --
		// OrgUnits field.
		// --
		final ColumnConfig orgUnitColumnConfig = new ColumnConfig(UserUnitDTO.ORG_UNIT, I18N.CONSTANTS.orgunit(), 500);
		orgUnitColumnConfig.setSortable(false);
		orgUnitColumnConfig.setRenderer(new GridCellRenderer() {
			@Override
			public Object render(final ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				ComboBox<OrgUnitDTO> orgUnitComboBox = Forms.combobox(null, true, OrgUnitDTO.ID, OrgUnitDTO.FULL_NAME,
					I18N.CONSTANTS.adminUserCreationOrgUnitChoice(), new ListStore<OrgUnitDTO>());
				orgUnitComboBox.getStore().add(availableOrgUnits);
				OrgUnitDTO orgUnit = ((UserUnitDTO) model).getOrgUnit();
				if (orgUnit != null) {
					orgUnitComboBox.setValue(orgUnit);
				}
				orgUnitStores.add(orgUnitComboBox.getStore());
				orgUnitComboBox.addSelectionChangedListener(new SelectionChangedListener<OrgUnitDTO>() {
					@Override
					public void selectionChanged(SelectionChangedEvent<OrgUnitDTO> event) {
						((UserUnitDTO) model).setOrgUnit(event.getSelectedItem());
					}
				});
				return orgUnitComboBox;
			}
		});
		// --
		// Profiles adapter field.
		// --
		ColumnConfig profilesColumnConfig = new ColumnConfig(UserUnitDTO.PROFILES, I18N.CONSTANTS.adminProfiles(), 500);
		profilesColumnConfig.setSortable(false);
		profilesColumnConfig.setRenderer(new GridCellRenderer() {
			@Override
			public Object render(final ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				final ListComboBox<ProfileDTO> profileListComboBox = new ListComboBox<ProfileDTO>(ProfileDTO.ID, ProfileDTO.NAME);
				List<ProfileDTO> profiles = model.get(property);
				profileListComboBox.getListStore().add(profiles);

				profileListComboBox.getListStore().addStoreListener(new StoreListener<ProfileDTO>() {
					@Override
					public void storeAdd(StoreEvent<ProfileDTO> event) {
						// The data is not in event.getModel() but in event.getModels() (which contains only one element)...
						userUnitActionHandler.onAddProfile((UserUnitDTO) model, event.getModels().get(0));
					}

					@Override
					public void storeRemove(StoreEvent<ProfileDTO> event) {
						userUnitActionHandler.onRemoveProfile((UserUnitDTO) model, event.getModel());
					}
				});
				setAvailableProfiles(profileListComboBox.getAvailableValuesStore(), availableProfiles);
				profileStores.add(profileListComboBox.getAvailableValuesStore());
				profileListComboBox.initComponent();
				return profileListComboBox;
			}
		});

		ColumnConfig actionsColumnConfig = new ColumnConfig("actions", "", 150);
		actionsColumnConfig.setSortable(false);
		actionsColumnConfig.setFixed(true);
		actionsColumnConfig.setRenderer(new GridCellRenderer() {
			@Override
			public Object render(final ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				if (((UserUnitDTO) model).getMainUserUnit()) {
					return null;
				}

				Button removeUserUnitButton = new Button(I18N.CONSTANTS.removeItem());
				removeUserUnitButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent event) {
						userUnitActionHandler.onRemoveUserUnit((UserUnitDTO) model);
					}
				});

				return removeUserUnitButton;
			}
		});

		ColumnModel columnModel = new ColumnModel(Arrays.asList(orgUnitColumnConfig, profilesColumnConfig, actionsColumnConfig));
		secondaryUserUnitsGrid = new Grid<UserUnitDTO>(new ListStore<UserUnitDTO>(), columnModel);
		secondaryUserUnitsGrid.setAutoExpandColumn(UserUnitDTO.ORG_UNIT);
		secondaryUserUnitsGrid.getView().setForceFit(true);
		secondaryUserUnitsGrid.setAutoHeight(true);

		addUserUnitButton = new Button(I18N.CONSTANTS.adminAddUserUnitButtonLabel());
		// --
		// Create button.
		// --

		createButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());

		// --
		// Form initialization.
		// --

		formPanel = Forms.panel(130);

		formPanel.add(nameField);
		formPanel.add(firstNameField);
		formPanel.add(emailField);
		formPanel.add(changePwdLink);
		formPanel.add(pwdField);
		formPanel.add(checkPwdField);
		formPanel.add(languageField);
		formPanel.add(secondaryUserUnitsGrid);
		formPanel.addButton(addUserUnitButton);
		formPanel.addButton(createButton);

		initPopup(formPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearForm() {
		nameField.clear();
		firstNameField.clear();
		emailField.clear();
		pwdField.clear();
		checkPwdField.clear();
		languageField.clearSelections();

		secondaryUserUnitsGrid.getStore().removeAll();
		orgUnitStores = new ArrayList<ListStore<OrgUnitDTO>>();
		profileStores = new ArrayList<ListStore<ProfileDTO>>();

		pwdField.hide();
		changePwdLink.setVisible(false);
		checkPwdField.hide();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getNameField() {
		return nameField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getFirstNameField() {
		return firstNameField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getPwdField() {
		return pwdField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Object> getChangePwdLink() {
		return changePwdLink;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getEmailField() {
		return emailField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<EnumModel<Language>> getLanguageField() {
		return languageField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel getForm() {
		return formPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getCreateButton() {
		return createButton;
	}

	@Override
	public ListStore<UserUnitDTO> getUserUnitStore() {
		return secondaryUserUnitsGrid.getStore();
	}

	@Override
	public Button getAddUserUnitButton() {
		return addUserUnitButton;
	}

	@Override
	public void setAvailableOrgUnits(List<OrgUnitDTO> orgUnits) {
		availableOrgUnits = orgUnits;
		for (ListStore<OrgUnitDTO> orgUnitStore : orgUnitStores) {
			orgUnitStore.removeAll();
			orgUnitStore.add(orgUnits);
		}
	}

	@Override
	public void setAvailableProfiles(List<ProfileDTO> profiles) {
		availableProfiles = profiles;
		for (ListStore<ProfileDTO> profileStore : profileStores) {
			setAvailableProfiles(profileStore, profiles);
		}
	}

	private void setAvailableProfiles(ListStore<ProfileDTO> store, List<ProfileDTO> profiles) {
		store.removeAll();
		for (ProfileDTO profile : profiles) {
			if (store.findModel(ProfileDTO.ID, profile.getId()) != null) {
				continue;
			}

			store.add(profile);
		}
	}

	@Override
	public void attachProfileHandler(UserEditPresenter.UserUnitActionHandler userUnitActionHandler) {
		this.userUnitActionHandler = userUnitActionHandler;
	}
}
