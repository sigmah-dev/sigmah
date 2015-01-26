/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.login;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous service for {@link PasswordManagementService}
 * @author RaphaÃ«l Calabro (rcalabro@ideia.fr)
 */
public interface PasswordManagementServiceAsync {

    public void forgotPassword(String email, String language, String hostUrl, AsyncCallback<Void> asyncCallback);
    public void validateAndGetUserEmailByToken(String token, AsyncCallback<String> asyncCallback);
    public void updatePassword(String email,String password, AsyncCallback<Void> asyncCallback);

}
