/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah.sync;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.GearsException;
import com.google.gwt.gears.client.localserver.LocalServer;
import com.google.gwt.gears.client.localserver.ManagedResourceStore;
import com.google.gwt.gears.client.localserver.ResourceStore;
import com.google.gwt.gears.client.localserver.ResourceStoreUrlCaptureHandler;
import com.google.gwt.gears.client.localserver.ResourceStoreUrlCaptureHandler.ResourceStoreUrlCaptureEvent;
import com.google.gwt.gears.offline.client.Offline;
import com.google.gwt.user.client.Timer;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.offline.sigmah.OnlineMode;

/**
 * Downloads locally the files needed to run Sigmah.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ApplicationSynchronizer extends AbstractSynchronizer {

    @Override
    public void synchronizeLocalDatabase() {
        fireOnStart();
        fireOnTaskChange(I18N.CONSTANTS.synchronizerApplicationDownload_0());

        final Factory factory = Factory.getInstance();

        if(factory != null) {
            
            final LocalServer localServer = factory.createLocalServer();
            final ResourceStore store = localServer.createStore(OnlineMode.LOCAL_DATABASE_NAME);

            store.capture(new ResourceStoreUrlCaptureHandler() {

                    @Override
                    public void onCapture(ResourceStoreUrlCaptureEvent event) {

                        try {
                            final ManagedResourceStore managedResourceStore = Offline.getManagedResourceStore();

                            new Timer() {

                                @Override
                                public void run() {
                                    switch (managedResourceStore.getUpdateStatus()) {
                                        case ManagedResourceStore.UPDATE_OK:
                                            managedResourceStore.setEnabled(true);
                                            fireOnComplete();
                                            break;
                                        case ManagedResourceStore.UPDATE_CHECKING:
                                        case ManagedResourceStore.UPDATE_DOWNLOADING:
                                            fireOnUpdate(0.5);
                                            schedule(500);
                                            break;
                                        case ManagedResourceStore.UPDATE_FAILED:
                                            Log.debug("Google Gears update failed: "+managedResourceStore.getLastErrorMessage());
                                            fireOnFailure(true, I18N.CONSTANTS.synchronizerApplicationDownload_0_failed()+managedResourceStore.getLastErrorMessage());
                                            break;
                                    }
                                }
                            }.schedule(500);

                        } catch (GearsException ex) {
                            Log.debug("Google Gears error while trying to cache Sigmah resources.", ex);
                            fireOnFailure(true, I18N.CONSTANTS.synchronizerApplicationDownload_0_failed()+ex.getMessage());
                        }

                    }
                }, "./");
                    
        }
    }

    @Override
    public void updateDistantDatabase() {
        // Disabling the local store.
        fireOnStart();
        fireOnTaskChange(I18N.CONSTANTS.synchronizerApplicationUpload_0());

        try {
            final Factory factory = Factory.getInstance();

            if(factory != null) {

                final ManagedResourceStore managedResourceStore = Offline.getManagedResourceStore();
                managedResourceStore.setEnabled(false);

                final LocalServer localServer = factory.createLocalServer();
                final ResourceStore store = localServer.createStore(OnlineMode.LOCAL_DATABASE_NAME);
                store.remove("./");
            }

            fireOnComplete();

        } catch (GearsException ex) {
            Log.debug("Google Gears error while trying to remove Sigmah from the cache.", ex);
            fireOnFailure(false, I18N.CONSTANTS.synchronizerApplicationUpload_0_failed()+ex.getMessage());
        }

    }

}
