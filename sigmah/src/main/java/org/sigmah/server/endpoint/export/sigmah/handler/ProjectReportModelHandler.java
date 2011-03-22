/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.export.sigmah.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import javax.persistence.EntityManager;
import org.sigmah.server.endpoint.export.sigmah.ExportException;
import org.sigmah.shared.domain.report.ProjectReportModel;

/**
 * Exports and imports project report models.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ProjectReportModelHandler implements ModelHandler {

    @Override
    public void importModel(InputStream inputStream, EntityManager em) throws ExportException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void exportModel(OutputStream outputStream, String identifier,
            EntityManager em) throws ExportException {

        if(identifier != null) {
            final Integer projectReportModelId = Integer.parseInt(identifier);

            final ProjectReportModel hibernateModel = em.find(ProjectReportModel.class, projectReportModelId);

            if(hibernateModel == null)
                throw new ExportException("No project report model is associated with the identifier '"+identifier+"'.");

            // Stripping hibernate proxies from the model.
            final ProjectReportModel realModel = Realizer.realize(hibernateModel);

            // Serialization
            try {
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(realModel);

            } catch (IOException ex) {
                throw new ExportException("An error occured while serializing the project model "+projectReportModelId, ex);
            }

        } else {
            throw new ExportException("The identifier is missing.");
        }
    }

}
