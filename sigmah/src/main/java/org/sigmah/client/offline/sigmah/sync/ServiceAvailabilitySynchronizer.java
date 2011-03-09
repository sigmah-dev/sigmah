/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah.sync;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.dispatch.remote.DirectDispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.Ping;
import org.sigmah.shared.command.result.VoidResult;

/**
 * Checks the availability of the server before trying to reconnect.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ServiceAvailabilitySynchronizer extends AbstractSynchronizer {

    private DirectDispatcher dispatcher;

    public ServiceAvailabilitySynchronizer(DirectDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void synchronizeLocalDatabase() {
        fireOnStart();
        fireOnComplete();
    }

    @Override
    public void updateDistantDatabase() {
        fireOnStart();
        fireOnTaskChange(I18N.CONSTANTS.synchronizerAvailabilityUpload_0());
        
        dispatcher.execute(new Ping(), null, new AsyncCallback<VoidResult>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.debug("Server communication failure.");
                fireOnFailure(true, I18N.CONSTANTS.synchronizerAvailabilityUpload_0_failed());
            }

            @Override
            public void onSuccess(VoidResult result) {
                fireOnComplete();
            }
            
        });
    }

}
