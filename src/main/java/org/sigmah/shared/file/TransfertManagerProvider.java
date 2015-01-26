package org.sigmah.shared.file;

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.page.PageManager;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.offline.dao.FileDataAsyncDAO;
import org.sigmah.offline.dao.TransfertAsyncDAO;
import org.sigmah.offline.fileapi.FileReader;
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.status.ConnectionStatus;

/**
 * Maintain and provides a TransfertManager singleton.
 * <p/>
 * The type of TransfertManager is selected based on the user's browser.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TransfertManagerProvider implements Provider<TransfertManager> {
    
    /**
     * Activates HTML5 engine if the current browser supports downloads from a 
     * DataURL.
     * 
     * As of november 2014, downloads from DataURL are unsupported for the 
     * following browsers:
     * - Google Chrome (crash for files > 1 MB)
     * - Internet Explorer 6 up to IE 9
     * 
     * Mozilla Firefox has the best support for offline downloads since it also
     * implements the "download" attribute.
     */
	public static boolean html5EngineActive = GWT.isProdMode() && (ClientUtils.isFF() || ClientUtils.isSafari() || ClientUtils.isIE10() || ClientUtils.isIE11());

    private final TransfertManager transfertManager;

    @Inject
    public TransfertManagerProvider(DispatchAsync dispatchAsync, AuthenticationProvider authenticationProvider, PageManager pageManager, ConnectionStatus connectionStatus, FileDataAsyncDAO fileDataAsyncDAO, TransfertAsyncDAO transfertAsyncDAO) {
        if(html5EngineActive && IndexedDB.isSupported() && FileReader.isSupported()) {
			this.transfertManager = new Html5TransfertManager(dispatchAsync, fileDataAsyncDAO, transfertAsyncDAO, connectionStatus);
		} else {
            this.transfertManager = new DirectTransfertManager(authenticationProvider, pageManager, connectionStatus);
		}
    }

    @Override
    public TransfertManager get() {
        return transfertManager;
    }
    
}
