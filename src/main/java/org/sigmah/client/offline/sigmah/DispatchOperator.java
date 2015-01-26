/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.dispatch.remote.DirectDispatcher;
import org.sigmah.client.dispatch.remote.RemoteDispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.offline.command.LocalDispatcher;
import org.sigmah.client.offline.sigmah.sync.ApplicationSynchronizer;
import org.sigmah.client.offline.sigmah.sync.AuthTokenSynchronizer;
import org.sigmah.client.offline.sigmah.sync.OrganizationSynchronizer;
import org.sigmah.client.offline.sigmah.sync.ServiceAvailabilitySynchronizer;
import org.sigmah.client.offline.sigmah.sync.Synchronizer;
import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.result.CommandResult;

/**
 * Select where to send request depending on the current connection mode (online or offline).
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class DispatchOperator implements Dispatcher {

    private final RemoteDispatcher remoteDispatcher;
    private final LocalDispatcher localDispatcher;

    private final List<Synchronizer> synchronizers;

    private final OnlineMode onlineMode;

    @Inject
    public DispatchOperator(
            RemoteDispatcher remoteDispatcher,
            LocalDispatcher localDispatcher,
            DirectDispatcher directDispatcher,
            OnlineMode onlineMode,
            Authentication authentication) {
        this.remoteDispatcher = remoteDispatcher;
        this.localDispatcher = localDispatcher;

        this.onlineMode = onlineMode;

        // Preparing the synchronizers
        final ArrayList<Synchronizer> list = new ArrayList<Synchronizer>();
        list.add(new ServiceAvailabilitySynchronizer(directDispatcher));
        list.add(new ApplicationSynchronizer());
        list.add(new AuthTokenSynchronizer());
        list.add(new OrganizationSynchronizer(remoteDispatcher, authentication));

        synchronizers = list;
    }

    @Override
    public <T extends CommandResult> void execute(Command<T> command, AsyncMonitor monitor, AsyncCallback<T> callback) {
        if(onlineMode.isOnline())
            remoteDispatcher.execute(command, monitor, callback);

        else if(localDispatcher.canExecute(command))
            localDispatcher.execute(command, monitor, callback);

        else {
            MessageBox.alert(I18N.CONSTANTS.sigmahOfflineUnavailable(), I18N.MESSAGES.sigmahOfflineUnavailableCommand(command.getClass().getName()), null);
            callback.onFailure(new UnavailableCommandException("No handler is registered for this command."));
        }
    }


    /**
     * Start the process to change the online mode.
     */
    public void setOnlineMode(final boolean toOnlineMode, final Synchronizer.Listener callback) {

        if(synchronizers.size() > 0) {
            // Calling the synchronizers to prepare the switch to the offline mode.
            final double step = 1.0 / synchronizers.size();

            final Synchronizer.Listener listener = new Synchronizer.Listener() {
                int index = 0;
                double currentProgress = 0.0;

                @Override
                public void onUpdate(double progress) {
                    callback.onUpdate(currentProgress + progress*step);
                }

                @Override
                public void onComplete() {
                    nextSynchronizer();
                }

                @Override
                public void onFailure(boolean critical, String reason) {
                    if(critical) {
                        synchronizers.get(index).removeListener(this);
                        callback.onFailure(critical, reason);

                    } else {
                        callback.onFailure(false, reason);

                        nextSynchronizer();
                    }
                }

                private void nextSynchronizer() {
                    synchronizers.get(index).removeListener(this);
                    index++;

                    currentProgress += step;

                    if(index < synchronizers.size()) {
                        // Launching the next synchronizer
                        synchronizers.get(index).addListener(this);

                        if(toOnlineMode)
                            synchronizers.get(index).updateDistantDatabase();
                        else
                            synchronizers.get(index).synchronizeLocalDatabase();

                    } else {
                        // Fin
                        callback.onTaskChange(I18N.CONSTANTS.synchronizerFinishing());
                        callback.onUpdate(currentProgress);
                        onlineMode.setOnline(toOnlineMode);
                        callback.onComplete();
                    }
                }

                @Override
                public void onStart() {
                    callback.onUpdate(currentProgress);
                }

                @Override
                public void onTaskChange(String taskName) {
                    callback.onTaskChange(taskName);
                }
            };

            callback.onStart();
            synchronizers.get(0).addListener(listener);

            if(toOnlineMode)
                synchronizers.get(0).updateDistantDatabase();
            else
                synchronizers.get(0).synchronizeLocalDatabase();

        } else {
            onlineMode.setOnline(false);
            callback.onComplete();
        }
    }
}
