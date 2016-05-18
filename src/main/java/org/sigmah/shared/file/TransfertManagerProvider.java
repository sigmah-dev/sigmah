package org.sigmah.shared.file;

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

import com.google.gwt.core.client.GWT;
import com.google.inject.Inject;
import com.google.inject.Provider;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.event.EventBus;
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
 *
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
     * - Google Chrome (crash for files &gt; 1 MB)
     * - Internet Explorer 6 up to IE 9
     * 
     * Mozilla Firefox has the best support for offline downloads since it also
     * implements the "download" attribute.
     */
	public static boolean html5EngineActive = GWT.isProdMode() && (ClientUtils.isFF() || ClientUtils.isSafari() || ClientUtils.isIE10() || ClientUtils.isIE11());

    private final TransfertManager transfertManager;

    @Inject
    public TransfertManagerProvider(DispatchAsync dispatchAsync, AuthenticationProvider authenticationProvider, PageManager pageManager, EventBus eventBus, FileDataAsyncDAO fileDataAsyncDAO, TransfertAsyncDAO transfertAsyncDAO) {
      DirectTransfertManager directTransfertManager = new DirectTransfertManager(authenticationProvider, pageManager, eventBus);
      if (html5EngineActive && IndexedDB.isSupported() && FileReader.isSupported()) {
        this.transfertManager = new Html5TransfertManager(dispatchAsync, fileDataAsyncDAO, transfertAsyncDAO, eventBus, directTransfertManager);
      } else {
        this.transfertManager = directTransfertManager;
      }
    }

    @Override
    public TransfertManager get() {
        return transfertManager;
    }
    
}
