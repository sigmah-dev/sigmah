package org.sigmah.client.ui.view.orgunit;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.orgunit.OrgUnitDashboardPresenter;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.HasTreeGrid;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.orgunit.OrgUnitTreeGrid;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */

@Singleton
public class OrgUnitDashboardView extends AbstractView implements OrgUnitDashboardPresenter.View {

	@Inject
	private Provider<ProjectsListWidget> projectsListWidgetProvider;

	private OrgUnitTreeGrid tree;
	private ContentPanel contentPanelOrgUnit;
	private ProjectsListWidget projectsListWidget;

	@Override
	public void initialize() {

		final LayoutContainer container = Layouts.vBox();
		container.add(createOrgUnitsPanel(), Layouts.vBoxData(Margin.BOTTOM));
		container.add(createProjectsPanel(), Layouts.vBoxData());

		add(container);
	}

	private Component createOrgUnitsPanel() {

		contentPanelOrgUnit = Panels.content(I18N.CONSTANTS.orgunitTree());
		tree = new OrgUnitTreeGrid(false);
		contentPanelOrgUnit.setTopComponent(tree.getToolbar());
		contentPanelOrgUnit.add(tree.getTreeGrid());

		return contentPanelOrgUnit;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOrgUnitsPanelTitle(String title) {
		contentPanelOrgUnit.setHeadingText(title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasTreeGrid<OrgUnitDTO> getOrgUnitsTreeGrid() {
		return tree;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectsListWidget getProjectsList() {
		return projectsListWidget;
	}

	/**
	 * Creates the projects component.
	 * 
	 * @return The projects component widget.
	 */
	private Widget createProjectsPanel() {

		projectsListWidget = projectsListWidgetProvider.get();
		projectsListWidget.initialize();

		return projectsListWidget.getView().asWidget();

	}

}
