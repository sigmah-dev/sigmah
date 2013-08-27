package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.server.endpoint.export.sigmah.handler.Realizer;
import org.sigmah.shared.command.GetProjectModelCopy;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.PhaseModel;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.ProjectModelVisibility;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.category.CategoryElement;
import org.sigmah.shared.domain.category.CategoryType;
import org.sigmah.shared.domain.element.BudgetElement;
import org.sigmah.shared.domain.element.BudgetSubField;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.domain.element.QuestionChoiceElement;
import org.sigmah.shared.domain.element.QuestionElement;
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.domain.layout.LayoutGroup;
import org.sigmah.shared.dto.ProjectModelDTOLight;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * Handler for the {@link GetProjectModelCopy} command.
 * 
 * @author Kristela Macaj (kmacaj@ideia.fr)
 */
public class GetProjectModelCopyHandler implements CommandHandler<GetProjectModelCopy> {

	private static final Log log = LogFactory.getLog(GetProjectModelCopyHandler.class);

	private final EntityManager em;
	private final Mapper mapper;

	/**
	 * The map of imported objects (original object, transformed object)
	 */
	public static HashMap<Object, Object> modelesReset = new HashMap<Object, Object>();

	/**
	 * The list of imported objects which are transformed or being transformed.
	 */
	public static HashSet<Object> modelesImport = new HashSet<Object>();

	@Inject
	public GetProjectModelCopyHandler(EntityManager em, Mapper mapper) {
		this.em = em;
		this.mapper = mapper;
	}

	@Override
	public CommandResult execute(GetProjectModelCopy cmd, User user) throws CommandException {

		if (log.isDebugEnabled()) {
			log.debug("[execute] Retrieving project model with id '" + cmd.getProjectModelId() + "'.");
		}

		Long existentProjectModelId = Long.valueOf(String.valueOf(cmd.getProjectModelId()));

		final ProjectModel existentProjectModel = em.find(ProjectModel.class, existentProjectModelId);

		if (existentProjectModel == null) {
			if (log.isDebugEnabled()) {
				log.debug("[execute] Project model id#" + cmd.getProjectModelId() + " doesn't exist.");
			}

			return null;
		} else {
			if (log.isDebugEnabled()) {
				log.debug("[execute] Found project model" + cmd.getProjectModelId());
			}
			ProjectModel copyProjectModel = Realizer.realize(existentProjectModel);
			copyProjectModel.resetImport(modelesReset, modelesImport);

			// Set status 'DRAFT'
			copyProjectModel.setStatus(ProjectModelStatus.DRAFT);

			// Set the visibility
			final ProjectModelVisibility visibility = new ProjectModelVisibility();
			visibility.setModel(copyProjectModel);
			if (copyProjectModel.getVisibilities() != null && !copyProjectModel.getVisibilities().isEmpty()) {
				visibility.setType(copyProjectModel.getVisibilities().get(0).getType());
			}
			visibility.setOrganization(user.getOrganization());

			final ArrayList<ProjectModelVisibility> visibilities = new ArrayList<ProjectModelVisibility>();
			visibilities.add(visibility);
			copyProjectModel.setVisibilities(visibilities);

			// Save project elements
			saveFlexibleElement(copyProjectModel, em);

			copyProjectModel.setName(cmd.getNewModelName());

			em.persist(copyProjectModel);

			return mapper.map(copyProjectModel, ProjectModelDTOLight.class);
		}
	}

	/**
	 * Save the flexible elements of imported project model.
	 * 
	 * @param projectModel
	 *            the imported project model
	 * @param em
	 *            the entity manager
	 */
	private void saveFlexibleElement(ProjectModel projectModel, EntityManager em) {
		// ProjectModel --> Banner --> Layout --> Groups --> Constraints
		if (projectModel.getProjectBanner() != null && projectModel.getProjectBanner().getLayout() != null) {
			List<LayoutGroup> bannerLayoutGroups = projectModel.getProjectBanner().getLayout().getGroups();
			if (bannerLayoutGroups != null) {
				for (LayoutGroup layoutGroup : bannerLayoutGroups) {
					List<LayoutConstraint> layoutConstraints = layoutGroup.getConstraints();
					if (layoutConstraints != null) {
						for (LayoutConstraint layoutConstraint : layoutConstraints) {
							if (layoutConstraint.getElement() != null) {
								if (layoutConstraint.getElement() instanceof QuestionElement) {
									List<QuestionChoiceElement> questionChoiceElements = ((QuestionElement) layoutConstraint
									                .getElement()).getChoices();
									CategoryType type = ((QuestionElement) layoutConstraint.getElement())
									                .getCategoryType();
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
														saveProjectModelCategoryElement(categoryElement, em);
														questionChoiceElement.setCategoryElement(categoryElement);
														em.merge(questionChoiceElement);
													} else {
														em.persist(questionChoiceElement);
													}
												}
											}
											// Set saved QuestionChoiceElement
											// to QuestionElement parent and
											// update it
											((QuestionElement) parent).setChoices(questionChoiceElements);
										}

										// Save the Category type of
										// QuestionElement parent(saved above)
										if (type != null) {
											// Set the saved CategoryType to
											// QuestionElement parent and update
											// it
											((QuestionElement) parent).setCategoryType(type);
										}
										// Update the QuestionElement parent
										em.merge(parent);
									} else {
										em.persist(layoutConstraint.getElement());
									}
								} else if (layoutConstraint.getElement() instanceof BudgetElement) {
									List<BudgetSubField> budgetSubFields = ((BudgetElement) layoutConstraint
									                .getElement()).getBudgetSubFields();
									if (budgetSubFields != null) {
										FlexibleElement parent = (FlexibleElement) layoutConstraint.getElement();
										((BudgetElement) parent).setBudgetSubFields(null);
										((BudgetElement) parent).setRatioDividend(null);
										((BudgetElement) parent).setRatioDivisor(null);

										if (budgetSubFields != null) {
											for (BudgetSubField budgetSubField : budgetSubFields) {
												if (budgetSubField != null) {
													budgetSubField.setId(null);
													if (budgetSubField.getType() != null) {
														switch (budgetSubField.getType()) {
														case PLANNED:
															((BudgetElement) parent).setRatioDivisor(budgetSubField);
															break;
														case RECEIVED:
															break;
														case SPENT:
															((BudgetElement) parent).setRatioDividend(budgetSubField);
															break;
														default:
															break;

														}
													}
													budgetSubField.setBudgetElement((BudgetElement) parent);

													em.persist(budgetSubField);
												}
											}
										}

									} else {
										em.persist(layoutConstraint.getElement());
									}
								}
							}
						}
					}
				}
			}

			// ProjectModel --> Detail --> Layout --> Groups --> Constraints
			if (projectModel.getProjectDetails() != null && projectModel.getProjectDetails().getLayout() != null) {
				List<LayoutGroup> detailLayoutGroups = projectModel.getProjectDetails().getLayout().getGroups();
				if (detailLayoutGroups != null) {
					for (LayoutGroup layoutGroup : detailLayoutGroups) {
						List<LayoutConstraint> layoutConstraints = layoutGroup.getConstraints();
						if (layoutConstraints != null) {
							for (LayoutConstraint layoutConstraint : layoutConstraints) {
								if (layoutConstraint.getElement() != null) {
									if (layoutConstraint.getElement() instanceof QuestionElement) {
										List<QuestionChoiceElement> questionChoiceElements = ((QuestionElement) layoutConstraint
										                .getElement()).getChoices();
										CategoryType type = ((QuestionElement) layoutConstraint.getElement())
										                .getCategoryType();
										if (questionChoiceElements != null || type != null) {

											FlexibleElement parent = (FlexibleElement) layoutConstraint.getElement();
											((QuestionElement) parent).setChoices(null);
											((QuestionElement) parent).setCategoryType(null);
											em.persist(parent);

											// Save QuestionChoiceElement with
											// their
											// QuestionElement parent(saved
											// above)
											if (questionChoiceElements != null) {
												for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
													if (questionChoiceElement != null) {
														questionChoiceElement.setId(null);
														questionChoiceElement
														                .setParentQuestion((QuestionElement) parent);
														CategoryElement categoryElement = questionChoiceElement
														                .getCategoryElement();
														if (categoryElement != null) {
															questionChoiceElement.setCategoryElement(null);

															em.persist(questionChoiceElement);
															saveProjectModelCategoryElement(categoryElement, em);
															questionChoiceElement.setCategoryElement(categoryElement);
															em.merge(questionChoiceElement);
														} else {
															em.persist(questionChoiceElement);
														}
													}
												}
												// Set saved
												// QuestionChoiceElement
												// to QuestionElement parent and
												// update it
												((QuestionElement) parent).setChoices(questionChoiceElements);
											}

											// Save the Category type of
											// QuestionElement parent(saved
											// above)
											if (type != null) {
												// Set the saved CategoryType to
												// QuestionElement parent and
												// update
												// it
												((QuestionElement) parent).setCategoryType(type);
											}
											// Update the QuestionElement parent
											em.merge(parent);
										} else {
											em.persist(layoutConstraint.getElement());
										}
									} else if (layoutConstraint.getElement() instanceof BudgetElement) {
										List<BudgetSubField> budgetSubFields = ((BudgetElement) layoutConstraint
										                .getElement()).getBudgetSubFields();
										if (budgetSubFields != null) {
											FlexibleElement parent = (FlexibleElement) layoutConstraint.getElement();
											((BudgetElement) parent).setBudgetSubFields(null);
											((BudgetElement) parent).setRatioDividend(null);
											((BudgetElement) parent).setRatioDivisor(null);

											for (BudgetSubField budgetSubField : budgetSubFields) {
												if (budgetSubField != null) {
													budgetSubField.setId(null);
													if (budgetSubField.getType() != null) {
														switch (budgetSubField.getType()) {
														case PLANNED:
															((BudgetElement) parent).setRatioDivisor(budgetSubField);
															break;
														case RECEIVED:
															break;
														case SPENT:
															((BudgetElement) parent).setRatioDividend(budgetSubField);
															break;
														default:
															break;

														}
													}
													budgetSubField.setBudgetElement((BudgetElement) parent);

													em.persist(budgetSubField);
												}
											}
										} else {
											em.persist(layoutConstraint.getElement());
										}

									} else {
										em.persist(layoutConstraint.getElement());
									}
								}
							}
						}
					}
				}
			}

			// ProjectModel --> Phases --> Layout --> Groups --> Constraints
			List<PhaseModel> phases = projectModel.getPhases();
			if (phases != null) {
				projectModel.setPhases(null);
				em.persist(projectModel);
				for (PhaseModel phase : phases) {
					phase.setParentProjectModel(projectModel);
					if (phase.getLayout() != null) {
						List<LayoutGroup> phaseLayoutGroups = phase.getLayout().getGroups();
						if (phaseLayoutGroups != null) {
							for (LayoutGroup layoutGroup : phaseLayoutGroups) {
								List<LayoutConstraint> layoutConstraints = layoutGroup.getConstraints();
								if (layoutConstraints != null) {
									for (LayoutConstraint layoutConstraint : layoutConstraints) {
										if (layoutConstraint.getElement() != null) {

											// Save parent QuestionElement like
											// a
											// FlexibleElement
											if (layoutConstraint.getElement() instanceof QuestionElement) {
												List<QuestionChoiceElement> questionChoiceElements = ((QuestionElement) layoutConstraint
												                .getElement()).getChoices();
												CategoryType type = ((QuestionElement) layoutConstraint.getElement())
												                .getCategoryType();
												if (questionChoiceElements != null || type != null) {

													FlexibleElement parent = (FlexibleElement) layoutConstraint
													                .getElement();
													((QuestionElement) parent).setChoices(null);
													((QuestionElement) parent).setCategoryType(null);
													em.persist(parent);

													// Save
													// QuestionChoiceElement
													// with their
													// QuestionElement
													// parent(saved above)
													if (questionChoiceElements != null) {
														for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
															if (questionChoiceElement != null) {
																questionChoiceElement.setId(null);
																questionChoiceElement
																                .setParentQuestion((QuestionElement) parent);
																CategoryElement categoryElement = questionChoiceElement
																                .getCategoryElement();
																if (categoryElement != null) {
																	questionChoiceElement.setCategoryElement(null);
																	em.persist(questionChoiceElement);
																	saveProjectModelCategoryElement(categoryElement, em);
																	questionChoiceElement
																	                .setCategoryElement(categoryElement);
																	em.merge(questionChoiceElement);
																} else {
																	em.persist(questionChoiceElement);
																}
															}
														}
														// Set saved
														// QuestionChoiceElement
														// to
														// QuestionElement
														// parent
														// and update it
														((QuestionElement) parent).setChoices(questionChoiceElements);
													}

													// Save the Category type of
													// QuestionElement
													// parent(saved
													// above)
													if (type != null) {
														// Set the saved
														// CategoryType to
														// QuestionElement
														// parent
														// and update it
														((QuestionElement) parent).setCategoryType(type);
													}
													// Update the
													// QuestionElement
													// parent
													em.merge(parent);
												} else {
													em.persist(layoutConstraint.getElement());
												}
											} else if (layoutConstraint.getElement() instanceof BudgetElement) {
												List<BudgetSubField> budgetSubFields = ((BudgetElement) layoutConstraint
												                .getElement()).getBudgetSubFields();
												if (budgetSubFields != null) {
													FlexibleElement parent = (FlexibleElement) layoutConstraint
													                .getElement();
													((BudgetElement) parent).setBudgetSubFields(null);
													((BudgetElement) parent).setRatioDividend(null);
													((BudgetElement) parent).setRatioDivisor(null);

													for (BudgetSubField budgetSubField : budgetSubFields) {
														if (budgetSubField != null) {
															budgetSubField.setId(null);
															if (budgetSubField.getType() != null) {
																switch (budgetSubField.getType()) {
																case PLANNED:
																	((BudgetElement) parent)
																	                .setRatioDivisor(budgetSubField);
																	break;
																case RECEIVED:
																	break;
																case SPENT:
																	((BudgetElement) parent)
																	                .setRatioDividend(budgetSubField);
																	break;
																default:
																	break;

																}
															}
															budgetSubField.setBudgetElement((BudgetElement) parent);

															em.persist(budgetSubField);
														}
													}
													em.persist(parent);
												} else {
													em.persist(layoutConstraint.getElement());
												}

											} else {
												em.persist(layoutConstraint.getElement());
											}
										}
									}
								}
							}
						}
					}
					if (phase.getDefinition() != null) {
						em.persist(phase.getDefinition());
					}
					em.persist(phase);
				}
				projectModel.setPhases(phases);
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
	private void saveProjectModelCategoryElement(CategoryElement categoryElement, EntityManager em) {
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
								saveProjectModelCategoryElement(element, em);
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
