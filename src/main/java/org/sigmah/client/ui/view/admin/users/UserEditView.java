package org.sigmah.client.ui.view.admin.users;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.users.UserEditPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.button.ClickableLabel;
import org.sigmah.client.ui.widget.form.ComboboxButtonField;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.Language;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.util.EntityConstants;
import org.sigmah.shared.util.Pair;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
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
	private ComboBox<OrgUnitDTO> orgUnitsField;
	private ComboboxButtonField profilesField;
	private FlowPanel profilesSelectionPanel;

	private Button createButton;

	/**
	 * Popup initialization.
	 */
	public UserEditView() {
		super(new PopupWidget(true), 550);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

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

		orgUnitsField =
				Forms.combobox(I18N.CONSTANTS.adminUsersOrgUnit(), true, OrgUnitDTO.ID, OrgUnitDTO.FULL_NAME, I18N.CONSTANTS.adminUserCreationOrgUnitChoice(),
					new ListStore<OrgUnitDTO>());

		// --
		// Profiles adapter field.
		// --

		profilesField = new ComboboxButtonField(I18N.CONSTANTS.adminUsersProfiles(), new Pair<String, String>(ProfileDTO.ID, ProfileDTO.NAME));
		profilesField.getComboBox(0).setEmptyText(I18N.CONSTANTS.adminUserCreationProfileChoice());

		profilesSelectionPanel = new FlowPanel();

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
		formPanel.add(orgUnitsField);
		formPanel.add(profilesField);
		formPanel.add(Forms.adapter(null, profilesSelectionPanel));
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
		orgUnitsField.clearSelections();
		profilesField.getComboBox(0).clearSelections();

		pwdField.hide();
		changePwdLink.setVisible(false);
		checkPwdField.hide();

		profilesSelectionPanel.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addProfile(final ProfileDTO profile, final ClickHandler deleteHandler) {

		final ClickableLabel label = new ClickableLabel(profile.getName());

		label.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				if (deleteHandler != null) {
					deleteHandler.onClick(event);
					label.removeFromParent();
				}
			}
		});

		profilesSelectionPanel.add(label);
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
	public ComboBox<OrgUnitDTO> getOrgUnitsField() {
		return orgUnitsField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<ProfileDTO> getProfilesField() {
		return profilesField.getComboBox(0);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getAddProfileButton() {
		return profilesField.getButton();
	}

}
