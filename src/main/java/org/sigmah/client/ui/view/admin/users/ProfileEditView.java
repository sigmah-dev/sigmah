package org.sigmah.client.ui.view.admin.users;

import com.extjs.gxt.ui.client.Style;
import java.util.Set;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.users.ProfileEditPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.button.ClickableLabel;
import org.sigmah.client.ui.widget.form.ComboboxButtonField;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;
import org.sigmah.shared.util.Pair;

import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Singleton;

/**
 * Profile create/edit popup view implementation.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class ProfileEditView extends AbstractPopupView<PopupWidget> implements ProfileEditPresenter.View {

	private FormPanel formPanel;
	private Field<String> nameField;
	private PermissionTree permissionTree;
	private ComboboxButtonField privacyGroupsField;
	private FlowPanel privacyGroupsSelectionPanel;
	private Button createButton;

	/**
	 * View popup initialization.
	 */
	protected ProfileEditView() {
		super(new PopupWidget(true));
		popup.setWidth(null); // Enables auto-width.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		formPanel = Forms.panel(150);

		// --
		// Name field.
		// --

		nameField = Forms.text(I18N.CONSTANTS.adminProfilesName(), true);

		// --
		// Global Permissions fields.
		// --

		permissionTree = new PermissionTree();
		permissionTree.expandAll();

		// --
		// Privacy groups / permissions field.
		// --

		privacyGroupsField =
				new ComboboxButtonField(I18N.CONSTANTS.adminProfilesPrivacyGroups(), new Pair<String, String>(PrivacyGroupDTO.ID, PrivacyGroupDTO.TITLE),
					new Pair<String, String>(EnumModel.VALUE_FIELD, EnumModel.DISPLAY_FIELD));

		getPrivacyGroupsComboBox().setEmptyText(I18N.CONSTANTS.adminPrivacyGroupChoice());

		privacyGroupsSelectionPanel = new FlowPanel();

		// --
		// Create button.
		// --

		createButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());

		// --
		// Form initialization.
		// --

		// #670: Resized panels to make the popup smaller.
		formPanel.add(nameField);
		formPanel.add(Forms.adapterWithScrollbars(I18N.CONSTANTS.adminProfilesGlobalPermissions(), permissionTree, 400, 300));
		formPanel.add(privacyGroupsField);
		formPanel.add(Forms.adapterWithScrollbars(null, privacyGroupsSelectionPanel, 400, 120));
		formPanel.addButton(createButton);

		initPopup(formPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearForm() {

		nameField.clear();
		privacyGroupsField.clearSelections();
		privacyGroupsSelectionPanel.clear();
		permissionTree.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPrivacyGroup(final PrivacyGroupDTO privacyGroup, final PrivacyGroupPermissionEnum permission, final ClickHandler deleteHandler) {

		final ClickableLabel label =
				new ClickableLabel(privacyGroup.getCode() + "-" + privacyGroup.getTitle() + " : " + PrivacyGroupPermissionEnum.getName(permission));

		label.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				if (deleteHandler != null) {
					deleteHandler.onClick(event);
					label.removeFromParent();
				}
			}
		});

		privacyGroupsSelectionPanel.add(label);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPermissionValue(final GlobalPermissionEnum globalPermission, final Boolean value) {
		permissionTree.setPermission(globalPermission, value);
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
	public Field<String> getNameField() {
		return nameField;
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
	public Set<GlobalPermissionEnum> getSelectedGlobalPermissions() {
		return permissionTree.getPermissions();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<PrivacyGroupDTO> getPrivacyGroupsComboBox() {
		return privacyGroupsField.getComboBox(0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<EnumModel<PrivacyGroupPermissionEnum>> getPrivacyGroupsPermissionsComboBox() {
		return privacyGroupsField.getComboBox(1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getPrivacyGroupsAddButton() {
		return privacyGroupsField.getButton();
	}

}
