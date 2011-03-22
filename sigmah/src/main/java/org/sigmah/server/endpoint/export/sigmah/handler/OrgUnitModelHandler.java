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
import org.sigmah.shared.domain.OrgUnitModel;

/**
 * Exports and imports organizational units models.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OrgUnitModelHandler implements ModelHandler {

    @Override
    public void importModel(InputStream inputStream, EntityManager em) throws ExportException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void exportModel(OutputStream outputStream, String identifier,
            EntityManager em) throws ExportException {
        
        if(identifier != null) {
            final Integer orgUnitModelId = Integer.parseInt(identifier);

            final OrgUnitModel hibernateModel = em.find(OrgUnitModel.class, orgUnitModelId);

            if(hibernateModel == null)
                throw new ExportException("No orgUnit model is associated with the identifier '"+identifier+"'.");

            // Stripping hibernate proxies from the model.
            final OrgUnitModel realModel = Realizer.realize(hibernateModel);

            // Serialization
            try {
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(realModel);

            } catch (IOException ex) {
                throw new ExportException("An error occured while serializing the orgUnit model "+orgUnitModelId, ex);
            }

        } else {
            throw new ExportException("The identifier is missing.");
        }
    }

}
