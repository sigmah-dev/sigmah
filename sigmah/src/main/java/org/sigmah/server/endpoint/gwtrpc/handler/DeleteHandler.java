/*
 * All Sigmah code is released under the GNU General Public License v3 
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.sigmah.client.page.dashboard.CreateProjectWindow.Mode;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.Deleteable;
import org.sigmah.shared.domain.OrgUnitModel;
import org.sigmah.shared.domain.Phase;
import org.sigmah.shared.domain.PhaseModel;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.UserDatabase;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.domain.layout.LayoutGroup;
import org.sigmah.shared.domain.report.ProjectReport;
import org.sigmah.shared.domain.report.ProjectReportVersion;
import org.sigmah.shared.domain.report.RichTextElement;
import org.sigmah.shared.domain.value.Value;

import com.google.inject.Inject;

/**
 * @author Alex Bertram
 * @see org.sigmah.shared.command.Delete
 * @see org.sigmah.shared.domain.Deleteable
 */
@SuppressWarnings("unchecked")
public class DeleteHandler implements CommandHandler<Delete> {

    private EntityManager em;

    @Inject
    public DeleteHandler(EntityManager em) {
        this.em = em;
    }

    @Override
    public CommandResult execute(Delete cmd, User user) {

        // TODO check permissions for delete!
        // These handler should redirect to one of the Entity policy classes.
        @SuppressWarnings("rawtypes")
        Class entityClass = entityClassForEntityName(cmd.getEntityName());

        if (Mode.TEST.equals(cmd.getMode())) {
            // Delete test project
            Project entity = (Project) em.find(entityClass, cmd.getId());
            deleteProjectWithDate(entity);
        } else if (ProjectModelStatus.DRAFT.equals(cmd.getProjectModelStatus())
            && "ProjectModel".equals(cmd.getEntityName())) { // Delete draft
                                                             // project
                                                             // model
            ProjectModel projectModel = (ProjectModel) em.find(entityClass, new Long(cmd.getId()));

            deleteProjectModelWithDate(projectModel);
            deleteDraftProjectModel(projectModel);

        } else if ("PhaseModel".equals(cmd.getEntityName())) {
            PhaseModel phaseModel = (PhaseModel) em.find(PhaseModel.class, new Long(cmd.getId()));
            deletePhaseModel(phaseModel);
        } else if (ProjectModelStatus.DRAFT.equals(cmd.getProjectModelStatus())
            && "OrgUnitModel".equals(cmd.getEntityName())) { // Delete draft
                                                             // OrgUnit
                                                             // model
            OrgUnitModel orgUnitModel = (OrgUnitModel) em.find(OrgUnitModel.class, new Integer(cmd.getId()));
            deleteOrgUnitModelWithDate(orgUnitModel);
        }

        else {
            Deleteable entity = (Deleteable) em.find(entityClass, cmd.getId());
            entity.delete();
        }

        return null;
    }

    private void deletePhaseModel(PhaseModel phaseModel) {

        // ----STEP1: delete the successor relation---------------------------

        // If this model is the successor of other phase model,
        // this relation should be removed first

        Query query = em.createQuery("FROM PhaseModel ");
        List<PhaseModel> models = (List<PhaseModel>) query.getResultList();
        for (PhaseModel p : models) {
            if (p.getSuccessors() != null) {
                if (p.getSuccessors().contains(phaseModel)) {
                    p.getSuccessors().remove(phaseModel);
                    em.merge(p);
                }
            }

        }

        // ----STEP2: delete all child phases using this phase model-----------
        Query query1 = em.createQuery("SELECT p FROM Phase p WHERE p.model.id=:phaseModelId");
        query1.setParameter("phaseModelId", phaseModel.getId());
        List<Phase> phases = (List<Phase>) query1.getResultList();
        for (Phase p : phases) {
            if (p.getParentProject() != null
                && p.getParentProject().getCurrentPhase() != null
                && p.getParentProject().getCurrentPhase() == p) {
                Project parentProject = em.find(Project.class, p.getParentProject().getId());
                parentProject.setCurrentPhase(null);
                em.merge(parentProject);
            }
            em.remove(p);
        }

        // -----STEP3: delete the phase
        // model-------------------------------------------

        em.remove(phaseModel);
        em.flush();
        em.clear();

    }

    private Class<Deleteable> entityClassForEntityName(String entityName) {
        try {
            return (Class<Deleteable>) Class.forName(UserDatabase.class.getPackage().getName() + "." + entityName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Invalid entity name '" + entityName + "'");
        } catch (ClassCastException e) {
            throw new RuntimeException("Entity type '" + entityName + "' not Deletable");
        }
    }

    /**
     * Delete the test project object.
     * 
     * @param project
     *            the object to delete.
     * @deprecated Use {@link #deleteProjectWithDate(Project)}
     */
    @SuppressWarnings("unused")
    @Deprecated
    private void deleteTestProject(Project project) {
        // delete the project flexible elements
        deleteProjectFlexibleElement(project);

        // delete the test project
        em.remove(project);
    }

    /**
     * Delete the project object.
     * 
     * @param project
     *            the object to delete.
     * @deprecated Use {@link #deleteProjectWithDate(Project)}
     */
    @SuppressWarnings("unused")
    @Deprecated
    private void deleteProject(Project project) {
        // delete the project flexible elements
        deleteProjectFlexibleElement(project);

        // delete the test project
        em.remove(project);
    }

    /**
     * Sets the deleted date of the given project.
     * 
     * @param project
     *            the project to delete.
     */
    private void deleteProjectWithDate(Project project) {
        project.delete();
    }

    /**
     * Sets the deleted date of the given project model.
     * 
     * @param projectModel
     *            the project model to delete.
     */
    private void deleteProjectModelWithDate(ProjectModel projectModel) {
        projectModel.delete();
    }

    /**
     * Sets the deleted date of the given org unit model.
     * 
     * @param orgUnitModel
     *            the org unit model to delete.
     */
    private void deleteOrgUnitModelWithDate(OrgUnitModel orgUnitModel) {
        orgUnitModel.delete();
    }

    /**
     * Delete the values of the test project.
     * 
     * @param project
     * @deprecated Use {@link #deleteProjectWithDate(Project)}
     */
    @Deprecated
    private void deleteProjectFlexibleElement(Project project) {
        // delete values
        Query query = em.createQuery("Select v FROM Value v WHERE v.containerId =:containerId");
        query.setParameter("containerId", project.getId());
        final List<Value> listResultsValues = (List<Value>) query.getResultList();
        if (listResultsValues != null) {
            for (Value value : listResultsValues) {
                em.remove(value);
            }
        }

        // delete project reports
        query = em.createQuery("Select pr FROM ProjectReport pr WHERE pr.project.id =:databaseid");
        query.setParameter("databaseid", project.getId());
        final List<ProjectReport> listResultReports = (List<ProjectReport>) query.getResultList();
        if (listResultReports != null) {
            for (ProjectReport report : listResultReports) {
                // Delete the project report's version
                ProjectReportVersion version = report.getCurrentVersion();
                if (version != null) {
                    // delete vercion's richText elements
                    List<RichTextElement> richTextElements = version.getTexts();
                    if (richTextElements != null) {
                        for (RichTextElement richTextElement : richTextElements) {
                            em.remove(richTextElement);
                        }
                    }

                    em.remove(version);
                }
                em.remove(report);
            }
        }
    }

    /**
     * Method to delete a project model. Only draft project model is allowed to delete.
     * 
     * @param projectModel
     * @author HUZHE(zhe.hu32@gmail.com)
     */
    private void deleteDraftProjectModel(ProjectModel projectModel) {

        // ------STEP 1: Get all projects using this project model and delete
        // them------------
        final Query query = em.createQuery("SELECT p FROM Project p WHERE p.projectModel=:model");
        query.setParameter("model", projectModel);
        List<Project> projects = (List<Project>) query.getResultList();

        for (Project p : projects) {
            deleteProjectWithDate(p);
        }

        em.flush();
    }

    /**
     * Delete a draft orgunit model
     * 
     * @param orgUnitModel
     *            orgUnit model to delete
     */
    private void deleteDraftOrgUnitModel(OrgUnitModel orgUnitModel) {
        // -------STEP1: Get all fields (FlexibleElement) in this model
        // ----------

        List<FlexibleElement> elements = new ArrayList<FlexibleElement>();

        // OrgUnitModel --> Banner --> Layout --> Groups --> Constraints
        if (orgUnitModel.getBanner() != null && orgUnitModel.getBanner().getLayout() != null) {
            List<LayoutGroup> bannerLayoutGroup = orgUnitModel.getBanner().getLayout().getGroups();

            if (bannerLayoutGroup != null) {
                for (LayoutGroup layoutGroup : bannerLayoutGroup) {
                    List<LayoutConstraint> layoutConstraints = layoutGroup.getConstraints();
                    if (layoutConstraints != null) {
                        for (LayoutConstraint layoutConstraint : layoutConstraints) {
                            if (layoutConstraint.getElement() != null) {

                                elements.add(layoutConstraint.getElement());

                            }
                        }
                    }
                }
            }

        }

        // OrgUnitModel --> Detail --> Layout --> Groups --> Constraints
        if (orgUnitModel.getDetails() != null && orgUnitModel.getDetails().getLayout() != null) {
            List<LayoutGroup> detailLayoutGroups = orgUnitModel.getDetails().getLayout().getGroups();

            if (detailLayoutGroups != null) {
                for (LayoutGroup detailLayoutGroup : detailLayoutGroups) {

                    List<LayoutConstraint> layoutConstraints = detailLayoutGroup.getConstraints();

                    if (layoutConstraints != null) {
                        for (LayoutConstraint layoutConstraint : layoutConstraints) {
                            if (layoutConstraint.getElement() != null) {
                                elements.add(layoutConstraint.getElement());
                            }
                        }
                    }

                }
            }
        }

        // -------SETP2: Delete this model
        // -----------------------------------------------------

        em.remove(orgUnitModel);

        // -------SETP3: Delete all flexible
        // elements-------------------------------------------
        for (FlexibleElement e : elements) {
            em.remove(e);
        }

        // update
        em.flush();

    }

}
