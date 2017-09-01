package org.sigmah.client.ui.view.calendar;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
/**
 *
 * @author Your Name <your.name at your.org>
 */
@RemoteServiceRelativePath(DesEncrypter.REMOTE_SERVICE_RELATIVE_PATH_ENC)
public interface DesEncrypter  extends RemoteService {
    
    public static final String REMOTE_SERVICE_RELATIVE_PATH_ENC = "encrypt";
    public String encrypt(String str) ;
}
