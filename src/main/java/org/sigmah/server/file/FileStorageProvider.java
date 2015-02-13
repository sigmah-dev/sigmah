package org.sigmah.server.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;

/**
 * Provides storage for the contents of uploaded files.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface FileStorageProvider {

	/**
	 * Creates a new file and returns the output stream to which it can be written.
	 * 
	 * @param storageId
	 *          The unique storage id for this version of the file.
	 * @return an OutputStream for writing the file's contents.
	 * @throws IOException
	 *           If the given {@code storageId} cannot be created.
	 */
	OutputStream create(String storageId) throws IOException;

	/**
	 * Opens the file of the given id.
	 * 
	 * @param storageId
	 *          The unique storage id for this version of the file.
	 * @return and input stream from which the contents can be read.
	 * @throws IOException
	 *           If the given {@code storageId} does not exist or cannot be opened.
	 */
	InputStream open(String storageId) throws IOException;

	/**
	 * Delete the file of the given id.
	 * 
	 * @param storageId
	 *          The unique storage id of the file.
	 * @return A boolean value indicating if the deletion was successful.
	 * @throws IOException
	 *           If the given {@code storageId} does not exist or cannot be deleted.
	 */
	boolean delete(String storageId) throws IOException;

	/**
	 * Copy the content of an inputstream in the file referenced by the given path
	 * 
	 * @param input
	 *          The input stream of the file to copy
	 * @param fileId
	 *          The unique storage id of the file.
	 * @param options
	 *          Options for the copy if needed
	 * @return Numbers of bytes written
	 * @throws IOException
	 */
	long copy(InputStream input, String fileId, CopyOption... options) throws IOException;
	
	/**
	 * Tells if the given id matches an existing file.
	 * 
	 * @param storageId
	 *			Storage id to test.
	 * @return <code>true</code> if a file exists, <code>false</code> otherwise.
	 */
	boolean exists(String storageId);
}
