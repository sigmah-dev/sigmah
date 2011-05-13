package org.sigmah.server.endpoint.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



/**
 * 
 * Provides storage for the contents of uploaded files
 * 
 * @author alexander
 *
 */
public interface FileStorageProvider {

	OutputStream create(String storageId) throws IOException;
	
	InputStream open(String storageId) throws IOException;
	
	
}
