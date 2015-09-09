package org.sigmah.server.servlet.exporter.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.ProjectModelVisibility;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.category.CategoryElement;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.domain.element.BudgetElement;
import org.sigmah.server.domain.element.BudgetSubField;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.QuestionChoiceElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.domain.element.ReportElement;
import org.sigmah.server.domain.element.ReportListElement;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.ProjectModelType;

/**
 * Exports and imports project models.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) V1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) V2.0
 */
public class ProjectModelHandler implements ModelHandler {

	private final static Log LOG = LogFactory.getLog(ProjectModelHandler.class);

	private ProjectModelType projectModelType = ProjectModelType.NGO;

	@Override
	public void importModel(InputStream inputStream, EntityManager em, User user) throws Exception {
		ObjectInputStream objectInputStream;
		em.getTransaction().begin();
		try {
			objectInputStream = new ObjectInputStream(inputStream);
			ProjectModel projectModel = (ProjectModel) objectInputStream.readObject();

			// Sets the new model as a draft.
			projectModel.setStatus(ProjectModelStatus.DRAFT);

			final HashMap<Object, Object> modelesReset = new HashMap<Object, Object>();
			final HashSet<Object> modelesImport = new HashSet<Object>();

			projectModel.resetImport();
			saveProjectFlexibleElement(projectModel, em, modelesReset, modelesImport, user);

			// Attaching the new model to the current user's organization
			final ProjectModelVisibility visibility = new ProjectModelVisibility();
			visibility.setModel(projectModel);
			visibility.setType(projectModelType);
			visibility.setOrganization(user.getOrganization());

			final ArrayList<ProjectModelVisibility> visibilities = new ArrayList<ProjectModelVisibility>();
			visibilities.add(visibility);
			projectModel.setVisibilities(visibilities);

			// Set the status to DRAFT
			projectModel.setStatus(ProjectModelStatus.DRAFT);

			// TODO ??
			for (PhaseModel pm : projectModel.getPhaseModels()) {
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

		} catch (IOException | ClassNotFoundException e) {
			LOG.debug("Error while importing a project model.", e);
			throw new Exception("Error while importing a project model.", e);
		}
	}

	@Override
	public String exportModel(OutputStream outputStream, String identifier, EntityManager em) throws Exception {

		String name = "";

		if (identifier == null) {
			throw new ExportException("The identifier is missing.");
		}

		final Integer projectModelId = Integer.parseInt(identifier);

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

		} catch (Exception ex) {
			throw new Exception("An error occured while serializing the project model " + projectModelId, ex);
		}

		return name;
	}

	/**
	 * Define the default project model type used when importing a project model.
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
	 *          the imported project model
	 * @param em
	 *          the entity manager
	 */
	private void saveProjectFlexibleElement(ProjectModel projectModel, EntityManager em, HashMap<Object, Object> modelesReset, HashSet<Object> modelesImport,
			User user) {

		// ProjectModel --> Banner --> Layout --> Groups --> Constraints
		if (projectModel.getProjectBanner() != null && projectModel.getProjectBanner().getLayout() != null) {

			final List<LayoutGroup> bannerLayoutGroups = projectModel.getProjectBanner().getLayout().getGroups();
			saveLayoutGroups(bannerLayoutGroups, em, modelesReset, modelesImport, user);
		}

		// ProjectModel --> Detail --> Layout --> Groups --> Constraints
		if (projectModel.getProjectDetails() != null && projectModel.getProjectDetails().getLayout() != null) {

			final List<LayoutGroup> detailLayoutGroups = projectModel.getProjectDetails().getLayout().getGroups();
			saveLayoutGroups(detailLayoutGroups, em, modelesReset, modelesImport, user);
		}

		// ProjectModel --> Phases --> Layout --> Groups --> Constraints
		List<PhaseModel> phases = projectModel.getPhaseModels();
		if (phases != null) {
			projectModel.setPhaseModels(null);
			em.persist(projectModel);
			for (PhaseModel phase : phases) {
				phase.setParentProjectModel(projectModel);
				if (phase.getLayout() != null) {

					final List<LayoutGroup> phaseLayoutGroups = phase.getLayout().getGroups();
					saveLayoutGroups(phaseLayoutGroups, em, modelesReset, modelesImport, user);
				}
				if (phase.getDefinition() != null) {
					em.persist(phase.getDefinition());
				}
				em.persist(phase);
			}
			projectModel.setPhaseModels(phases);
		}
	}

	private void saveLayoutGroups(final List<LayoutGroup> layoutGroups, EntityManager em, HashMap<Object, Object> modelesReset, HashSet<Object> modelesImport,
			User user) {

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
							final FlexibleElement element = layoutConstraint.getElement();

							// Do not persist an element twice.
							if (em.contains(element)) {
								continue;
							}

							// Initialize export flags of flexible element
							element.initializeExportFlags();

							// If the current element is a QuestionElement
							if (element instanceof QuestionElement) {
								final QuestionElement questionElement = (QuestionElement) element;
								List<QuestionChoiceElement> questionChoiceElements = questionElement.getChoices();
								CategoryType type = questionElement.getCategoryType();
								if (questionChoiceElements != null || type != null) {

									questionElement.setChoices(null);
									questionElement.setCategoryType(null);
									em.persist(questionElement);

									// Save QuestionChoiceElement with their
									// QuestionElement parent(saved above)
									if (questionChoiceElements != null) {
										for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
											if (questionChoiceElement != null) {
												questionChoiceElement.setId(null);
												questionChoiceElement.setParentQuestion(questionElement);
												CategoryElement categoryElement = questionChoiceElement.getCategoryElement();
												if (categoryElement != null) {
													questionChoiceElement.setCategoryElement(null);

													em.persist(questionChoiceElement);
													saveProjectModelCategoryElement(categoryElement, em, modelesReset, modelesImport);
													questionChoiceElement.setCategoryElement(categoryElement);
													em.merge(questionChoiceElement);
												} else {
													em.persist(questionChoiceElement);
												}
											}
										}
										// Set saved QuestionChoiceElement to
										// QuestionElement parent and update it
										questionElement.setChoices(questionChoiceElements);
									}

									// Save the Category type of QuestionElement
									// parent(saved above)
									if (type != null) {
										if (em.find(CategoryType.class, type.getId()) == null) {
											List<CategoryElement> typeElements = type.getElements();
											if (typeElements != null) {
												type.setElements(null);
												em.merge(type);
												for (CategoryElement categoryElement : typeElements) {
													if (em.find(CategoryElement.class, categoryElement.getId()) == null) {
														categoryElement.setParentType(type);
														saveProjectModelCategoryElement(categoryElement, em, modelesReset, modelesImport);
													}
												}
												type.setElements(typeElements);
												em.merge(type);
											}
										}
										// Set the saved CategoryType to
										// QuestionElement parent and update it
										questionElement.setCategoryType(type);
									}
									// Update the QuestionElement parent
									em.merge(questionElement);
									
								} else {
									em.persist(element);
								}
							}
							// Report element
							else if (element instanceof ReportElement) {
								final ReportElement reportElement = (ReportElement) element;
								final ProjectReportModel oldModel = reportElement.getModel();

								if (oldModel != null) {
									final int oldModelId = oldModel.getId();
									final ProjectReportModel newModel;

									if (!reportModelsId.contains(oldModelId)) {
										oldModel.resetImport(new HashMap<Object, Object>(), new HashSet<Object>());
										oldModel.setOrganization(user.getOrganization());
										newModel = oldModel;
										ProjectReportModelHandler.saveProjectReportModelElement(newModel, em);
										em.persist(newModel);
										reportElement.setModel(newModel);
										em.persist(reportElement);
										reportModelsId.add(reportElement.getModel().getId());
									}
									// If the report model has been already
									// saved, it is re-used.
									else {
										newModel = em.find(ProjectReportModel.class, oldModelId);
										reportElement.setModel(newModel);
										em.persist(reportElement);
									}

								}

							}
							// Reports list element
							else if (element instanceof ReportListElement) {
								final ReportListElement reportListElement = (ReportListElement) element;
								final ProjectReportModel oldModel = reportListElement.getModel();

								if (oldModel != null) {
									final int oldModelId = oldModel.getId();
									final ProjectReportModel newModel;

									if (!reportModelsId.contains(oldModelId)) {
										oldModel.resetImport(new HashMap<Object, Object>(), new HashSet<Object>());
										oldModel.setOrganization(user.getOrganization());
										newModel = oldModel;
										ProjectReportModelHandler.saveProjectReportModelElement(newModel, em);
										em.persist(newModel);
										reportListElement.setModel(newModel);
										em.persist(reportListElement);
										reportModelsId.add(reportListElement.getModel().getId());
									}
									// If the report model has been already
									// saved, it is re-used.
									else {
										newModel = em.find(ProjectReportModel.class, oldModelId);
										reportListElement.setModel(newModel);
										em.persist(reportListElement);
									}
								}
							}
							// Budget element
							else if(element instanceof BudgetElement) {
								final BudgetElement budgetElement = (BudgetElement) element;
								
								final List<BudgetSubField> subFields = budgetElement.getBudgetSubFields();
								final BudgetSubField ratioDividend = budgetElement.getRatioDividend();
								final BudgetSubField ratioDivisor = budgetElement.getRatioDivisor();
								
								budgetElement.setBudgetSubFields(null);
								budgetElement.setRatioDividend(null);
								budgetElement.setRatioDivisor(null);
								
								em.persist(budgetElement);
								
								final ArrayList<BudgetSubField> allSubFields = new ArrayList<BudgetSubField>(subFields);
								allSubFields.add(ratioDividend);
								allSubFields.add(ratioDivisor);
								
								for(final BudgetSubField subField : allSubFields) {
									subField.setBudgetElement(budgetElement);
									em.persist(budgetElement);
								}
								budgetElement.setBudgetSubFields(subFields);
								budgetElement.setRatioDividend(ratioDividend);
								budgetElement.setRatioDivisor(ratioDivisor);
								em.merge(budgetElement);
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
	 *          the category element to save.
	 * @param em
	 *          the entity manager.
	 */
	private void saveProjectModelCategoryElement(CategoryElement categoryElement, EntityManager em, HashMap<Object, Object> modelesReset,
			HashSet<Object> modelesImport) {
		if (!modelesImport.contains(categoryElement)) {
			modelesImport.add(categoryElement);

			if (!modelesReset.containsKey(categoryElement)) {
				CategoryElement key = categoryElement;
				modelesReset.put(key, categoryElement);
				categoryElement.setId(null);

				CategoryType parentType = categoryElement.getParentType();
				if (!modelesImport.contains(parentType)) {
					modelesImport.add(parentType);

					if (!modelesReset.containsKey(parentType)) {
						CategoryType parentKey = parentType;
						modelesReset.put(parentKey, parentType);
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
					} else {
						parentType = (CategoryType) modelesReset.get(parentType);
					}
				}
				categoryElement.setParentType(parentType);
				em.persist(categoryElement);
			} else {
				categoryElement = (CategoryElement) modelesReset.get(categoryElement);
			}
		}
	}
}
