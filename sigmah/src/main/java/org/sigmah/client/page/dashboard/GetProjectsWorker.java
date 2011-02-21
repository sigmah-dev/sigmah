package org.sigmah.client.page.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.ProgressingAsyncMonitor;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.GetProjectsFromId;
import org.sigmah.shared.command.result.ProjectListResult;
import org.sigmah.shared.dto.ProjectDTOLight;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Represents a worker which get projects chunk by chunk.
 * 
 * @author tmi
 */
public class GetProjectsWorker {

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
         *            The error.
         */
        public void serverError(Throwable error);

        /**
         * Method called when a chunk is retrieved.
         * 
         * @param projects
         *            The chunk.
         */
        public void chunkRetrieved(List<ProjectDTOLight> projects);

        /**
         * Method called after the last chunk has been retrieved.
         */
        public void ended();
    }

    /**
     * The dispatcher.
     */
    private final Dispatcher dispatcher;

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
     * The async monitor.
     */
    private ProgressingAsyncMonitor monitor;

    /**
     * Builds a new worker with a default chuck size to 1.
     * 
     * @param dispatcher
     *            The dispatcher.
     * @param cmd
     *            The {@link GetProjects} command to execute.
     */
    public GetProjectsWorker(Dispatcher dispatcher, GetProjects cmd, Component component) {
        this(dispatcher, cmd, component, 1);
    }

    /**
     * Builds a new worker.
     * 
     * @param dispatcher
     *            The dispatcher.
     * @param cmd
     *            The {@link GetProjects} command to execute.
     * @param component
     *            The component to mask while the worker runs.
     * @param chunkSize
     *            The size of each chunk.
     */
    public GetProjectsWorker(Dispatcher dispatcher, GetProjects cmd, Component component, int chunkSize) {
        assert dispatcher != null;
        assert cmd != null;
        assert component != null;
        this.dispatcher = dispatcher;
        this.cmd = cmd;
        this.component = component;
        this.chunkSize = chunkSize <= 0 ? 1 : chunkSize;
        this.listeners = new ArrayList<WorkerListener>();
    }

    /**
     * Adds a listener to this worker.
     * 
     * @param listener
     *            The new listener.
     */
    public void addWorkerListener(WorkerListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Runs the worker.
     */
    public void run() {

        monitor = new ProgressingAsyncMonitor(component);

        // First call to get the list of projects ids.
        cmd.setReturnType(GetProjects.ProjectResultType.ID);
        dispatcher.execute(cmd, monitor, new AsyncCallback<ProjectListResult>() {

            @Override
            public void onFailure(Throwable e) {
                Log.error("[GetProjects command] Error while getting projects.", e);
                monitor.initCounter(0);
                fireServerError(e);
            }

            @Override
            public void onSuccess(ProjectListResult result) {

                // List of the projects ids.
                final List<Integer> list = result.getListProjectsIds();

                // Retrieves projects by chunks.
                if (list != null && !list.isEmpty()) {
                    projectsIds = list;
                    monitor.initCounter(projectsIds.size());
                    chunk();
                } else {
                    monitor.initCounter(0);
                }
            }
        });
    }

    private void chunk() {

        // No more project to get.
        if (projectsIds.isEmpty()) {
            fireEnded();
            return;
        }

        // Store the next ids to retreive.
        final ArrayList<Integer> nextWaveIds = new ArrayList<Integer>(chunkSize);
        final int count = projectsIds.size() >= chunkSize ? chunkSize : projectsIds.size();
        for (int i = 0; i < count; i++) {
            nextWaveIds.add(projectsIds.remove(0));
        }

        // Retrieves these projects.
        dispatcher.execute(new GetProjectsFromId(nextWaveIds), null, new AsyncCallback<ProjectListResult>() {

            @Override
            public void onFailure(Throwable e) {
                Log.error("[GetProjectsFromId command] Error while getting projects.", e);
                monitor.increment(Integer.MAX_VALUE);
                fireServerError(e);
            }

            @Override
            public void onSuccess(ProjectListResult result) {

                // Updates the monitor.
                monitor.increment(count);

                // Fires event.
                fireChunkRetrieved(result.getListProjectsLightDTO());

                // Next chunk.
                chunk();
            }
        });
    }

    /**
     * Method called if a server error occurs.
     * 
     * @param error
     *            The error.
     */
    protected void fireServerError(Throwable error) {
        for (final WorkerListener listener : listeners) {
            listener.serverError(error);
        }
    }

    /**
     * Method called when a chunk is retrieved.
     * 
     * @param projects
     *            The chunk.
     */
    protected void fireChunkRetrieved(List<ProjectDTOLight> projects) {
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
