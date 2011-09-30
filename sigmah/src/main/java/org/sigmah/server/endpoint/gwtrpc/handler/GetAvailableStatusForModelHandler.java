package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.sigmah.shared.command.CheckModelUsage.ModelType;
import org.sigmah.shared.command.GetAvailableStatusForModel;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ProjectModelStatusListResult;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetAvailableStatusForModelHandler implements CommandHandler<GetAvailableStatusForModel> {

    private EntityManager em;

    @Inject
    public GetAvailableStatusForModelHandler(EntityManager em) {
        this.em = em;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CommandResult execute(GetAvailableStatusForModel cmd, User user) throws CommandException {

        final ProjectModelStatus status = cmd.getStatus();
        final ArrayList<ProjectModelStatus> availableStatus = new ArrayList<ProjectModelStatus>();

        if (status != null) {
            switch (status) {
            case DRAFT:
                availableStatus.add(ProjectModelStatus.DRAFT);
                availableStatus.add(ProjectModelStatus.READY);
                availableStatus.add(ProjectModelStatus.UNAVAILABLE);
                break;
            case READY:
                availableStatus.add(ProjectModelStatus.DRAFT);
                availableStatus.add(ProjectModelStatus.READY);
                availableStatus.add(ProjectModelStatus.UNAVAILABLE);
                break;
            case USED:
                availableStatus.add(ProjectModelStatus.USED);
                availableStatus.add(ProjectModelStatus.UNAVAILABLE);
                break;
            case UNAVAILABLE:

                availableStatus.add(ProjectModelStatus.UNAVAILABLE);

                final ModelType type = cmd.getModelType();
                final Integer idO = cmd.getOrgUnitModelId();
                final Long idP = cmd.getProjectModelId();
                Boolean used = null;

                if (type != null) {
                    switch (cmd.getModelType()) {
                    case ProjectModel: {
                        if (idP != null) {
                            final String queryStr = "SELECT p FROM Project p WHERE p.projectModel.id =:projectModelId";
                            final Query query = em.createQuery(queryStr);
                            query.setParameter("projectModelId", idP);

                            final List<Project> projects = (List<Project>) query.getResultList();
                            used = projects != null && projects.size() > 0;
                        }
                        break;
                    }
                    case OrgUnitModel: {
                        if (idO != null) {
                            String queryStr = "SELECT o FROM OrgUnit o WHERE o.orgUnitModel.id =:orgUnitModelId";
                            Query query = em.createQuery(queryStr);
                            query.setParameter("orgUnitModelId", idO);

                            List<OrgUnit> orgUnits = (List<OrgUnit>) query.getResultList();
                            used = orgUnits != null && orgUnits.size() > 0;
                        }
                        break;
                    }
                    }

                    if (used != null) {
                        if (used) {
                            availableStatus.add(ProjectModelStatus.USED);
                        } else {
                            availableStatus.add(ProjectModelStatus.READY);
                        }
                    }

                }

                break;

            }

        }

        return new ProjectModelStatusListResult(availableStatus.toArray(new ProjectModelStatus[availableStatus.size()]));

    }

}
