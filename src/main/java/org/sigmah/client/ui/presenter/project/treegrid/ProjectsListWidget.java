package org.sigmah.client.ui.presenter.project.treegrid;

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
import java.util.Date;
import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.project.treegrid.ProjectsListView;
import org.sigmah.client.ui.widget.HasTreeGrid;
import org.sigmah.client.ui.widget.HasTreeGrid.TreeGridEventHandler;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientConfiguration;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;
import org.sigmah.client.util.NumberUtils;
import org.sigmah.shared.command.GetCategories;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.UpdateProjectFavorite;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.sigmah.shared.util.ProfileUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.grid.filters.ListFilter;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.client.util.profiler.Scenario;

/**
 * <p>
 * Widget displaying a list of projects with a tree structure.
 * </p>
 * <p>
 * This widget respects MVP structure (presenter & view separation):
 * <ul>
 * <li>This class handles the widget functionalities (data loading, events management, etc.).</li>
 * <li>The widget's view is implemented by {@link ProjectsListView}.</li>
 * </ul>
 * </p>
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectsListWidget extends AbstractPresenter<ProjectsListWidget.View> {

	/**
	 * The widget view interface.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	@ImplementedBy(ProjectsListView.class)
	public static interface View extends ViewInterface, HasTreeGrid<ProjectDTO> {

		/**
		 * Time column key.
		 */
		static final String TIME_COLUMN = "time";

		/**
		 * Category filter key.
		 */
		static final String CATEGORY_FILTER = "categoryFilter";

		/**
		 * Allows the presenter to provide necessary handlers to the view.
		 * 
		 * @param handlerProvider
		 *          The {@link HandlerProvider} implementation.
		 */
		void setHandlerProvider(HandlerProvider handlerProvider);

		/**
		 * Updates the widget accessibility state.
		 * 
		 * @param authorized
		 *          If {@code true}, the widget is accessible and rendered. If {@code false}, the widget is hidden and a
		 *          proper label is rendered.
		 */
		void updateAccessibilityState(boolean authorized);

		ContentPanel getProjectsPanel();

		GridFilters getGridFilters();

		/**
		 * Returns the project model type field.
		 * 
		 * @return The project model type field.
		 */
		Field<ProjectModelType> getProjectModelTypeField();

		/**
		 * Display the given date as the last refreshed date.
		 * 
		 * @param date
		 *          The last refreshed date.
		 */
		void updateRefreshingDate(Date date);

		Button getRefreshButton();

		Button getFilterButton();

		/**
		 * Updates the view toolbar.
		 * 
		 * @param refresh
		 *          {@code true} to enable <em>refresh</em> functionality, {@code false} to disable it.
		 * @param export
		 *          {@code true} to enable <em>export</em> functionality, {@code false} to disable it.
		 */
		void updateToolbar(boolean refresh, boolean export);

		/**
		 * Returns the export button.
		 * 
		 * @return The export button.
		 */
		Button getExportButton();
		
		/**
		 * Resynchronize the size of the grid.
		 */
		void syncSize();

	}

	/**
	 * Allows the presenter to provide necessary handlers to the view.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static interface HandlerProvider {

		/**
		 * Returns if the given {@code model} is a favorite project.
		 * 
		 * @param model
		 *          The corresponding project.
		 * @return {@code true} if the given {@code model} is a favorite project, {@code false} otherwise.
		 */
		boolean isFavoriteProject(final ProjectDTO model);

		/**
		 * Returns the given {@code model} corresponding {@link ProjectModelType} value.
		 * 
		 * @param model
		 *          The corresponding project.
		 * @return The given {@code model} corresponding {@link ProjectModelType} value.
		 */
		ProjectModelType getProjectModelType(final ProjectDTO model);

		/**
		 * Method executed on given {@code model} corresponding star icon (favorite) click event.
		 * 
		 * @param model
		 *          The corresponding project.
		 */
		void onStarIconClicked(final ProjectDTO model);

	}

	/**
	 * Defines the refreshing mode.
	 * 
	 * @author Tom Miette (tmiette@ideia.fr)
	 */
	public static enum RefreshMode {

		/**
		 * The project list is refreshed each time the {@link ProjectsListWidget#refresh(boolean, Integer...)} method is
		 * called.
		 */
		ALWAYS,

		/**
		 * The project list is refreshed each time user clicks on the refresh button.
		 */
		MANUAL,

		/**
		 * Refresh the project list when {@link ProjectsListWidget#refresh(boolean, Integer...)} is called for the first
		 * time. Subsequent refreshs are called by the refresh button.
		 */
		ON_FIRST_TIME;

	}

	/**
	 * Defines the loading mode.
	 * 
	 * @author Tom Miette (tmiette@ideia.fr)
	 */
	public static enum LoadingMode {

		/**
		 * The projects list id loaded by one single server call.
		 */
		ONE_TIME,

		/**
		 * The projects list is loaded by chunks and a progress bar informs of the loading process.
		 */
		CHUNK;

	}

	// Current projects grid parameters.
	private ProjectModelType currentModelType;
	private final ArrayList<Integer> orgUnitsIds;

	/**
	 * The refreshing mode (automatic by default).
	 */
	private RefreshMode refreshMode;

	/**
	 * The loading mode (one time by default).
	 */
	private LoadingMode loadingMode;

	/**
	 * The GetProjects command which will be executed for the next refresh.
	 */
	private GetProjects command;

	/**
	 * Has the project grid already been loaded once?
	 */
	private boolean loaded;

	/**
	 * Builds a new project list panel with default values.
	 */
	@Inject
	public ProjectsListWidget(final View view, final Injector injector) {
		super(view, injector);
		this.orgUnitsIds = new ArrayList<Integer>();

		// Default values.
		init(null, null);

		// Default filters parameters.
		currentModelType = ProjectModelType.NGO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Listeners.
		// --

		addListeners();

		// --
		// Filters.
		// --

		addFilters();

		// --
		// Store sorter implementation.
		// --

		view.getStore().setStoreSorter(new StoreSorter<ProjectDTO>() {

			@Override
			public int compare(final Store<ProjectDTO> store, final ProjectDTO m1, final ProjectDTO m2, final String property) {

				if (ProjectDTO.NAME.equals(property)) {
					return m1.getName().compareToIgnoreCase(m2.getName());

				} else if (ProjectDTO.FULL_NAME.equals(property)) {
					return m1.getFullName().compareToIgnoreCase(m2.getFullName());

				} else if (ProjectDTO.CURRENT_PHASE_NAME.equals(property)) {
					return m1.getCurrentPhaseName().compareToIgnoreCase(m2.getCurrentPhaseName());

				} else if (ProjectDTO.ORG_UNIT_NAME.equals(property)) {
					return m1.getOrgUnitName().compareToIgnoreCase(m2.getOrgUnitName());

				} else if (ProjectDTO.SPEND_BUDGET.equals(property)) {
					final Double d1 = NumberUtils.adjustRatio(NumberUtils.ratio(m1.getSpendBudget(), m1.getPlannedBudget()));
					final Double d2 = NumberUtils.adjustRatio(NumberUtils.ratio(m2.getSpendBudget(), m2.getPlannedBudget()));
					return d1.compareTo(d2);

				} else if (View.TIME_COLUMN.equals(property)) {
					final Double d1 = m1.getElapsedTime();
					final Double d2 = m2.getElapsedTime();
					return d1.compareTo(d2);

				} else if (ProjectDTO.ACTIVITY_ADVANCEMENT.equals(property)) {
					final Integer a1 = m1.getActivityAdvancement();
					final Integer a2 = m2.getActivityAdvancement();
					return a1.compareTo(a2);

				} else if (ProjectDTO.CATEGORY_ELEMENTS.equals(property)) {
					return 0;

				} else {
					return super.compare(store, m1, m2, property);
				}
			}
		});

		// --
		// Project name click handler.
		// --

		view.setTreeGridEventHandler(new TreeGridEventHandler<ProjectDTO>() {

			@Override
			public void onRowClickEvent(final ProjectDTO rowElement) {
				Profiler.INSTANCE.startScenario(Scenario.OPEN_PROJECT);
				eventBus.navigateRequest(Page.PROJECT_DASHBOARD.requestWith(RequestParameter.ID, rowElement.getId()));
			}
		});

		// --
		// View's specific handlers.
		// --

		view.setHandlerProvider(new HandlerProvider() {

			@Override
			public boolean isFavoriteProject(final ProjectDTO model) {

				final Integer userId = auth().getUserId();

				if (userId == null || model == null || ClientUtils.isEmpty(model.getFavoriteUsers())) {
					return false;
				}

				for (final UserDTO u : model.getFavoriteUsers()) {
					if (u.getId().equals(userId)) {
						return true;
					}
				}

				return false;
			}

			@Override
			public ProjectModelType getProjectModelType(final ProjectDTO model) {
				if(model == null) {
					return ProjectModelType.NGO;
				}
				return model.getProjectModelType(auth().getOrganizationId());
			}

			@Override
			public void onStarIconClicked(final ProjectDTO model) {

				// Get the project's id from tree structure.
				final Integer projectId = model.getId();
				final UpdateProjectFavorite updateCmd;

				if (isFavoriteProject(model)) {
					// Remove the favorite user from project favorite user list
					updateCmd = new UpdateProjectFavorite(projectId, UpdateProjectFavorite.UpdateType.REMOVE);

				} else {
					// Add current user into the favorite user list of the project
					updateCmd = new UpdateProjectFavorite(projectId, UpdateProjectFavorite.UpdateType.ADD);
				}

				// RPC to change the favorite tag
				dispatch.execute(updateCmd, new CommandResultHandler<CreateResult>() {

					@Override
					public void onCommandFailure(final Throwable caught) {

						if (Log.isErrorEnabled()) {
							Log.error("Error while setting the favorite status of the project #" + projectId + ".", caught);
						}

						N10N.error(I18N.CONSTANTS.projectStarredError(), I18N.CONSTANTS.projectStarredErrorDetails());
					}

					@Override
					public void onCommandSuccess(final CreateResult result) {

						if (result == null || result.getEntity() == null) {
							N10N.warn(I18N.CONSTANTS.projectStarredError(), I18N.CONSTANTS.projectStarredErrorDetails());
							return;
						}

						// Updates local store.
						final ProjectDTO resultProject = (ProjectDTO) result.getEntity();
						model.setFavoriteUsers(resultProject.getFavoriteUsers());
						view.getStore().update(model);

						N10N.notification(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.projectStarred(), MessageType.VALID);
					}
				}, new LoadingMask(view.getProjectsPanel()));
			}
		});
	}

	/**
	 * <p>
	 * Initializes the widget.
	 * </p>
	 * <p>
	 * This method should be called in parent presenter's {@code onBind()} method.
	 * </p>
	 * 
	 * @param refreshMode
	 *          The {@link RefreshMode} value. If {@code null}, default {@link RefreshMode#ALWAYS} value is set.
	 * @param loadingMode
	 *          The {@link LoadingMode} value. If {@code null}, default {@link LoadingMode#ONE_TIME} value is set.
	 */
	public void init(final RefreshMode refreshMode, final LoadingMode loadingMode) {

		// Initializes values.
		this.refreshMode = refreshMode != null ? refreshMode : RefreshMode.ALWAYS;
		this.loadingMode = loadingMode != null ? loadingMode : LoadingMode.ONE_TIME;
	}

	/**
	 * Asks for a refresh of the projects list. If the refreshing mode is set to {@link RefreshMode#ALWAYS}, the list
	 * will be refreshed immediately. Otherwise, the list will be refreshed depending on the selected refreshing mode.
	 * 
	 * @param viewOwnOrManage
	 *          If the projects that the user own or manage must be included in the list (no matter of their
	 *          organizational units).
	 * @param orgUnitsIds
	 *          The list of ids of the organizational units for which the projects will be retrieved. The projects of each
	 *          the sub-organizational units are retrieved automatically.
	 */
	public void refresh(final boolean viewOwnOrManage, final Integer... orgUnitsIds) {

		// Updates toolbar.
		final boolean refreshEnabled = refreshMode == RefreshMode.MANUAL || refreshMode == RefreshMode.ON_FIRST_TIME;
		final boolean exportEnabled = ProfileUtils.isGranted(auth(), GlobalPermissionEnum.GLOBAL_EXPORT);
		view.updateToolbar(refreshEnabled, exportEnabled);

		// Updates accessibility.
		view.updateAccessibilityState(ProfileUtils.isGranted(auth(), GlobalPermissionEnum.VIEW_PROJECT));

		final List<Integer> orgUnitsIdsAsList = orgUnitsIds != null ? Arrays.asList(orgUnitsIds) : null;

		this.orgUnitsIds.clear();
		this.orgUnitsIds.addAll(orgUnitsIdsAsList);

		// Builds the next refresh command.
		command = new GetProjects(orgUnitsIdsAsList, null);
		command.setViewOwnOrManage(viewOwnOrManage);

		// If the mode is automatic, the list is refreshed immediately.
		if (refreshMode == RefreshMode.ALWAYS || (refreshMode == RefreshMode.ON_FIRST_TIME && !loaded)) {
			refreshProjectGrid(command);
			loaded = true;
		}
	}

	// ---------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------

	/**
	 * Sets-up the listeners.
	 */
	private void addListeners() {

		// --
		// Grid 'time' filter activation.
		// --

		view.getProjectsPanel().addListener(Events.AfterLayout, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				view.getGridFilters().getFilter(View.TIME_COLUMN).setActive(true, false);
			}
		});

		// --
		// Updates the projects grid heading when the store is filtered.
		// --

		view.getStore().addListener(Store.Filter, new Listener<StoreEvent<ProjectDTO>>() {

			@Override
			public void handleEvent(StoreEvent<ProjectDTO> be) {
				view.getProjectsPanel().setHeadingText(I18N.CONSTANTS.projects() + " (" + view.getStore().getChildCount() + ')');
			}
		});

		// --
		// Adds actions on filter by project model type.
		// --

		view.getProjectModelTypeField().addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				currentModelType = view.getProjectModelTypeField().getValue();
				applyProjectFilters();
			}
		});

		// --
		// Delete project event.
		// --

		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.PROJECT_CREATE)) {
					// On project creation event.
					final ProjectDTO createdProject = event.getParam(1);
					view.getStore().clearFilters();
					view.getStore().add(createdProject, false);
					view.getStore().applyFilters(null);

				} else if (event.concern(UpdateEvent.PROJECT_DELETE)) {
					// On project delete event.
					// Will force projects list to reload on next refresh.
					loaded = false;

				} else if (event.concern(UpdateEvent.PROJECT_DRAFT_DELETE)) {
					// On 'draft' project delete event.
					final ProjectDTO deletedDraftProject = event.getParam(0);
					onDraftProjectDeleted(deletedDraftProject);
					
				} else if(event.concern(UpdateEvent.USER_LOGGED_IN)) {
					// Force dashboard to load projects for the new user.
					loaded = false;
				}
			}
		}));

		// --
		// Export action handler.
		// --

		view.getExportButton().addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				eventBus.navigate(Page.PROJECT_EXPORTS);
			}

		});
	}

	/**
	 * Sets-up the grid filters.
	 */
	private void addFilters() {

		// --
		// The filter by model type.
		// --

		view.getStore().addFilter(new StoreFilter<ProjectDTO>() {

			@Override
			public boolean select(Store<ProjectDTO> store, ProjectDTO parent, ProjectDTO item, String property) {

				boolean selected = false;

				// Root item.
				if (item.getParent() == null) {
					// A root item is filtered if its type doesn't match the current type.
					selected = item.getVisibility(auth().getOrganizationId()) == currentModelType;
				}
				// Child item
				else {
					// A child item is filtered if its parent is filtered.
					selected = ((ProjectDTO) item.getParent()).getVisibility(auth().getOrganizationId()) == currentModelType;
				}

				return selected;
			}
		});

		// --
		// Refresh button selection listener.
		// --

		view.getRefreshButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// Explicit refresh.
				refreshProjectGrid(command);
			}
		});

		// --
		// Filters aren't used for the moment.
		// --

		view.getFilterButton().setVisible(false);
	}

	/**
	 * Applies the store filters.
	 */
	private void applyProjectFilters() {
		view.getStore().applyFilters(null);
	}

	/**
	 * Refreshes the projects grid with the current parameters.
	 * 
	 * @param cmd
	 *          The {@link GetProjects} command to execute.
	 */
	private void refreshProjectGrid(final GetProjects cmd) {

		// Checks that the user can view projects.
		if (!ProfileUtils.isGranted(auth(), GlobalPermissionEnum.VIEW_PROJECT)) {
			return;
		}

		if (cmd == null) {
			return;
		}
		
		// Reload filter labels for category filter list store.
		reloadCategoryListFilterStore();

		
		if (loadingMode == LoadingMode.ONE_TIME) {
			// Loads projects in one time (one request).
			loadProjectsInOneTime(cmd);

		} else if (loadingMode == LoadingMode.CHUNK) {
			// Loads projects in multiple 'chunks' (multiple requests) to render a progression.
			loadProjectsInMultipleChunks(cmd);
		}
	}

	/**
	 * Loads projects using one request.<br>
	 * To load the projects with a progress bar, see {@link #loadProjectsInMultipleChunks(GetProjects)}.
	 * 
	 * @param cmd
	 *          The command retrieving projects.
	 */
	private void loadProjectsInOneTime(final GetProjects cmd) {

		cmd.setMappingMode(ProjectDTO.Mode._USE_PROJECT_MAPPER);

		dispatch.execute(cmd, new CommandResultHandler<ListResult<ProjectDTO>>() {

			@Override
			public void onCommandSuccess(final ListResult<ProjectDTO> result) {

				view.getStore().removeAll();
				view.getStore().clearFilters();

				if (result != null) {
					addProjectToTreeGrid(result.getList());
				}

				applyProjectFilters();
				view.updateRefreshingDate(new Date());
			}

		}, new LoadingMask(view.getProjectsPanel()));
	}

	/**
	 * Loads projects using multiple <em>chunks</em> (see {@link GetProjectsWorker}).<br>
	 * This mechanism allows the application to render a progress bar.
	 * 
	 * @param cmd
	 *          The command retrieving projects.
	 */
	private void loadProjectsInMultipleChunks(final GetProjects cmd) {

		// --
		// Builds a new chunks worker.
		// --

		final GetProjectsWorker worker = new GetProjectsWorker(dispatch, cmd, view.getProjectsPanel(), ClientConfiguration.getChunkSize());
		worker.addWorkerListener(new GetProjectsWorker.WorkerListener() {
			
			private int chunk = 0;

			@Override
			public void serverError(final Throwable error) {

				if (Log.isErrorEnabled()) {
					Log.error("Error while getting projects by chunks.", error);
				}

				applyProjectFilters();
				view.updateRefreshingDate(new Date());
				N10N.warn(I18N.CONSTANTS.error(), I18N.CONSTANTS.refreshProjectListError());
			}

			@Override
			public void chunkRetrieved(final List<ProjectDTO> projects) {
				addProjectToTreeGrid(projects);
			}

			@Override
			public void ended() {
				applyProjectFilters();
				view.updateRefreshingDate(new Date());
			}
		});

		// --
		// Runs the worker.
		// --

		view.getStore().removeAll();
		view.getStore().clearFilters();
		

		worker.run();
	}

	private void addProjectToTreeGrid(final List<ProjectDTO> projects) {

		if (ClientUtils.isEmpty(projects)) {
			return;
		}

		for (final ProjectDTO project : projects) {
			project.setTreeRendering(true);

			if (ClientUtils.isNotEmpty(project.getChildrenProjects())) {
				for (final ProjectDTO child : project.getChildrenProjects()) {
					child.setTreeRendering(true);
				}
			}
		}

		view.getStore().add(projects, true);
	}

	/**
	 * Fetches categories in active user's organization and fills {@link ListFilter} store of a category column.
	 */
	private void reloadCategoryListFilterStore() {

		dispatch.execute(new GetCategories(), new CommandResultHandler<ListResult<CategoryTypeDTO>>() {

			@SuppressWarnings("unchecked")
			@Override
			public void onCommandSuccess(final ListResult<CategoryTypeDTO> result) {

				final ListFilter categoryListFilter = (ListFilter) view.getGridFilters().getFilter(ProjectDTO.CATEGORY_ELEMENTS);
				final ListStore<ModelData> filterStore = categoryListFilter.getStore();
				final List<CategoryTypeDTO> categories = result.getList();

				for (final CategoryTypeDTO category : categories) {

					final List<CategoryElementDTO> categoryElements = category.getCategoryElementsDTO();

					for (final CategoryElementDTO element : categoryElements) {

						final String filterLabel = element.getLabel() + " (" + category.getLabel() + ")";

						boolean exist = false;
						for (final ModelData model : filterStore.getModels()) {
							if (model.get(View.CATEGORY_FILTER).equals(filterLabel)) {
								exist = true;
								break;
							}
						}

						if (!exist) {
							final ModelData model = new BaseModelData();
							model.set(View.CATEGORY_FILTER, filterLabel);

							filterStore.add(model);
						}
					}
				}
			}
		});
	}

	/**
	 * Method executed when a <b>draft</b> project is deleted.
	 * 
	 * @param deletedDraftProject
	 *          The deleted <b>draft</b> project.
	 */
	private void onDraftProjectDeleted(final ProjectDTO deletedDraftProject) {

		final TreeStore<ProjectDTO> store = view.getStore();
		store.clearFilters();

		final Integer projectId = deletedDraftProject.getId();

		// Inspect root elements.
		final List<ProjectDTO> parents = store.getRootItems();
		for (final ProjectDTO parent : parents) {

			final List<ProjectDTO> childrens = parent.getChildrenProjects();

			for (final ProjectDTO child : childrens) {
				// Deletes children if equals to project.
				if (child.getId().equals(projectId) || child.getId().equals(projectId)) {
					store.remove(parent, child);
				}
			}
		}

		// Deletes the parent that corresponds to project.
		if (store.findModel(ProjectDTO.ID, projectId) != null) {
			// Deletes children links.
			store.removeAll(store.findModel(ProjectDTO.ID, projectId));
			store.remove(store.findModel(ProjectDTO.ID, projectId));

		} else {
			// Deletes children links.
			store.removeAll(store.findModel(ProjectDTO.ID, projectId));
			store.remove(store.findModel(ProjectDTO.ID, projectId));
		}

		store.applyFilters(null);

		// Show notification.
		N10N.infoNotif(I18N.CONSTANTS.deleteTestProjectHeader(), I18N.CONSTANTS.deleteTestProjectSucceededDetails());
	}

}
