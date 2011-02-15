/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.login;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous service for {@link RetrievePasswordService}
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface RetrievePasswordServiceAsync {

    public void retrievePassword(String email, String language, AsyncCallback<Void> asyncCallback);

}
