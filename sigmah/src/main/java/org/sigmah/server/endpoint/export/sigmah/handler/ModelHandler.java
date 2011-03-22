/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.export.sigmah.handler;

import java.io.InputStream;
import java.io.OutputStream;
import javax.persistence.EntityManager;
import org.sigmah.server.endpoint.export.sigmah.ExportException;

/**
 * Handle the export and import operations of models.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface ModelHandler {

    /**
     * Read a model from the given input stream and persist it.
     * @param inputStream Data stream.
     * @param em
     */
    void importModel(InputStream inputStream, EntityManager em) throws ExportException;

    /**
     * Write the content of the model identified by the given properties into
     * the given output stream.
     * @param outputStream Output stream.
     * @param properties Map of properties, must identify a model.
     * @param em
     */
    void exportModel(OutputStream outputStream, String identifier, EntityManager em) throws ExportException;
}
