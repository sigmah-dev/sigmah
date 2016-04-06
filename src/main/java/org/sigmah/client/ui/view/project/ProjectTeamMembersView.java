package org.sigmah.client.ui.view.project;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Label;

import java.util.Arrays;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.project.ProjectTeamMembersPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.TeamMemberDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

public class ProjectTeamMembersView extends AbstractView implements ProjectTeamMembersPresenter.View {
	private ContentPanel mainPanel;
	private Grid<ModelData> teamMembersGrid;
	private ListStore<ModelData> teamMembersStore;
	private ProjectTeamMembersPresenter.RemoveTeamMemberButtonCreationHandler removeTeamMemberButtonCreationHandler;
	private Button addTeamMemberButton;
	private Button addTeamMemberByProfileButton;

	@Override
	public void initialize() {
		teamMembersStore = new ListStore<ModelData>();
		teamMembersStore.sort(TeamMemberDTO.ORDER, Style.SortDir.ASC);

		final Layout layout = Layouts.vBoxLayout(VBoxLayout.VBoxLayoutAlign.STRETCH,
				new Layouts.LayoutOptions(new Padding(5), false, Layouts.LayoutOptions.Scroll.VERTICAL));

		mainPanel = Panels.content(I18N.CONSTANTS.projectTabTeamMembers(), layout);
		mainPanel.setBorders(true);

		mainPanel.setTopComponent(buildToolbar());

		buildTeamMembersGrid();

		mainPanel.add(teamMembersGrid);

		add(mainPanel);
	}

	@Override
	public LayoutContainer getMainPanel() {
		return mainPanel;
	}

	@Override
	public Button getAddTeamMemberButton() {
		return addTeamMemberButton;
	}

	@Override
	public Button getAddTeamMemberByProfileButton() {
		return addTeamMemberByProfileButton;
	}

	@Override
	public ListStore<ModelData> getTeamMembersStore() {
		return teamMembersStore;
	}

	@Override
	public void setRemoveTeamMemberButtonCreationHandler(ProjectTeamMembersPresenter.RemoveTeamMemberButtonCreationHandler removeTeamMemberButtonCreationHandler) {
		this.removeTeamMemberButtonCreationHandler = removeTeamMemberButtonCreationHandler;
	}

	private ToolBar buildToolbar() {
		addTeamMemberButton = Forms.button(I18N.CONSTANTS.addTeamMemberButtonLabel(), IconImageBundle.ICONS.addUser());
		addTeamMemberByProfileButton = Forms.button(I18N.CONSTANTS.addTeamMembersByProfileButtonLabel(), IconImageBundle.ICONS.add());

		// Actions toolbar.
		final ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(Style.HorizontalAlignment.LEFT);
		toolBar.setBorders(false);

		toolBar.add(addTeamMemberButton);
		toolBar.add(addTeamMemberByProfileButton);
		toolBar.add(new FillToolItem());

		return toolBar;
	}

	private void buildTeamMembersGrid() {
		ColumnConfig nameColumnConfig = new ColumnConfig(TeamMemberDTO.NAME, I18N.CONSTANTS.name(), 500);
		nameColumnConfig.setSortable(false);
		nameColumnConfig.setRenderer(new GridCellRenderer() {
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				switch ((TeamMemberDTO.TeamMemberType) model.get(TeamMemberDTO.TYPE)) {
					case MANAGER:
						return I18N.MESSAGES.projectTeamMemberManagerLabel(model.get(UserDTO.COMPLETE_NAME).toString());
					case TEAM_MEMBER_PROFILE:
						return I18N.MESSAGES.projectTeamMemberProfileLabel(model.get(ProfileDTO.NAME).toString());
					case TEAM_MEMBER:
						return model.get(UserDTO.COMPLETE_NAME);
					default:
						throw new IllegalStateException();
				}
			}
		});

		ColumnConfig actionsColumnConfig = new ColumnConfig("actions", "", 150);
		actionsColumnConfig.setSortable(false);
		actionsColumnConfig.setFixed(true);
		actionsColumnConfig.setRenderer(new GridCellRenderer() {
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				TeamMemberDTO.TeamMemberType type = model.get(TeamMemberDTO.TYPE);
				if (type == TeamMemberDTO.TeamMemberType.MANAGER) {
					return null;
				}

				Button button = new Button(I18N.CONSTANTS.removeItem());
				if (type == TeamMemberDTO.TeamMemberType.TEAM_MEMBER) {
					removeTeamMemberButtonCreationHandler.onCreateRemoveUserButton(button, (UserDTO) model);
				} else {
					// the data is supposed to be a TEAM_MEMBER_PROFILE
					removeTeamMemberButtonCreationHandler.onCreateRemoveProfileButton(button, (ProfileDTO) model);
				}
				return button;
			}
		});

		ColumnModel columnModel = new ColumnModel(Arrays.asList(nameColumnConfig, actionsColumnConfig));
		teamMembersGrid = new Grid<ModelData>(teamMembersStore, columnModel);
		teamMembersGrid.setAutoExpandColumn(TeamMemberDTO.NAME);
		teamMembersGrid.getView().setForceFit(true);
		teamMembersGrid.setAutoHeight(true);
	}

	@Override
	public void buildAddTeamMemberDialog(final ProjectTeamMembersPresenter.AddTeamMemberHandler handler, List<UserDTO> availableUsers) {
		final Window window = new Window();
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setLayout(new FitLayout());
		window.setSize(350, 180);
		window.setHeadingHtml(I18N.CONSTANTS.addTeamMemberDialogTitle());

		if (availableUsers.isEmpty()) {
			window.add(new Label(I18N.CONSTANTS.noAvailableUserToAddInTeamMembers()));
			window.show();
			return;
		}

		final FormPanel panel = Forms.panel();
		final Button validateButton = Forms.button(I18N.CONSTANTS.addTeamMemberButtonLabel());
		validateButton.setEnabled(false);
		panel.getButtonBar().add(validateButton);
		ListStore<UserDTO> userStore = new ListStore<UserDTO>();
		userStore.add(availableUsers);
		final ComboBox<UserDTO> userComboBox = Forms.combobox(I18N.CONSTANTS.addTeamMemberComboboxLabel(), true, UserDTO.ID,
				UserDTO.COMPLETE_NAME, I18N.CONSTANTS.selectTeamMemberEmptyChoice(), userStore);
		panel.add(userComboBox);
		window.add(panel);

		panel.getButtonBar().add(validateButton);
		validateButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(final ButtonEvent event) {
				handler.onAddTeamMember(userComboBox.getValue());
				window.hide();
			}
		});

		userComboBox.addSelectionChangedListener(new SelectionChangedListener<UserDTO>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<UserDTO> event) {
				validateButton.setEnabled(event.getSelectedItem() != null);
			}
		});

		window.show();
	}

	@Override
	public void buildAddTeamMembersByProfileDialog(final ProjectTeamMembersPresenter.SelectTeamMembersByProfileHandler handler,
																								 List<ProfileDTO> profiles) {
		final Window window = new Window();
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setLayout(new FitLayout());
		window.setSize(350, 180);
		window.setHeadingHtml(I18N.CONSTANTS.addTeamMembersByProfileDialogTitle());

		final FormPanel panel = Forms.panel();
		final Button validateButton = Forms.button(I18N.CONSTANTS.addTeamMembersByProfileButtonLabel());
		validateButton.setEnabled(false);
		panel.getButtonBar().add(validateButton);
		ListStore<ProfileDTO> profileStore = new ListStore<ProfileDTO>();
		profileStore.add(profiles);
		final ComboBox<ProfileDTO> profileComboBox = Forms.combobox(I18N.CONSTANTS.selectTeamMembersByProfileComboboxLabel(),
				true, ProfileDTO.ID, ProfileDTO.NAME, I18N.CONSTANTS.selectTeamMembersByProfileEmptyChoice(), profileStore);
		panel.add(profileComboBox);
		window.add(panel);

		panel.getButtonBar().add(validateButton);
		validateButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(final ButtonEvent event) {
				handler.onSelectTeamMemberByProfile(profileComboBox.getValue());
				window.hide();
			}
		});

		profileComboBox.addSelectionChangedListener(new SelectionChangedListener<ProfileDTO>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<ProfileDTO> event) {
				validateButton.setEnabled(event.getSelectedItem() != null);
			}
		});

		window.show();
	}
}
