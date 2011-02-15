/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.login;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Service that allows users to retrieves their passwords.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@RemoteServiceRelativePath("password")
public interface RetrievePasswordService extends RemoteService {
    void retrievePassword(String email, String language) throws Exception;
}
