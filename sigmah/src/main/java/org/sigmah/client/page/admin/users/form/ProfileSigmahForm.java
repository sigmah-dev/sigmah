/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.admin.users.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.ui.ClickableLabel;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetPrivacyGroups;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.PrivacyGroupsListResult;
import org.sigmah.shared.domain.profile.GlobalPermissionEnum;
import org.sigmah.shared.domain.profile.PrivacyGroupPermissionEnum;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupPermDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Grid;

/**
 * Create profile form.
 * 
 * @author nrebiai
 * 
 */
public class ProfileSigmahForm extends FormPanel {

	private final TextField<String> nameField;
	private final ProfileDTO profileToUpdate;
	private final List<CheckBoxGroup> checkGlobalPermissions;
	private final ComboBox<PrivacyGroupDTO> privacyGroupsListCombo;
	private final ComboBox<PrivacyGroupPermDTO> privacyGroupsPermissionsListCombo;
	private final Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> privacyGroupsPerms = new HashMap<PrivacyGroupDTO, PrivacyGroupPermissionEnum>();
	private final Map<Integer, ClickableLabel> selectedPrivacyGroups = new HashMap<Integer, ClickableLabel>();
	private final List<Integer> selectedPrivacyGroupsIds = new ArrayList<Integer>();
	private int num = 0;
	private final Dispatcher dispatcher;

	private final static int LABEL_WIDTH = 90;
	private final static int MAX_PRIVACY_GROUPS_TENTATIVES_PER_USER = 100;
	private final static String PRIVACY_GROUP = "idPrivacyGroup";

	public ProfileSigmahForm(Dispatcher dispatcher, UserLocalCache userCache,
	                final AsyncCallback<CreateResult> callback, ProfileDTO profileToUpdate) {

		this.dispatcher = dispatcher;
		this.profileToUpdate = profileToUpdate;
		UIConstants constants = GWT.create(UIConstants.class);

		FormLayout layoutTop = new FormLayout();
		layoutTop.setLabelWidth(LABEL_WIDTH);
		// setLayout(layout);
		// setScrollMode(Scroll.AUTOY);

		LayoutContainer top = new LayoutContainer();
		top.setLayout(layoutTop);

		LayoutContainer main = new LayoutContainer();
		main.setLayout(new ColumnLayout());

		LayoutContainer left = new LayoutContainer();
		left.setStyleAttribute("padding", "5px 0px 5px 0px");
		FormLayout layoutLeft = new FormLayout();
		layoutLeft.setLabelAlign(LabelAlign.TOP);
		// left.setStyleAttribute("margin", "3px 0px 0px 3px");
		// left.setAutoWidth(true);
		left.setLayout(layoutLeft);

		LayoutContainer middle = new LayoutContainer();
		middle.setStyleAttribute("padding", "5px 0px 5px 0px");
		FormLayout layoutMiddle = new FormLayout();
		layoutMiddle.setLabelAlign(LabelAlign.TOP);
		// middle.setStyleAttribute("margin", "3px 0px 0px 3px");
		// middle.setAutoWidth(true);
		middle.setLayout(layoutMiddle);

		LayoutContainer right = new LayoutContainer();
		right.setStyleAttribute("padding", "5px 0px 5px 0px");
		FormLayout layoutRight = new FormLayout();
		layoutRight.setLabelAlign(LabelAlign.TOP);
		// right.setStyleAttribute("margin", "3px 0px 0px 3px");
		// right.setAutoWidth(true);
		right.setLayout(layoutRight);

		LayoutContainer bottom = new LayoutContainer();
		FormLayout layoutBottom = new FormLayout();
		layoutBottom.setLabelWidth(LABEL_WIDTH);
		bottom.setLayout(layoutBottom);

		nameField = new TextField<String>();
		nameField.setFieldLabel(constants.adminProfilesName());
		nameField.setAllowBlank(false);

		if (profileToUpdate != null && !profileToUpdate.getName().isEmpty())
			nameField.setValue(profileToUpdate.getName());
		top.add(nameField);

		/*
		 * ************************************Global Permissions
		 * *******************************************
		 */

		checkGlobalPermissions = new ArrayList<CheckBoxGroup>();

		CheckBoxGroup checkGPGroupLabel = new CheckBoxGroup();
		checkGPGroupLabel.setOrientation(Orientation.VERTICAL);
		checkGPGroupLabel.setFieldLabel(I18N.CONSTANTS.adminProfilesGlobalPermissions());

		CheckBoxGroup checkGPGroupAdmin = new CheckBoxGroup();
		checkGPGroupAdmin.setOrientation(Orientation.VERTICAL);
		checkGPGroupAdmin.setFieldLabel(I18N.CONSTANTS.categoryAdministration());
		checkGPGroupAdmin.setStyleAttribute("margin-bottom", "5px");
		checkGPGroupAdmin.setLabelStyle("font-weight:bold;");

		CheckBoxGroup checkGPGroupProject = new CheckBoxGroup();
		checkGPGroupProject.setOrientation(Orientation.VERTICAL);
		checkGPGroupProject.setFieldLabel(I18N.CONSTANTS.categoryProject());
		checkGPGroupProject.setLabelStyle("font-weight:bold;");

		CheckBoxGroup checkGPGroupOthers = new CheckBoxGroup();
		checkGPGroupOthers.setOrientation(Orientation.VERTICAL);
		checkGPGroupOthers.setFieldLabel(I18N.CONSTANTS.categoryOthers());
		checkGPGroupOthers.setLabelStyle("font-weight:bold;");

		CheckBoxGroup checkGPGroupNotMapped = new CheckBoxGroup();
		checkGPGroupNotMapped.setOrientation(Orientation.VERTICAL);
		checkGPGroupNotMapped.setFieldLabel(I18N.CONSTANTS.categoryNotMapped());
		checkGPGroupNotMapped.setLabelStyle("font-weight:bold;");

		boolean notMappedEmpty = true;

		String label = "";
		for (GlobalPermissionEnum enumItem : GlobalPermissionEnum.values()) {
			label = GlobalPermissionEnum.getName(enumItem);
			CheckBox box = createCheckBox(enumItem.toString(), label);
			if (profileToUpdate != null && profileToUpdate.getGlobalPermissions() != null
			                && profileToUpdate.getGlobalPermissions().contains(enumItem)) {
				box.setValue(true);
			}
			if (GlobalPermissionEnum.getCategory(enumItem).equals(I18N.CONSTANTS.categoryAdministration())) {
				checkGPGroupAdmin.add(box);
				continue;
			}

			if (GlobalPermissionEnum.getCategory(enumItem).equals(I18N.CONSTANTS.categoryProject())) {
				checkGPGroupProject.add(box);
				continue;
			}
			if (GlobalPermissionEnum.getCategory(enumItem).equals(I18N.CONSTANTS.categoryOthers())) {
				checkGPGroupOthers.add(box);
				continue;
			}
			if (GlobalPermissionEnum.getCategory(enumItem).equals(I18N.CONSTANTS.categoryNotMapped())) {
				checkGPGroupNotMapped.add(box);
				notMappedEmpty = false;
				continue;
			}
		}
		checkGlobalPermissions.add(checkGPGroupAdmin);
		checkGlobalPermissions.add(checkGPGroupProject);
		checkGlobalPermissions.add(checkGPGroupOthers);
		if (!notMappedEmpty)
			checkGlobalPermissions.add(checkGPGroupNotMapped);
		// left.add(new Label(I18N.CONSTANTS.categoryProject()));

		left.add(checkGPGroupLabel);

		middle.add(checkGPGroupProject);

		// right.add(new Label(I18N.CONSTANTS.categoryAdministration()));
		right.add(checkGPGroupAdmin);

		// right.add(new Label(I18N.CONSTANTS.categoryOthers()));
		right.add(checkGPGroupOthers);

		if (!notMappedEmpty) {
			// right.add(new Label(I18N.CONSTANTS.categoryNotMapped()));
			checkGPGroupOthers.setStyleAttribute("margin-bottom", "5px");
			right.add(checkGPGroupNotMapped);
		}

		// main.add(left, new ColumnData(.333));
		// main.add(middle, new ColumnData(.333));
		// main.add(right, new ColumnData(.333));

		main.add(left, new ColumnData(.20));
		main.add(middle, new ColumnData(.30));
		main.add(right, new ColumnData(.30));

		/*
		 * ************************************Privacy Groups
		 * *******************************************
		 */
		// Add hidden empty labels
		for (int i1 = 0; i1 < MAX_PRIVACY_GROUPS_TENTATIVES_PER_USER; i1++) {
			final ClickableLabel pglabel = new ClickableLabel();
			pglabel.hide();
			pglabel.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					pglabel.removeFromParent();

					selectedPrivacyGroupsIds.remove(new Integer(((PrivacyGroupDTO) pglabel.getData(PRIVACY_GROUP))
					                .getId()));
					if (privacyGroupsPerms.containsKey((PrivacyGroupDTO) pglabel.getData(PRIVACY_GROUP))) {
					}
					privacyGroupsPerms.remove((PrivacyGroupDTO) pglabel.getData(PRIVACY_GROUP));
				}

			});
			selectedPrivacyGroups.put(i1, pglabel);
		}

		// List all types of permissions
		privacyGroupsPermissionsListCombo = new ComboBox<PrivacyGroupPermDTO>();
		privacyGroupsPermissionsListCombo.setDisplayField("permission");
		privacyGroupsPermissionsListCombo.setValueField("id");
		privacyGroupsPermissionsListCombo.setEditable(false);
		privacyGroupsPermissionsListCombo.setTriggerAction(TriggerAction.ALL);
		int j = 0;
		ListStore<PrivacyGroupPermDTO> permsStore = new ListStore<PrivacyGroupPermDTO>();
		for (PrivacyGroupPermissionEnum enumItem : PrivacyGroupPermissionEnum.values()) {
			j++;
			PrivacyGroupPermDTO perm = new PrivacyGroupPermDTO();
			perm.setId(j);
			perm.setPermission(PrivacyGroupPermissionEnum.getName(enumItem));
			if (!PrivacyGroupPermissionEnum.NONE.equals(enumItem))
				permsStore.add(perm);
		}
		privacyGroupsPermissionsListCombo.setStore(permsStore);

		privacyGroupsListCombo = new ComboBox<PrivacyGroupDTO>();
		privacyGroupsListCombo.setDisplayField("title");
		privacyGroupsListCombo.setValueField("id");
		privacyGroupsListCombo.setEditable(false);
		privacyGroupsListCombo.setTriggerAction(TriggerAction.ALL);
		final ListStore<PrivacyGroupDTO> privacyGroupsStore = new ListStore<PrivacyGroupDTO>();
		dispatcher.execute(new GetPrivacyGroups(), null, new AsyncCallback<PrivacyGroupsListResult>() {

			@Override
			public void onFailure(Throwable arg0) {
				privacyGroupsListCombo.setEmptyText(I18N.CONSTANTS.adminChoiceProblem());
			}

			@Override
			public void onSuccess(PrivacyGroupsListResult result) {
				privacyGroupsListCombo.setEmptyText(I18N.CONSTANTS.adminPrivacyGroupChoice());
				privacyGroupsStore.removeAll();
				if (result != null) {
					privacyGroupsStore.add(result.getList());
					privacyGroupsStore.commitChanges();
				}
			}
		});

		privacyGroupsListCombo.setStore(privacyGroupsStore);

		final Grid privacyGroupsAddSelectionGrid = new Grid(1, 4);

		privacyGroupsAddSelectionGrid.getCellFormatter().setWidth(0, 0, (LABEL_WIDTH + 5) + "px");
		privacyGroupsAddSelectionGrid.setCellPadding(0);
		privacyGroupsAddSelectionGrid.setCellSpacing(0);
		privacyGroupsAddSelectionGrid
		                .setWidget(0, 0, new LabelField(I18N.CONSTANTS.adminProfilesPrivacyGroups() + ":"));
		privacyGroupsAddSelectionGrid.setWidget(0, 1, privacyGroupsListCombo);
		privacyGroupsAddSelectionGrid.setWidget(0, 2, privacyGroupsPermissionsListCombo);
		privacyGroupsListCombo.setHideLabel(false);

		// Case Edit Profile add ids
		if (profileToUpdate != null && profileToUpdate.getPrivacyGroups() != null) {
			Set<PrivacyGroupDTO> usedPrivacyGroups = profileToUpdate.getPrivacyGroups().keySet();
			for (PrivacyGroupDTO usedPrivacyGroup : usedPrivacyGroups) {
				selectedPrivacyGroupsIds.add(new Integer(usedPrivacyGroup.getId()));
				privacyGroupsPerms.put(usedPrivacyGroup, profileToUpdate.getPrivacyGroups().get(usedPrivacyGroup));
			}
		}

		final Button addButton = new Button(I18N.CONSTANTS.addItem());
		addButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				if (privacyGroupsListCombo.getValue() != null) {

					if (!selectedPrivacyGroupsIds.contains(new Integer(privacyGroupsListCombo.getValue().getId()))) {
						if (num < MAX_PRIVACY_GROUPS_TENTATIVES_PER_USER) {
							selectedPrivacyGroups.get(num).setData(PRIVACY_GROUP, privacyGroupsListCombo.getValue());
							selectedPrivacyGroups.get(num).setText(
							                privacyGroupsListCombo.getValue().getCode()
							                                + "-"
							                                + privacyGroupsListCombo.getValue().getTitle()
							                                + " : "
							                                + privacyGroupsPermissionsListCombo.getValue()
							                                                .getPermission());
							selectedPrivacyGroups.get(num).show();
							num++;
							selectedPrivacyGroupsIds.add(new Integer(privacyGroupsListCombo.getValue().getId()));
							privacyGroupsPerms.put(privacyGroupsListCombo.getValue(), PrivacyGroupPermissionEnum
							                .translatePGPermission(privacyGroupsPermissionsListCombo.getValue()
							                                .getPermission()));

						} else {
							MessageBox.alert(I18N.CONSTANTS.adminMaxAttempts(),
							                I18N.CONSTANTS.adminMaxAttemptsProfiles(), null);
							ProfileSigmahForm.this.hide();
						}
					}
				}
			}
		});

		privacyGroupsAddSelectionGrid.setWidget(0, 3, addButton);
		bottom.add(privacyGroupsAddSelectionGrid);

		// Case Edit Profile : display used privacy groups
		if (profileToUpdate != null && profileToUpdate.getPrivacyGroups() != null) {
			Set<PrivacyGroupDTO> usedPrivacyGroups = profileToUpdate.getPrivacyGroups().keySet();
			for (PrivacyGroupDTO usedPrivacyGroup : usedPrivacyGroups) {
				if (num < MAX_PRIVACY_GROUPS_TENTATIVES_PER_USER) {
					selectedPrivacyGroups.get(num).setData(PRIVACY_GROUP, usedPrivacyGroup);
					selectedPrivacyGroups.get(num).setText(
					                usedPrivacyGroup.getCode()
					                                + "-"
					                                + usedPrivacyGroup.getTitle()
					                                + " : "
					                                + PrivacyGroupPermissionEnum.getName(profileToUpdate
					                                                .getPrivacyGroups().get(usedPrivacyGroup)));
					selectedPrivacyGroups.get(num).show();
					num++;
				} else {
					MessageBox.alert("Maximum Attempts",
					                "Maximum attempts to modify user's profiles have been reached. Try again", null);
					ProfileSigmahForm.this.hide();
				}
			}
		}

		for (ClickableLabel selected : selectedPrivacyGroups.values()) {
			bottom.add(selected);
		}

		// Create button.
		final Button createButton = new Button(I18N.CONSTANTS.save());
		createButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				createProfile(callback);
			}
		});
		bottom.add(createButton);

		add(top, new FormData("100%"));
		add(main, new FormData("100%"));
		add(bottom, new FormData("100%"));

	}

	protected CheckBox createCheckBox(String property, String label) {
		CheckBox box = new CheckBox();
		box.setName(property);
		box.setBoxLabel(label);
		return box;
	}

	private void createProfile(final AsyncCallback<CreateResult> callback) {

		mask(I18N.CONSTANTS.saving());

		if (!this.isValid()) {
			MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
			                I18N.MESSAGES.createFormIncompleteDetails(I18N.MESSAGES.adminStandardProfile()), null);
			unmask();
			return;
		}

		ProfileDTO profileToSave = new ProfileDTO();
		HashMap<String, Object> newProfileProperties = new HashMap<String, Object>();

		if (profileToUpdate != null) {
			profileToSave.setId(profileToUpdate.getId());
		}
		// name
		final String name = nameField.getValue();
		profileToSave.setName(name);

		// global permissions
		final Set<GlobalPermissionEnum> globalPerms = new HashSet<GlobalPermissionEnum>();
		for (CheckBoxGroup checkGPGroup : checkGlobalPermissions) {
			List<CheckBox> checkedGlobalPermissions = checkGPGroup.getValues();
			for (CheckBox checkedGlobalPermission : checkedGlobalPermissions) {
				globalPerms.add(GlobalPermissionEnum.valueOf(checkedGlobalPermission.getName()));
			}
		}
		profileToSave.setGlobalPermissions(globalPerms);
		profileToSave.setPrivacyGroups(privacyGroupsPerms);
		newProfileProperties.put("profile", profileToSave);
		dispatcher.execute(new CreateEntity("Profile", newProfileProperties), null, new AsyncCallback<CreateResult>() {
			public void onFailure(Throwable caught) {
				MessageBox.alert(
				                I18N.CONSTANTS.adminProfileCreationBox(),
				                I18N.MESSAGES.adminStandardCreationFailure(I18N.MESSAGES.adminStandardProfile() + " '"
				                                + name + "'"), null);
				callback.onFailure(caught);
				unmask();
			}

			@Override
			public void onSuccess(CreateResult result) {
				if (result != null) {
					callback.onSuccess(result);
					if (profileToUpdate != null) {
						Notification.show(
						                I18N.CONSTANTS.adminProfileCreationBox(),
						                I18N.MESSAGES.adminStandardUpdateSuccess(I18N.MESSAGES.adminStandardProfile()
						                                + " '" + result.getEntity().get("name") + "'"));
					} else {
						Notification.show(
						                I18N.CONSTANTS.adminProfileCreationBox(),
						                I18N.MESSAGES.adminStandardCreationSuccess(I18N.MESSAGES.adminStandardProfile()
						                                + " '" + result.getEntity().get("name") + "'"));
					}

				} else {
					Throwable t = new Throwable("ProfileSigmahForm : creation result is null");
					callback.onFailure(t);
					MessageBox.alert(
					                I18N.CONSTANTS.adminProfileCreationBox(),
					                I18N.MESSAGES.adminStandardCreationNull(I18N.MESSAGES.adminStandardProfile() + " '"
					                                + name + "'"), null);
				}
				unmask();
			}
		});

	}

}
