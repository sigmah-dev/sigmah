package org.sigmah.server.file;

import java.io.IOException;
import java.io.InputStream;

/**
 * Manages logo of an organization.
 * 
 * @author Aurélien Ponçon
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface LogoManager {

	/**
	 * Replace the old logo of an organization by the given new one.
	 * 
	 * @param logoInputStream
	 *          The inputstream of the image.
	 * @param path
	 *          The path of the logo file.
	 */
	long updateLogo(InputStream logoInputStream, String path) throws IOException;

}
