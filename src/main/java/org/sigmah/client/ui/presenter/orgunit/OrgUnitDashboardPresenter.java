package org.sigmah.client.ui.presenter.orgunit;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget.LoadingMode;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget.RefreshMode;
import org.sigmah.client.ui.view.orgunit.OrgUnitDashboardView;
import org.sigmah.client.ui.widget.HasTreeGrid;
import org.sigmah.client.ui.widget.HasTreeGrid.TreeGridEventHandler;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * OrgUnit Dashboard Presenter.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
@Singleton
public class OrgUnitDashboardPresenter extends AbstractOrgUnitPresenter<OrgUnitDashboardPresenter.View> {

	/**
	 * Presenter's view interface.
	 */
	@ImplementedBy(OrgUnitDashboardView.class)
	public static interface View extends AbstractOrgUnitPresenter.View {

		/**
		 * Returns the OrgUnit Tree
		 * 
		 * @return the OrgUnit Tree
		 */
		HasTreeGrid<OrgUnitDTO> getOrgUnitsTreeGrid();

		/**
		 * Sets the org units panel header title.
		 * 
		 * @param title
		 *          The new title.
		 */
		void setOrgUnitsPanelTitle(String title);

		/**
		 * get Project List Widget
		 * 
		 * @return ProjectsListWidget
		 */
		ProjectsListWidget getProjectsList();

	}

	@Inject
	public OrgUnitDashboardPresenter(View view, Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ORGUNIT_DASHBOARD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// Org units tree grid events handler.
		view.getOrgUnitsTreeGrid().setTreeGridEventHandler(new TreeGridEventHandler<OrgUnitDTO>() {

			@Override
			public void onRowClickEvent(final OrgUnitDTO rowElement) {
				eventBus.navigateRequest(Page.ORGUNIT_DASHBOARD.requestWith(RequestParameter.ID, rowElement.getId()));
			}
		});

		// Projects widget initialization.
		view.getProjectsList().init(RefreshMode.ALWAYS, LoadingMode.ONE_TIME);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(PageRequest request) {

		// Loads orgunit.
		loadOrgUnits(getOrgUnit());

		// Reloads projects.
		view.getProjectsList().refresh(false, getOrgUnit().getId());
	}

	/**
	 * Retrieves OrgUnits and populates tree grid store.
	 * 
	 * @param orgUnit
	 *          The current {@link OrgUnitDTO}.
	 */
	private void loadOrgUnits(final OrgUnitDTO orgUnit) {

		view.setOrgUnitsPanelTitle(I18N.CONSTANTS.orgunitTree() + " - " + orgUnit.getName() + " (" + orgUnit.getFullName() + ")");

		if (!orgUnit.getChildrenOrgUnits().isEmpty()) {

			view.getOrgUnitsTreeGrid().getStore().removeAll();

			for (final OrgUnitDTO childOrgUnit : orgUnit.getChildrenOrgUnits()) {
				view.getOrgUnitsTreeGrid().getStore().add(childOrgUnit, true);
			}

			view.getOrgUnitsTreeGrid().getTreeGrid().setExpanded(view.getOrgUnitsTreeGrid().getStore().getRootItems().get(0), true, false);

		} else {
			view.setOrgUnitsPanelTitle(I18N.CONSTANTS.orgunitTree() + " - " + orgUnit.getName() + " (" + orgUnit.getFullName() + ")");
			view.getOrgUnitsTreeGrid().getStore().removeAll();
		}
	}

}
