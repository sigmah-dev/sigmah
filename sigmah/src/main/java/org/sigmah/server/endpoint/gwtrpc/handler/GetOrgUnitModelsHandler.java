package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.domain.OrgUnitModel;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.command.GetOrgUnitModels;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.OrgUnitModelListResult;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * Retrieves the list of org unit models available to the user.
 * 
 * @author nrebiai
 * 
 */
public class GetOrgUnitModelsHandler implements CommandHandler<GetOrgUnitModels> {

    private static final Log log = LogFactory.getLog(GetOrgUnitModelsHandler.class);

    private final EntityManager em;
    private final Mapper mapper;

    @Inject
    public GetOrgUnitModelsHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    public CommandResult execute(GetOrgUnitModels cmd, User user) throws CommandException {

        final ArrayList<OrgUnitModelDTO> orgUnitModelDTOList = new ArrayList<OrgUnitModelDTO>();

        // Creates selection query.
        final Query query = em.createQuery("SELECT m FROM OrgUnitModel m ORDER BY m.name");

        // Gets all project models entities.
        @SuppressWarnings("unchecked")
        final List<OrgUnitModel> models = (List<OrgUnitModel>) query.getResultList();

        // Mapping (entity -> dto).
        if (models != null) {
            for (final OrgUnitModel model : models) {
                orgUnitModelDTOList.add(mapper.map(model, OrgUnitModelDTO.class));
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("[execute] Found " + orgUnitModelDTOList.size() + " org unit models.");
        }

        return new OrgUnitModelListResult(orgUnitModelDTOList);
    }

}
