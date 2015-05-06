package org.sigmah.client.ui.presenter;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget.LoadingMode;
import org.sigmah.client.ui.presenter.project.treegrid.ProjectsListWidget.RefreshMode;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.DashboardView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.HasTreeGrid;
import org.sigmah.client.ui.widget.HasTreeGrid.TreeGridEventHandler;
import org.sigmah.client.ui.widget.WorkInProgressWidget;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.GetMonitoredPoints;
import org.sigmah.shared.command.GetOrgUnit;
import org.sigmah.shared.command.GetReminders;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.util.ProfileUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.offline.sync.UpdateDates;

/**
 * Dashboard page presenter.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class DashboardPresenter extends AbstractPagePresenter<DashboardPresenter.View> {

	/**
	 * View interface.
	 */
	@ImplementedBy(DashboardView.class)
	public static interface View extends ViewInterface {

		/**
		 * Returns the reminders wrapper component.
		 * 
		 * @return The reminders wrapper component.
		 */
		Component getRemindersPanel();

		/**
		 * Returns the reminders list store.
		 * 
		 * @return The reminders list store.
		 */
		ListStore<ReminderDTO> getRemindersStore();

		/**
		 * Returns the monitored points wrapper component.
		 * 
		 * @return The monitored points wrapper component.
		 */
		Component getMonitoredPointsPanel();

		/**
		 * Returns the monitored points list store.
		 * 
		 * @return The monitored points list store.
		 */
		ListStore<MonitoredPointDTO> getMonitoredPointsStore();

		/**
		 * Clears the menu buttons component and removes all the buttons.
		 */
		void clearMenuButtons();

		/**
		 * Adds a navigation button to the menu buttons panel.
		 * 
		 * @param buttonText
		 *          (required) The button label.
		 * @param buttonIcon
		 *          (optional) Button icon displayed next to the label.
		 * @param clickHandler
		 *          The button click handler implementation.
		 */
		void addMenuButton(final String buttonText, final AbstractImagePrototype buttonIcon, final Listener<ButtonEvent> clickHandler);

		/**
		 * Returns the OrgUnit tree grid component.
		 * 
		 * @return The OrgUnit tree grid component.
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
		 * Returns the {@link ProjectsListWidget} widget.
		 * 
		 * @return The projects list widget.
		 */
		ProjectsListWidget getProjectsList();

	}
	
	private Integer lastUserId;
	
	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public DashboardPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.DASHBOARD;
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
		view.getProjectsList().init(RefreshMode.BOTH, LoadingMode.CHUNK);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		// Reloads the reminders store.
		loadReminders();

		// Reloads the monitored points store.
		loadMonitoredPoints();

		// Initializes menu buttons.
		initializeMenuButtons();

		// Reloads OrgUnits.
		loadOrgUnits();

		// Reloads projects.
		view.getProjectsList().refresh(true, auth().getOrgUnitId());
		
		// Ask the user to synchronize its favorite projects.
		// BUGFIX #701: only showing this message if the user is online.
		final boolean userIsOnline = injector.getApplicationStateManager().getState() == ApplicationState.ONLINE;
		final boolean userIsDifferent = auth().getUserId() != null && !auth().getUserId().equals(lastUserId);
		final boolean userHasSynchronized = UpdateDates.getDatabaseUpdateDate(auth()) != null;
		if(userIsOnline && userIsDifferent && !userHasSynchronized) {
			N10N.confirmation(I18N.CONSTANTS.offline(), I18N.CONSTANTS.sigmahOfflineWelcome(), new ConfirmCallback() {

				@Override
				public void onAction() {
					eventBus.updateZoneRequest(Zone.OFFLINE_BANNER.requestWith(RequestParameter.PULL_DATABASE, true));
				}
			});
		}
		lastUserId = auth().getUserId();
	}

	// -------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------------------

	/**
	 * Performs a dispatch command to load the reminders list and populates the view store.
	 */
	private void loadReminders() {

		dispatch.execute(new GetReminders(ReminderDTO.Mode.BASE), new CommandResultHandler<ListResult<ReminderDTO>>() {

			@Override
			public void onCommandSuccess(final ListResult<ReminderDTO> result) {

				final List<ReminderDTO> reminderListToLoad = new ArrayList<ReminderDTO>();

				// Only show the undeleted reminders.
				for (final ReminderDTO reminder : result.getList()) {

					// TODO [PERF] Filter should be performed on server-side.
					final Boolean deleted = reminder.getDeleted();

					if (Log.isDebugEnabled()) {
						Log.debug("Deleted reminder? " + deleted);
					}

					if (ClientUtils.isNotTrue(deleted)) {
						reminderListToLoad.add(reminder);
					}
				}

				view.getRemindersStore().removeAll();
				view.getRemindersStore().add(reminderListToLoad);
			}
		}, new LoadingMask(view.getRemindersPanel()));
	}

	/**
	 * Performs a dispatch command to load the monitored points list and populates the view store.
	 */
	private void loadMonitoredPoints() {

		dispatch.execute(new GetMonitoredPoints(MonitoredPointDTO.Mode.BASE), new CommandResultHandler<ListResult<MonitoredPointDTO>>() {

			@Override
			public void onCommandSuccess(final ListResult<MonitoredPointDTO> result) {

				final List<MonitoredPointDTO> pointListToLoad = new ArrayList<MonitoredPointDTO>();

				// Only show the undeleted monitored points.
				for (final MonitoredPointDTO p : result.getList()) {

					// TODO [PERF] Filter should be performed on server-side.
					final Boolean deleted = p.getDeleted();

					if (Log.isDebugEnabled()) {
						Log.debug("Deleted monitored point? " + deleted);
					}

					if (ClientUtils.isNotTrue(deleted)) {
						pointListToLoad.add(p);
					}
				}

				view.getMonitoredPointsStore().removeAll();
				view.getMonitoredPointsStore().add(pointListToLoad);
			}
		}, new LoadingMask(view.getMonitoredPointsPanel()));
	}

	/**
	 * Initializes menu buttons based on authenticated user profile permissions.
	 */
	private void initializeMenuButtons() {

		view.clearMenuButtons();

		// Create project.
		if (ProfileUtils.isGranted(auth(), GlobalPermissionEnum.CREATE_PROJECT)) {

			final PageRequest request = new PageRequest(Page.CREATE_PROJECT);
			request.addParameter(RequestParameter.TYPE, CreateProjectPresenter.Mode.PROJECT);

			view.addMenuButton(I18N.CONSTANTS.createProjectNewProject(), IconImageBundle.ICONS.add(), new ButtonClickHandler(request));
		}

		// Draft project.
		if (ProfileUtils.isGranted(auth(), GlobalPermissionEnum.CREATE_TEST_PROJECT)) {

			final PageRequest request = new PageRequest(Page.CREATE_PROJECT);
			request.addParameter(RequestParameter.TYPE, CreateProjectPresenter.Mode.TEST_PROJECT);

			view.addMenuButton(I18N.CONSTANTS.createTestProject(), IconImageBundle.ICONS.add(), new ButtonClickHandler(request));
		}

		// Users administration.
		if (ProfileUtils.isGranted(auth(), GlobalPermissionEnum.VIEW_ADMIN)) {
			view.addMenuButton(I18N.CONSTANTS.adminboard(), IconImageBundle.ICONS.setup(), new ButtonClickHandler(getDefaultAdminPage()));
		}

		// Import.
		if (ProfileUtils.isGranted(auth(), GlobalPermissionEnum.IMPORT_BUTTON)) {
			view.addMenuButton(I18N.CONSTANTS.importItem(), null, new ButtonClickHandler(Page.IMPORT_VALUES));
		}

		// TODO Handle other menus buttons.
		// There are two ways to show these menus (authentication / profile).
		// if (auth().isShowMenus()) {
		// view.addMenuButton(I18N.CONSTANTS.dataEntry(), IconImageBundle.ICONS.dataEntry(), new SiteGridPageState());
		// view.addMenuButton(I18N.CONSTANTS.reports(), IconImageBundle.ICONS.report(), new ReportListPageState());
		// view.addMenuButton(I18N.CONSTANTS.charts(), IconImageBundle.ICONS.barChart(), new ChartPageState());
		// view.addMenuButton(I18N.CONSTANTS.maps(), IconImageBundle.ICONS.map(), new MapPageState());
		// view.addMenuButton(I18N.CONSTANTS.tables(), IconImageBundle.ICONS.table(), new PivotPageState());
		// view.addMenuButton(I18N.CONSTANTS.setup(), IconImageBundle.ICONS.setup(), new DbListPageState());
		// }
	}
	
	/**
	 * Find the page of the administration to show by default for the current user.
	 * 
	 * @return First page of the administration to show.
	 */
	private Page getDefaultAdminPage() {
		final Page[] administrationPages = new Page[] {
			Page.ADMIN_USERS, Page.ADMIN_ORG_UNITS, 
			Page.ADMIN_PROJECTS_MODELS, Page.ADMIN_ORG_UNITS_MODELS, 
			Page.ADMIN_REPORTS_MODELS, Page.ADMIN_CATEGORIES, 
			Page.ADMIN_IMPORTATION_SCHEME, Page.ADMIN_PARAMETERS
		};
		
		final GlobalPermissionEnum[] accessRights = new GlobalPermissionEnum[] {
			GlobalPermissionEnum.MANAGE_USERS, GlobalPermissionEnum.MANAGE_ORG_UNITS,
			GlobalPermissionEnum.MANAGE_PROJECT_MODELS, GlobalPermissionEnum.MANAGE_ORG_UNIT_MODELS,
			GlobalPermissionEnum.MANAGE_REPORT_MODELS, GlobalPermissionEnum.MANAGE_CATEGORIES,
			GlobalPermissionEnum.MANAGE_IMPORTATION_SCHEMES, GlobalPermissionEnum.MANAGE_SETTINGS
		};
		
		for(int index = 0; index < accessRights.length; index++) {
			if(ProfileUtils.isGranted(auth(), accessRights[index])) {
				return administrationPages[index];
			}
		}
		
		return Page.ADMIN_USERS;
	}

	/**
	 * {@link Listener} implementation navigating to a given {@link PageRequest}.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	private class ButtonClickHandler implements Listener<ButtonEvent> {

		private final PageRequest request;

		private ButtonClickHandler(final Page page) {
			this.request = page != null ? page.request() : null;
		}

		private ButtonClickHandler(final PageRequest request) {
			this.request = request;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void handleEvent(final ButtonEvent be) {

			if (request != null) {
				eventBus.navigateRequest(request);

			} else {
				WorkInProgressWidget.popup(true);
			}
		}
	}

	/**
	 * Retrieves OrgUnits and populates tree grid store.
	 */
	private void loadOrgUnits() {

		dispatch.execute(new GetOrgUnit(auth().getOrgUnitId(), OrgUnitDTO.Mode.WITH_TREE), new CommandResultHandler<OrgUnitDTO>() {

			@Override
			public void onCommandSuccess(final OrgUnitDTO result) {

				view.setOrgUnitsPanelTitle(I18N.CONSTANTS.orgunitTree() + " - " + result.getName() + " (" + result.getFullName() + ")");
				view.getOrgUnitsTreeGrid().getStore().removeAll();
				view.getOrgUnitsTreeGrid().getStore().add(result, true);
				view.getOrgUnitsTreeGrid().getTreeGrid().setExpanded(view.getOrgUnitsTreeGrid().getStore().getRootItems().get(0), true, false);
			}
		});
	}

}
