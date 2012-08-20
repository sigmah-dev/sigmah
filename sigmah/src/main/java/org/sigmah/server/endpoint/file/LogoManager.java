package org.sigmah.server.endpoint.file;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * Manages logo of an organization
 * 
 * @author Aurélien Ponçon
 *
 */
public interface LogoManager {
    
    /*
     * Replace the old logo of an organization by the new one
     * 
     * @param logoInputStream 
     *              The inputstream of the image
     * @param path
     *              The path of the logo file             
     */
    public void updateLogo(InputStream logoInputStream, String path) throws IOException;
    
}
