package org.sigmah.client.ui.presenter.project;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.page.event.PageRequestEvent;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasSubPresenter.SubPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.dto.ProjectDTO;

import com.allen_sauer.gwt.log.client.Log;

/**
 * Project's presenters abstract code.<br/>
 * This super-class assumes that all inherited classes are a sub-presenter of {@link ProjectPresenter}. Can be changed
 * by overriding the {@link #getParentPresenter()} method.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public abstract class AbstractProjectPresenter<V extends AbstractProjectPresenter.View> extends AbstractPagePresenter<V> implements
																																																												SubPresenter<ProjectPresenter> {

	/**
	 * Description of the view managed by this presenter.
	 */
	public static interface View extends ViewInterface {

		// No methods here yet.

	}

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	public AbstractProjectPresenter(final V view, final Injector injector) {
		super(view, injector);
	}
	
	/**
	 * Defines the loading mode of projects.
	 */
	private boolean useCache;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void executeOnPageRequest(final PageRequestEvent event, final Page page) {

		final PageRequest request = event.getRequest();
		final Integer projectId = request.getParameterInteger(RequestParameter.ID);
		final Integer amendmentId = request.getParameterInteger(RequestParameter.VERSION);

		final ProjectDTO currentProject = getParentPresenter().getCurrentProject();

		if (useCache && currentProject != null && projectId.equals(currentProject.getId())) {
			if (Log.isDebugEnabled()) {
				Log.debug("Project #" + projectId + " has already been loaded. No need to load it again.");
			}
			onProjectLoaded(currentProject, event, page);
			
		} else {
			if (Log.isDebugEnabled()) {
				Log.debug("Project #" + projectId + " is not the current loaded project. Loading it from server.");
			}
			// Retrieves project (full loading executing only once).
			dispatch.execute(new GetProject(projectId, amendmentId, null), new CommandResultHandler<ProjectDTO>() {

				@Override
				protected void onCommandSuccess(final ProjectDTO project) {
					onProjectLoaded(project, event, page);
				}
			});
		}
	}

	/**
	 * Method executed once project has been loaded.
	 * 
	 * @param loadedProject
	 *          The loaded project.
	 * @param event
	 *          The page request event.
	 * @param page
	 *          The accessed page.
	 */
	private void onProjectLoaded(final ProjectDTO loadedProject, final PageRequestEvent event, final Page page) {

		// Stores project instance into local attribute AND parent presenter.
		getParentPresenter().setCurrentProject(loadedProject);
		getParentPresenter().setCurrentDisplayedPhase(loadedProject.getCurrentPhase());

		final PageRequest request = event.getRequest();

		// Updates the tab title.
		eventBus.updateZoneRequest(Zone.MENU_BANNER.requestWith(RequestParameter.REQUEST, request).addData(RequestParameter.HEADER, loadedProject.getName()));

		// Executes child page 'onPageRequest()'.
		afterOnPageRequest(event, page);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectPresenter getParentPresenter() {
		return injector.getProjectPresenter();
	}

	/**
	 * Returns the retrieved {@link ProjectDTO}.
	 * 
	 * @return The retrieved {@link ProjectDTO}.
	 */
	protected final ProjectDTO getProject() {
		return getParentPresenter().getCurrentProject();
	}

	/**
	 * Changes the way the project presenter loads projects.
	 * If <code>true</code>, a project already opened will not be loaded again
	 * until a different project has been selected.
	 * If <code>false</code>, projects will be reloaded every time.
	 * @param useCache <code>true</code> to use cache, <code>false</code> to reload the projects every time.
	 */
	protected void setUseCache(boolean useCache) {
		this.useCache = useCache;
	}

}
