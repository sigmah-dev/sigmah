/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah.sync;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.localserver.LocalServer;
import com.google.gwt.gears.client.localserver.ResourceStore;
import com.google.gwt.gears.client.localserver.ResourceStoreUrlCaptureHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.offline.sigmah.OnlineMode;
import org.sigmah.client.offline.sigmah.dao.OrganizationDAO;
import org.sigmah.shared.command.GetOrganization;
import org.sigmah.shared.dto.OrganizationDTO;
import org.sigmah.shared.dto.value.FileUploadUtils;

/**
 * Saves informations about the organization of the current user.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OrganizationSynchronizer extends AbstractSynchronizer {

    private Dispatcher dispatcher;
    private Authentication authentication;

    public OrganizationSynchronizer(Dispatcher dispatcher, Authentication authentication) {
        this.dispatcher = dispatcher;
        this.authentication = authentication;
    }

    @Override
    public void synchronizeLocalDatabase() {
        fireOnStart();
        fireOnTaskChange(I18N.CONSTANTS.synchronizerOrganizationDownload_0());

        final Factory factory = Factory.getInstance();

        if(factory != null) {

            dispatcher.execute(new GetOrganization(authentication.getOrganizationId()), null, new AsyncCallback<OrganizationDTO>() {

                @Override
                public void onFailure(Throwable caught) {
                    fireOnFailure(false, I18N.CONSTANTS.synchronizerOrganizationDownload_0_failed()+caught.getMessage());
                }

                @Override
                public void onSuccess(OrganizationDTO result) {
                    final Database database = factory.createDatabase();
                    database.open(OnlineMode.LOCAL_DATABASE_NAME);
                    
                    try {
                        fireOnUpdate(0.1);
                        fireOnTaskChange(I18N.CONSTANTS.synchronizerOrganizationDownload_1());
                        OrganizationDAO.createTablesIfNotExists(database);

                        fireOnUpdate(0.2);
                        fireOnTaskChange(I18N.CONSTANTS.synchronizerOrganizationDownload_2());
                        OrganizationDAO.insertOrReplaceOrganization(result, database);

                        final LocalServer localServer = factory.createLocalServer();
                        final ResourceStore store = localServer.createStore(OnlineMode.LOCAL_DATABASE_NAME);

                        fireOnUpdate(0.8);
                        fireOnTaskChange(I18N.CONSTANTS.synchronizerOrganizationDownload_3());
                        store.capture(new ResourceStoreUrlCaptureHandler() {

                            @Override
                            public void onCapture(ResourceStoreUrlCaptureEvent event) {
                                fireOnComplete();
                            }
                        }, GWT.getModuleBaseURL() + "image-provider?" + FileUploadUtils.IMAGE_URL + "=" + result.getLogo());

                    } catch (DatabaseException ex) {
                        Log.debug("Error while writing the organization dto to the local database.", ex);
                        fireOnFailure(false, I18N.CONSTANTS.synchronizerOrganizationDownload_0_failed()+ex.getMessage());

                    } finally {
                        try {
                            database.close();
                        } catch (DatabaseException ex) {
                            Log.debug("Database closing error.", ex);
                            fireOnFailure(false, I18N.CONSTANTS.synchronizerOrganizationDownload_0_failed()+ex.getMessage());
                        }
                    }
                }
            });
            
        } else
            fireOnFailure(false, I18N.CONSTANTS.synchronizerOrganizationDownload_0_failed()+"Google Gears isn't available.");
    }

    @Override
    public void updateDistantDatabase() {
        fireOnStart();
        fireOnTaskChange(I18N.CONSTANTS.synchronizerOrganizationUpload_0());

        final Factory factory = Factory.getInstance();

        if(factory != null) {
            final Database database = factory.createDatabase();
            database.open(OnlineMode.LOCAL_DATABASE_NAME);

            try {
                OrganizationDAO.truncateTables(database);

                fireOnComplete();

            } catch (DatabaseException ex) {
                Log.debug("Error while removing the organization dto from the local database.", ex);
                fireOnFailure(false, I18N.CONSTANTS.synchronizerOrganizationUpload_0_failed()+ex.getMessage());

            } finally {
                try {
                    database.close();
                } catch (DatabaseException ex) {
                    Log.debug("Database closing error.", ex);
                    fireOnFailure(false, I18N.CONSTANTS.synchronizerOrganizationUpload_0_failed()+ex.getMessage());
                }
            }

        } else
            fireOnFailure(false, I18N.CONSTANTS.synchronizerOrganizationUpload_0_failed()+"Google Gears isn't available.");
    }

}
