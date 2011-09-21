/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.export.sigmah.handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.domain.Authentication;
import org.sigmah.server.endpoint.export.sigmah.ExportException;
import org.sigmah.shared.domain.PhaseModel;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.domain.ProjectModelVisibility;
import org.sigmah.shared.domain.category.CategoryElement;
import org.sigmah.shared.domain.category.CategoryType;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.domain.element.QuestionChoiceElement;
import org.sigmah.shared.domain.element.QuestionElement;
import org.sigmah.shared.domain.element.ReportElement;
import org.sigmah.shared.domain.element.ReportListElement;
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.domain.layout.LayoutGroup;
import org.sigmah.shared.domain.report.ProjectReportModel;

/**
 * Exports and imports project models.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ProjectModelHandler implements ModelHandler {

    private final static Log LOG = LogFactory.getLog(ProjectModelHandler.class);

    private ProjectModelType projectModelType = ProjectModelType.NGO;

    @Override
    public void importModel(InputStream inputStream, EntityManager em, Authentication authentication)
            throws ExportException {
        ObjectInputStream objectInputStream;
        em.getTransaction().begin();
        try {
            objectInputStream = new ObjectInputStream(inputStream);
            ProjectModel projectModel = (ProjectModel) objectInputStream.readObject();

            // Sets the new model as a draft.
            projectModel.setStatus(ProjectModelStatus.DRAFT);

            final HashMap<Object, Object> modelesReset = new HashMap<Object, Object>();
            final HashSet<Object> modelesImport = new HashSet<Object>();

            projectModel.resetImport(modelesReset, modelesImport);
            saveProjectFlexibleElement(projectModel, em, modelesReset, modelesImport, authentication);

            // Attaching the new model to the current user's organization
            final ProjectModelVisibility visibility = new ProjectModelVisibility();
            visibility.setModel(projectModel);
            visibility.setType(projectModelType);
            visibility.setOrganization(authentication.getUser().getOrganization());

            final ArrayList<ProjectModelVisibility> visibilities = new ArrayList<ProjectModelVisibility>();
            visibilities.add(visibility);
            projectModel.setVisibilities(visibilities);

            // Set the staus to DRAFT
            projectModel.setStatus(ProjectModelStatus.DRAFT);

            for (PhaseModel pm : projectModel.getPhases()) {
                for (LayoutGroup lg : pm.getLayout().getGroups()) {
                    for (LayoutConstraint lc : lg.getConstraints()) {
                        FlexibleElement fe = lc.getElement();
                        System.out.println(fe.getClass() + " id " + fe.getId());
                        if (fe instanceof QuestionElement) {
                            for (QuestionChoiceElement qce : ((QuestionElement) fe).getChoices()) {
                                System.out.println(qce.getClass() + " id " + qce.getId());
                            }
                        }
                    }
                }
            }

            em.merge(projectModel);
            em.getTransaction().commit();
            LOG.debug("The project model '" + projectModel.getName() + "' has been imported successfully.");

        } catch (Throwable e) {
            LOG.debug("Error while importing a project model.", e);
            throw new ExportException("Error while importing a project model.", e);
        }
    }

    @Override
    public String exportModel(OutputStream outputStream, String identifier, EntityManager em) throws ExportException {

        String name = "";

        if (identifier != null) {
            final Long projectModelId = Long.parseLong(identifier);

            final ProjectModel hibernateModel = em.find(ProjectModel.class, projectModelId);

            if (hibernateModel == null)
                throw new ExportException("No project model is associated with the identifier '" + identifier + "'.");

            name = hibernateModel.getName();

            // Removing superfluous links
            hibernateModel.setVisibilities(null);

            // Stripping hibernate proxies from the model.
            final ProjectModel realModel = Realizer.realize(hibernateModel);

            // Serialization
            try {
                final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(realModel);

            } catch (IOException ex) {
                throw new ExportException("An error occured while serializing the project model " + projectModelId, ex);
            }

        } else {
            throw new ExportException("The identifier is missing.");
        }

        return name;
    }

    /**
     * Define the default project model type used when importing a project
     * model.
     * 
     * @param projectModelType
     */
    public void setProjectModelType(ProjectModelType projectModelType) {
        this.projectModelType = projectModelType;
    }

    /**
     * Save the flexible elements of imported project model.
     * 
     * @param projectModel
     *            the imported project model
     * @param em
     *            the entity manager
     */
    private void saveProjectFlexibleElement(ProjectModel projectModel, EntityManager em,
            HashMap<Object, Object> modelesReset, HashSet<Object> modelesImport, Authentication authentication) {

        // ProjectModel --> Banner --> Layout --> Groups --> Constraints
        if (projectModel.getProjectBanner() != null && projectModel.getProjectBanner().getLayout() != null) {

            final List<LayoutGroup> bannerLayoutGroups = projectModel.getProjectBanner().getLayout().getGroups();
            saveLayoutGroups(bannerLayoutGroups, em, modelesReset, modelesImport, authentication);
        }

        // ProjectModel --> Detail --> Layout --> Groups --> Constraints
        if (projectModel.getProjectDetails() != null && projectModel.getProjectDetails().getLayout() != null) {

            final List<LayoutGroup> detailLayoutGroups = projectModel.getProjectDetails().getLayout().getGroups();
            saveLayoutGroups(detailLayoutGroups, em, modelesReset, modelesImport, authentication);
        }

        // ProjectModel --> Phases --> Layout --> Groups --> Constraints
        List<PhaseModel> phases = projectModel.getPhases();
        if (phases != null) {
            projectModel.setPhases(null);
            em.persist(projectModel);
            for (PhaseModel phase : phases) {
                phase.setParentProjectModel(projectModel);
                if (phase.getLayout() != null) {

                    final List<LayoutGroup> phaseLayoutGroups = phase.getLayout().getGroups();
                    saveLayoutGroups(phaseLayoutGroups, em, modelesReset, modelesImport, authentication);
                }
                if (phase.getDefinition() != null) {
                    em.persist(phase.getDefinition());
                }
                em.persist(phase);
            }
            projectModel.setPhases(phases);
        }
    }

    private void saveLayoutGroups(final List<LayoutGroup> layoutGroups, EntityManager em,
            HashMap<Object, Object> modelesReset, HashSet<Object> modelesImport, Authentication authentication) {

        final HashSet<Integer> reportModelsId = new HashSet<Integer>();

        if (layoutGroups != null) {
            for (LayoutGroup layoutGroup : layoutGroups) {

                final List<LayoutConstraint> layoutConstraints;
                if (layoutGroup != null)
                    layoutConstraints = layoutGroup.getConstraints();
                else
                    layoutConstraints = null;

                if (layoutConstraints != null) {
                    // Iterating over the constraints
                    for (LayoutConstraint layoutConstraint : layoutConstraints) {
                        if (layoutConstraint != null && layoutConstraint.getElement() != null) {

                            // Do not persist an element twice.
                            if (em.contains(layoutConstraint.getElement())) {
                                continue;
                            }

                            // If the current element is a QuestionElement
                            if (layoutConstraint.getElement() instanceof QuestionElement) {
                                List<QuestionChoiceElement> questionChoiceElements = ((QuestionElement) layoutConstraint
                                        .getElement()).getChoices();
                                CategoryType type = ((QuestionElement) layoutConstraint.getElement()).getCategoryType();
                                if (questionChoiceElements != null || type != null) {

                                    FlexibleElement parent = (FlexibleElement) layoutConstraint.getElement();
                                    ((QuestionElement) parent).setChoices(null);
                                    ((QuestionElement) parent).setCategoryType(null);
                                    em.persist(parent);

                                    // Save QuestionChoiceElement with their
                                    // QuestionElement parent(saved above)
                                    if (questionChoiceElements != null) {
                                        for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
                                            if (questionChoiceElement != null) {
                                                questionChoiceElement.setId(null);
                                                questionChoiceElement.setParentQuestion((QuestionElement) parent);
                                                CategoryElement categoryElement = questionChoiceElement
                                                        .getCategoryElement();
                                                if (categoryElement != null) {
                                                    questionChoiceElement.setCategoryElement(null);

                                                    em.persist(questionChoiceElement);
                                                    saveProjectModelCategoryElement(categoryElement, em, modelesReset,
                                                            modelesImport);
                                                    questionChoiceElement.setCategoryElement(categoryElement);
                                                    em.merge(questionChoiceElement);
                                                } else {
                                                    em.persist(questionChoiceElement);
                                                }
                                            }
                                        }
                                        // Set saved QuestionChoiceElement to
                                        // QuestionElement parent and update it
                                        ((QuestionElement) parent).setChoices(questionChoiceElements);
                                    }

                                    // Save the Category type of QuestionElement
                                    // parent(saved above)
                                    if (type != null) {
                                        if (em.find(CategoryType.class, type.getId()) == null) {
                                            List<CategoryElement> typeElements = type.getElements();
                                            if (typeElements != null) {
                                                type.setElements(null);
                                                em.merge(type);
                                                for (CategoryElement element : typeElements) {
                                                    if (em.find(CategoryElement.class, element.getId()) == null) {
                                                        element.setParentType(type);
                                                        saveProjectModelCategoryElement(element, em, modelesReset,
                                                                modelesImport);
                                                    }
                                                }
                                                type.setElements(typeElements);
                                                em.merge(type);
                                            }
                                        }
                                        // Set the saved CategoryType to
                                        // QuestionElement parent and update it
                                        ((QuestionElement) parent).setCategoryType(type);
                                    }
                                    // Update the QuestionElement parent
                                    em.merge(parent);
                                } else {
                                    em.persist(layoutConstraint.getElement());
                                }
                            }
                            // Report element
                            else if (layoutConstraint.getElement() instanceof ReportElement) {

                                final ReportElement element = (ReportElement) layoutConstraint.getElement();
                                final ProjectReportModel oldModel = element.getModel();

                                if (oldModel != null) {

                                    final int oldModelId = oldModel.getId();
                                    final ProjectReportModel newModel;

                                    if (!reportModelsId.contains(oldModelId)) {
                                        oldModel.resetImport(new HashMap<Object, Object>(), new HashSet<Object>());
                                        oldModel.setOrganization(authentication.getUser().getOrganization());
                                        newModel = oldModel;
                                        ProjectReportModelHandler.saveProjectReportModelElement(newModel, em);
                                        em.persist(newModel);
                                        element.setModel(newModel);
                                        em.persist(element);
                                        reportModelsId.add(element.getModel().getId());
                                    }
                                    // If the report model has been already
                                    // saved, it is re-used.
                                    else {
                                        newModel = em.find(ProjectReportModel.class, oldModelId);
                                        element.setModel(newModel);
                                        em.persist(element);
                                    }

                                }

                            }
                            // Reports list element
                            else if (layoutConstraint.getElement() instanceof ReportListElement) {

                                final ReportListElement element = (ReportListElement) layoutConstraint.getElement();
                                final ProjectReportModel oldModel = element.getModel();

                                if (oldModel != null) {

                                    final int oldModelId = oldModel.getId();
                                    final ProjectReportModel newModel;

                                    if (!reportModelsId.contains(oldModelId)) {
                                        oldModel.resetImport(new HashMap<Object, Object>(), new HashSet<Object>());
                                        oldModel.setOrganization(authentication.getUser().getOrganization());
                                        newModel = oldModel;
                                        em.persist(element);
                                        reportModelsId.add(element.getModel().getId());
                                    }
                                    // If the report model has been already
                                    // saved, it is re-used.
                                    else {
                                        newModel = em.find(ProjectReportModel.class, oldModelId);
                                        element.setModel(newModel);
                                        em.persist(element);
                                    }

                                }

                            }
                            // Others elements
                            else {
                                em.persist(layoutConstraint.getElement());
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Save the category element of a question choice element.
     * 
     * @param categoryElement
     *            the category element to save.
     * @param em
     *            the entity manager.
     */
    private void saveProjectModelCategoryElement(CategoryElement categoryElement, EntityManager em,
            HashMap<Object, Object> modelesReset, HashSet<Object> modelesImport) {
        if (!modelesImport.contains(categoryElement)) {
            modelesImport.add(categoryElement);

            if (!modelesReset.containsKey(categoryElement)) {
                CategoryElement key = categoryElement;
                categoryElement.setId(null);

                CategoryType parentType = categoryElement.getParentType();
                if (!modelesImport.contains(parentType)) {
                    modelesImport.add(parentType);

                    if (!modelesReset.containsKey(parentType)) {
                        CategoryType parentKey = parentType;
                        parentType.setId(null);

                        List<CategoryElement> elements = parentType.getElements();
                        if (elements != null) {
                            parentType.setElements(null);
                            em.persist(parentType);
                            for (CategoryElement element : elements) {
                                categoryElement.setParentType(parentType);
                                saveProjectModelCategoryElement(element, em, modelesReset, modelesImport);
                            }
                            parentType.setElements(elements);
                            em.merge(parentType);
                        } else {
                            em.persist(parentType);
                        }
                        modelesReset.put(parentKey, parentType);
                    } else {
                        parentType = (CategoryType) modelesReset.get(parentType);
                    }
                }
                categoryElement.setParentType(parentType);
                em.persist(categoryElement);
                modelesReset.put(key, categoryElement);
            } else {
                categoryElement = (CategoryElement) modelesReset.get(categoryElement);
            }
        }
    }
}
