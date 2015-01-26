/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah.sync;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.GearsException;
import com.google.gwt.gears.client.localserver.LocalServer;
import com.google.gwt.gears.client.localserver.ResourceStore;
import com.google.gwt.gears.client.localserver.ResourceStoreUrlCaptureHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.offline.sigmah.OnlineMode;

/**
 * Saves the current authentication.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class AuthTokenSynchronizer extends AbstractSynchronizer {

    @Override
    public void synchronizeLocalDatabase() {
        fireOnStart();
        fireOnTaskChange(I18N.CONSTANTS.synchronizerAuthTokenDownload_0());

        final Factory factory = Factory.getInstance();

        if(factory != null) {
            final LocalServer localServer = factory.createLocalServer();

            try {
                if (!localServer.canServeLocally("../SigmahAuthToken")) {
                    final ResourceStore store = localServer.createStore(OnlineMode.LOCAL_DATABASE_NAME);
                    
                    store.capture(new ResourceStoreUrlCaptureHandler() {

                        @Override
                        public void onCapture(ResourceStoreUrlCaptureEvent event) {
                            fireOnComplete();
                        }
                    }, "../SigmahAuthToken");
                }
                else
                    fireOnComplete();
                
            } catch (GearsException ex) {
                Log.debug("Google Gears error while trying to cache the AuthToken.", ex);
                fireOnFailure(true, I18N.CONSTANTS.synchronizerAuthTokenDownload_0_failed()+ex.getMessage());
            }
        }
    }

    @Override
    public void updateDistantDatabase() {
        fireOnStart();
        fireOnTaskChange(I18N.CONSTANTS.synchronizerAuthTokenUpload_0());

        final Factory factory = Factory.getInstance();

        if(factory != null) {
            final LocalServer localServer = factory.createLocalServer();

            try {
                if (localServer.canServeLocally("../SigmahAuthToken")) {
                    final ResourceStore store = localServer.createStore(OnlineMode.LOCAL_DATABASE_NAME);

                    store.remove("../SigmahAuthToken");
                }
                
                fireOnComplete();

            } catch (GearsException ex) {
                Log.debug("Google Gears error while trying to remove the AuthToken from cache.", ex);
                fireOnFailure(true, I18N.CONSTANTS.synchronizerAuthTokenUpload_0_failed()+ex.getMessage());
            }
        }
    }

}
