/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.login;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Service that allows users to retrieves their passwords.
 * @author RaphaÃ«l Calabro (rcalabro@ideia.fr)
 */
@RemoteServiceRelativePath("password")
public interface PasswordManagementService extends RemoteService {
    void forgotPassword(String email, String language, String hostUrl) throws Exception;
    String validateAndGetUserEmailByToken(String token) throws Exception;
    void updatePassword(String email,String password) throws Exception;
}
