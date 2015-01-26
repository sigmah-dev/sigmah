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

	/**
	 * Creates a new file and returns the output stream to which it can be written.
	 * 
	 * @param storageId the unique storage id for this version of the file
	 * @return an OutputStream for writing the file's contents
	 * @throws IOException
	 */
	OutputStream create(String storageId) throws IOException;
	
	/**
	 * Opens the file of the given id
	 * 
	 * @param storageId the unique storage id for this version of the file
	 * @return and input stream from which the contents can be read
	 * @throws IOException
	 */
	InputStream open(String storageId) throws IOException;
	
	
	/**
	 * Delete the file of the given id
	 * @param storageId the unique storage id of the file
	 * @return boolean indicating if the deletion was succesful
	 */
	Boolean delete(String storageId);
}
