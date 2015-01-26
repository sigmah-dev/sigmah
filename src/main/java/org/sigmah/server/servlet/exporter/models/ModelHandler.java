package org.sigmah.server.servlet.exporter.models;

import java.io.InputStream;
import java.io.OutputStream;

import javax.persistence.EntityManager;

import org.sigmah.server.domain.User;

/**
 * Handle the export and import operations of models.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) v1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
 */
public interface ModelHandler {

	/**
	 * Read a model from the given input stream and persist it.
	 * 
	 * @param inputStream
	 *          Data stream.
	 * @param em
	 * @param authentication
	 *          Current user.
	 */
	void importModel(InputStream inputStream, EntityManager em, User user) throws Exception;

	/**
	 * Write the content of the model identified by the given properties into the given output stream.
	 * 
	 * @param outputStream
	 *          Output stream.
	 * @param properties
	 *          Map of properties, must identify a model.
	 * @param em
	 * @return The name of the exported model.
	 */
	String exportModel(OutputStream outputStream, String identifier, EntityManager em) throws Exception;
}
