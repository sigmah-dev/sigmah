package org.sigmah.client.ui.view.calendar;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 *
 * @author Your Name <your.name at your.org>
 */
public interface DesEncrypterAsync {
    public void encrypt(String s, AsyncCallback<String> callback);
}
