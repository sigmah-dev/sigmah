package org.sigmah.server.file.util;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface MultipartRequestCallback {
	void onInputStream(InputStream stream, String name, String mimeType) throws IOException;
}
