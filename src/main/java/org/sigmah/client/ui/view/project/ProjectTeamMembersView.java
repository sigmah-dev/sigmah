package org.sigmah.client.ui.view.project;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

import java.util.Arrays;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.project.ProjectTeamMembersPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.TeamMemberDTO;
import org.sigmah.shared.dto.UserDTO;

public class ProjectTeamMembersView extends AbstractView implements ProjectTeamMembersPresenter.View {
	private ContentPanel mainPanel;
	private Grid<ModelData> teamMembersGrid;
	private ListStore<ModelData> teamMembersStore;

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
	public ListStore<ModelData> getTeamMembersStore() {
		return teamMembersStore;
	}

	private ToolBar buildToolbar() {

		// TODO: Add "Save" button
		// TODO: Add "Add a member" button

		// Actions toolbar.
		final ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(Style.HorizontalAlignment.LEFT);
		toolBar.setBorders(false);

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

				// TODO: Add a button
				return null;
			}
		});

		ColumnModel columnModel = new ColumnModel(Arrays.asList(nameColumnConfig, actionsColumnConfig));
		teamMembersGrid = new Grid<ModelData>(teamMembersStore, columnModel);
		teamMembersGrid.setAutoExpandColumn(TeamMemberDTO.NAME);
		teamMembersGrid.getView().setForceFit(true);
		teamMembersGrid.setAutoHeight(true);
	}
}
