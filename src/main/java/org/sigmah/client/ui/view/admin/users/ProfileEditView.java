package org.sigmah.client.ui.view.admin.users;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

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
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum.GlobalPermissionCategory;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;
import org.sigmah.shared.util.Pair;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
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

	private Map<GlobalPermissionEnum, CheckBox> globalPermissionCheckBoxes;

	private FormPanel formPanel;
	private Field<String> nameField;
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

		globalPermissionCheckBoxes = new HashMap<GlobalPermissionEnum, CheckBox>();
		formPanel = Forms.panel(150);

		// --
		// Name field.
		// --

		nameField = Forms.text(I18N.CONSTANTS.adminProfilesName(), true);

		// --
		// Global Permissions fields.
		// --

		// Ordered map.
		final Map<GlobalPermissionCategory, CheckBoxGroup> checkBoxGroups = new TreeMap<GlobalPermissionCategory, CheckBoxGroup>();

		for (final GlobalPermissionEnum globalPermission : GlobalPermissionEnum.values()) {

			final GlobalPermissionCategory category = globalPermission.getCategory();
			final CheckBox checkBox = Forms.checkbox(GlobalPermissionEnum.getName(globalPermission), globalPermission.name(), (Boolean) null);
			globalPermissionCheckBoxes.put(globalPermission, checkBox);

			if (checkBoxGroups.containsKey(category)) {
				// Existing group.
				checkBoxGroups.get(category).add(checkBox);

			} else {
				// New group.
				final CheckBoxGroup checkBoxGroup = Forms.checkBoxGroup(GlobalPermissionCategory.getName(category), Orientation.VERTICAL, checkBox);
				checkBoxGroup.setLabelStyle("font-weight:bold;");
				checkBoxGroups.put(category, checkBoxGroup);
			}
		}

		// Custom layout.
		final HorizontalPanel panel = new HorizontalPanel();
		for (final Iterator<CheckBoxGroup> groupsIterator = checkBoxGroups.values().iterator(); groupsIterator.hasNext();) {

			final CheckBoxGroup checkBoxGroup = groupsIterator.next();

			final FormLayout layout = new FormLayout();
			layout.setLabelAlign(LabelAlign.TOP);
			layout.setDefaultWidth(-1); // Auto-width.

			final FormData formData = new FormData();
			if (groupsIterator.hasNext()) {
				formData.setMargins(new Margins(0, 10, 0, 0));
			}

			final LayoutContainer container = new LayoutContainer(layout);
			container.setStyleAttribute("padding", "5px 0px");
			container.add(checkBoxGroup, formData);

			panel.add(container);
		}

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

		formPanel.add(nameField);
		formPanel.add(Forms.adapter(I18N.CONSTANTS.adminProfilesGlobalPermissions(), panel));
		formPanel.add(privacyGroupsField);
		formPanel.add(Forms.adapter(null, privacyGroupsSelectionPanel));
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

		for (final CheckBox checkBox : globalPermissionCheckBoxes.values()) {
			checkBox.clear();
		}
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
		globalPermissionCheckBoxes.get(globalPermission).setValue(value);
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

		final Set<GlobalPermissionEnum> selected = new HashSet<GlobalPermissionEnum>();

		for (final Entry<GlobalPermissionEnum, CheckBox> entry : globalPermissionCheckBoxes.entrySet()) {
			if (entry != null && ClientUtils.isTrue(entry.getValue().getValue())) {
				selected.add(entry.getKey());
			}
		}

		return selected;
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
