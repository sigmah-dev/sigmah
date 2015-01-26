/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry.editor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.NullAsyncMonitor;
import org.sigmah.client.map.MapApiLoader;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.SiteDTO;

/**
 * Loader 
 * 
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class SiteFormLoader {

    private final Dispatcher dataConn;
    private final EventBus eventBus;

    private SiteFormLeash leash = null;

    public SiteFormLoader(EventBus eventBus, Dispatcher service) {
        this.dataConn = service;
        this.eventBus = eventBus;
    }


}
