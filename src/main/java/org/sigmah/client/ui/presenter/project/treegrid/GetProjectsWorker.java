package org.sigmah.client.ui.presenter.project.treegrid;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.dispatch.monitor.ProgressMask;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.GetProjectsFromId;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Component;

/**
 * Represents a worker which get projects chunk by chunk.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
final class GetProjectsWorker {

	/**
	 * Receives the worker events.
	 * 
	 * @author tmi
	 */
	public static interface WorkerListener {

		/**
		 * Method called if a server error occurs.
		 * 
		 * @param error
		 *          The error.
		 */
		void serverError(Throwable error);

		/**
		 * Method called when a chunk is retrieved.
		 * 
		 * @param projects
		 *          The chunk.
		 */
		void chunkRetrieved(List<ProjectDTO> projects);

		/**
		 * Method called after the last chunk has been retrieved.
		 */
		void ended();

	}

	/**
	 * The dispatcher.
	 */
	private final DispatchAsync dispatch;

	/**
	 * The {@link GetProjects} command to execute.
	 */
	private final GetProjects cmd;

	/**
	 * The component to mask while the worker runs.
	 */
	private final Component component;

	/**
	 * The size of each chunk.
	 */
	private final int chunkSize;

	/**
	 * Listeners.
	 */
	private final ArrayList<WorkerListener> listeners;

	/**
	 * The list of projects ids to retrieve by chunks.
	 */
	private List<Integer> projectsIds;

	/**
	 * The number of projects ids to retrieve by chunks.
	 */
	private int projectsIdsSize;

	/**
	 * The async monitor.
	 */
	private ProgressMask monitor;

	/**
	 * Builds a new worker with a default chuck size to 1.
	 * 
	 * @param dispatch
	 *          The dispatcher.
	 * @param cmd
	 *          The {@link GetProjects} command to execute.
	 */
	public GetProjectsWorker(DispatchAsync dispatch, GetProjects cmd, Component component) {
		this(dispatch, cmd, component, 1);
	}

	/**
	 * Builds a new worker.
	 * 
	 * @param dispatch
	 *          The dispatcher.
	 * @param cmd
	 *          The {@link GetProjects} command to execute.
	 * @param component
	 *          The component to mask while the worker runs.
	 * @param chunkSize
	 *          The size of each chunk.
	 */
	public GetProjectsWorker(final DispatchAsync dispatch, final GetProjects cmd, final Component component, final int chunkSize) {
		assert dispatch != null;
		assert cmd != null;
		assert component != null;
		this.dispatch = dispatch;
		this.cmd = cmd;
		this.component = component;
		this.chunkSize = chunkSize <= 0 ? 1 : chunkSize;
		this.listeners = new ArrayList<WorkerListener>();
	}

	/**
	 * Adds a listener to this worker.
	 * 
	 * @param listener
	 *          The new listener.
	 */
	public void addWorkerListener(final WorkerListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Runs the worker.
	 */
	public void run() {

		monitor = new ProgressMask(component);

		// First call to get the list of projects ids.
		cmd.setMappingMode(ProjectDTO.Mode.BASE);

		dispatch.execute(cmd, new CommandResultHandler<ListResult<ProjectDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				if (Log.isErrorEnabled()) {
					Log.error("[GetProjects command] Error while getting projects.", e);
				}
				monitor.initCounter(0);
				fireServerError(e);
			}

			@Override
			public void onCommandSuccess(final ListResult<ProjectDTO> result) {

				// List of the projects ids.
				final List<Integer> ids = new ArrayList<Integer>();
				for (final ProjectDTO project : result.getList()) {
					ids.add(project.getId());
				}

				// Retrieves projects by chunks.
				if (ids != null && !ids.isEmpty()) {
					projectsIds = ids;
					projectsIdsSize = ids.size();
					monitor.initCounter(projectsIdsSize);
					chunk();

				} else {
					monitor.initCounter(0);
				}
			}
		}, new LoadingMask(component));
	}

	private void chunk() {

		// No more project to get.
		if (projectsIds.isEmpty()) {
			fireEnded();
			return;
		}

		// Store the next ids to retrieve.
		final ArrayList<Integer> nextWaveIds = new ArrayList<Integer>(chunkSize);
		final int count = projectsIds.size() >= chunkSize ? chunkSize : projectsIds.size();
		for (int i = 0; i < count; i++) {
			nextWaveIds.add(projectsIds.remove(0));
		}

		// Retrieves these projects.
		dispatch.execute(new GetProjectsFromId(nextWaveIds, ProjectDTO.Mode._USE_PROJECT_MAPPER), new CommandResultHandler<ListResult<ProjectDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				if (Log.isErrorEnabled()) {
					Log.error("[GetProjectsFromId command] Error while getting projects.", e);
				}
				monitor.increment(projectsIdsSize);
				fireServerError(e);
			}

			@Override
			public void onCommandSuccess(final ListResult<ProjectDTO> result) {

				// Updates the monitor.
				monitor.increment(count);

				// Fires event.
				fireChunkRetrieved(result.getList());

				// Next chunk.
				chunk();
			}
		}, monitor);
	}

	/**
	 * Method called if a server error occurs.
	 * 
	 * @param error
	 *          The error.
	 */
	protected void fireServerError(final Throwable error) {
		for (final WorkerListener listener : listeners) {
			listener.serverError(error);
		}
	}

	/**
	 * Method called when a chunk is retrieved.
	 * 
	 * @param projects
	 *          The chunk.
	 */
	protected void fireChunkRetrieved(final List<ProjectDTO> projects) {
		for (final WorkerListener listener : listeners) {
			listener.chunkRetrieved(projects);
		}
	}

	/**
	 * Method called after the last chunk has been retrieved.
	 */
	protected void fireEnded() {
		for (final WorkerListener listener : listeners) {
			listener.ended();
		}
	}
}
