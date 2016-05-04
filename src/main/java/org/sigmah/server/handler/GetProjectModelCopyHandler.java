package org.sigmah.server.handler;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.ProjectModelVisibility;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.domain.element.BudgetElement;
import org.sigmah.server.domain.element.BudgetSubField;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.QuestionChoiceElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.servlet.exporter.models.Realizer;
import org.sigmah.shared.command.GetProjectModelCopy;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

/**
 * Handler for the {@link GetProjectModelCopy} command.
 * 
 * @author Kristela Macaj (kmacaj@ideia.fr) (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetProjectModelCopyHandler extends AbstractCommandHandler<GetProjectModelCopy, ProjectModelDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetProjectModelCopyHandler.class);

	/**
	 * The map of imported objects (original object, transformed object)
	 */
	private static final HashMap<Object, Object> modelesReset = new HashMap<Object, Object>();

	/**
	 * The list of imported objects which are transformed or being transformed.
	 */
	private static final HashSet<Object> modelesImport = new HashSet<Object>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectModelDTO execute(final GetProjectModelCopy cmd, final UserExecutionContext context) throws CommandException {

		LOG.debug("Retrieving project model for command: {}", cmd);

		final ProjectModel existentProjectModel = em().find(ProjectModel.class, cmd.getModelId());

		if (existentProjectModel == null) {
			LOG.debug("Project model id with #{} doesn't exist.", cmd.getModelId());
			throw new CommandException("Project model with id " + cmd.getModelId() + " cannot be found.");
		}

		LOG.debug("Found project model with id #{}.", cmd.getModelId());

		final ProjectModel copyProjectModel = Realizer.realize(existentProjectModel);
		copyProjectModel.resetImport(true);

		// Set status 'DRAFT'
		copyProjectModel.setStatus(ProjectModelStatus.DRAFT);

		// Set the visibility
		final ProjectModelVisibility visibility = new ProjectModelVisibility();
		visibility.setModel(copyProjectModel);
		if (copyProjectModel.getVisibilities() != null && !copyProjectModel.getVisibilities().isEmpty()) {
			visibility.setType(copyProjectModel.getVisibilities().get(0).getType());
		}
		visibility.setOrganization(context.getUser().getOrganization());

		final ArrayList<ProjectModelVisibility> visibilities = new ArrayList<ProjectModelVisibility>();
		visibilities.add(visibility);
		copyProjectModel.setVisibilities(visibilities);

		saveCopy(copyProjectModel, cmd);

		return mapper().map(copyProjectModel, new ProjectModelDTO(), cmd.getMappingMode());
	}

	/**
	 * Save the given copy in a transaction.
	 * 
	 * @param copyProjectModel
	 * @param cmd 
	 */
	@Transactional
	protected void saveCopy(final ProjectModel copyProjectModel, final GetProjectModelCopy cmd) {
		// Save project elements
		saveFlexibleElement(copyProjectModel, em());

		copyProjectModel.setName(cmd.getNewModelName());

		em().persist(copyProjectModel);
	}

	/**
	 * Saves the flexible elements of the imported project model.
	 * 
	 * @param projectModel
	 *          The imported project model.
	 * @param em
	 *          The entity manager.
	 */
	private static void saveFlexibleElement(final ProjectModel projectModel, final EntityManager em) {

		// ProjectModel → Banner → Layout → Groups → Constraints

		if (projectModel.getProjectBanner() == null || projectModel.getProjectBanner().getLayout() == null) {
			return;
		}

		final List<LayoutGroup> bannerLayoutGroups = projectModel.getProjectBanner().getLayout().getGroups();
		if (bannerLayoutGroups != null) {
			for (final LayoutGroup layoutGroup : bannerLayoutGroups) {

				final List<LayoutConstraint> layoutConstraints = layoutGroup.getConstraints();

				if (layoutConstraints == null) {
					continue;
				}

				for (final LayoutConstraint layoutConstraint : layoutConstraints) {

					if (layoutConstraint.getElement() == null) {
						continue;
					}

					if (layoutConstraint.getElement() instanceof QuestionElement) {
						List<QuestionChoiceElement> questionChoiceElements = ((QuestionElement) layoutConstraint.getElement()).getChoices();
						CategoryType type = ((QuestionElement) layoutConstraint.getElement()).getCategoryType();
						if (questionChoiceElements != null || type != null) {

							FlexibleElement parent = layoutConstraint.getElement();
							((QuestionElement) parent).setChoices(null);
							((QuestionElement) parent).setCategoryType(null);
							em.persist(parent);

							// Save QuestionChoiceElement with their QuestionElement parent(saved above).
							if (questionChoiceElements != null) {
								for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
									if (questionChoiceElement != null) {
										questionChoiceElement.setId(null);
										questionChoiceElement.setParentQuestion((QuestionElement) parent);
										// BUGFIX #652: Removed the duplication of the category element.
										em.persist(questionChoiceElement);
									}
								}
								// Set saved QuestionChoiceElement to QuestionElement parent and update it.
								((QuestionElement) parent).setChoices(questionChoiceElements);
							}

							// Save the Category type of QuestionElement parent(saved above).
							if (type != null) {
								// Set the saved CategoryType to QuestionElement parent and update it.
								((QuestionElement) parent).setCategoryType(type);
							}
							// Update the QuestionElement parent.
							em.merge(parent);
						} else {
							em.persist(layoutConstraint.getElement());
						}

					} else if (layoutConstraint.getElement() instanceof BudgetElement) {
						List<BudgetSubField> budgetSubFields = ((BudgetElement) layoutConstraint.getElement()).getBudgetSubFields();
						if (budgetSubFields != null) {
							FlexibleElement parent = layoutConstraint.getElement();
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

		// ProjectModel → Detail → Layout → Groups → Constraints

		if (projectModel.getProjectDetails() != null && projectModel.getProjectDetails().getLayout() != null) {
			List<LayoutGroup> detailLayoutGroups = projectModel.getProjectDetails().getLayout().getGroups();
			if (detailLayoutGroups != null) {
				for (LayoutGroup layoutGroup : detailLayoutGroups) {
					List<LayoutConstraint> layoutConstraints = layoutGroup.getConstraints();
					if (layoutConstraints != null) {
						for (LayoutConstraint layoutConstraint : layoutConstraints) {
							if (layoutConstraint.getElement() != null) {
								if (layoutConstraint.getElement() instanceof QuestionElement) {
									List<QuestionChoiceElement> questionChoiceElements = ((QuestionElement) layoutConstraint.getElement()).getChoices();
									CategoryType type = ((QuestionElement) layoutConstraint.getElement()).getCategoryType();
									if (questionChoiceElements != null || type != null) {

										FlexibleElement parent = layoutConstraint.getElement();
										((QuestionElement) parent).setChoices(null);
										((QuestionElement) parent).setCategoryType(null);
										em.persist(parent);

										// Save QuestionChoiceElement with their QuestionElement parent(saved above).
										if (questionChoiceElements != null) {
											for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
												if (questionChoiceElement != null) {
													questionChoiceElement.setId(null);
													questionChoiceElement.setParentQuestion((QuestionElement) parent);
													// BUGFIX #652: Removed the duplication of the category element.
													em.persist(questionChoiceElement);
												}
											}
											// Set saved QuestionChoiceElement to QuestionElement parent and update it.
											((QuestionElement) parent).setChoices(questionChoiceElements);
										}

										// Save the Category type of QuestionElement parent(saved above).
										if (type != null) {
											// Set the saved CategoryType to QuestionElement parent and update it
											((QuestionElement) parent).setCategoryType(type);
										}
										// Update the QuestionElement parent.
										em.merge(parent);
									} else {
										em.persist(layoutConstraint.getElement());
									}
								} else if (layoutConstraint.getElement() instanceof BudgetElement) {
									List<BudgetSubField> budgetSubFields = ((BudgetElement) layoutConstraint.getElement()).getBudgetSubFields();
									if (budgetSubFields != null) {
										FlexibleElement parent = layoutConstraint.getElement();
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

		// ProjectModel → Phases → Layout → Groups → Constraints
		List<PhaseModel> phases = projectModel.getPhaseModels();
		if (phases != null) {
			projectModel.setPhaseModels(null);
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

										// Save parent QuestionElement like a FlexibleElement.
										if (layoutConstraint.getElement() instanceof QuestionElement) {
											List<QuestionChoiceElement> questionChoiceElements = ((QuestionElement) layoutConstraint.getElement()).getChoices();
											CategoryType type = ((QuestionElement) layoutConstraint.getElement()).getCategoryType();
											if (questionChoiceElements != null || type != null) {

												FlexibleElement parent = layoutConstraint.getElement();
												((QuestionElement) parent).setChoices(null);
												((QuestionElement) parent).setCategoryType(null);
												em.persist(parent);

												// Save QuestionChoiceElement with their QuestionElement parent(saved above).
												if (questionChoiceElements != null) {
													for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
														if (questionChoiceElement != null) {
															questionChoiceElement.setId(null);
															questionChoiceElement.setParentQuestion((QuestionElement) parent);
															
															// BUGFIX #652: Removed the duplication of the category element.
															em.persist(questionChoiceElement);
														}
													}
													// Set saved QuestionChoiceElement to QuestionElement parent and update it.
													((QuestionElement) parent).setChoices(questionChoiceElements);
												}

												// Save the Category type of QuestionElement parent(saved above).
												if (type != null) {
													// Set the saved CategoryType to QuestionElement parent and update it.
													((QuestionElement) parent).setCategoryType(type);
												}
												// Update the QuestionElement parent.
												em.merge(parent);
											} else {
												em.persist(layoutConstraint.getElement());
											}
										} else if (layoutConstraint.getElement() instanceof BudgetElement) {
											List<BudgetSubField> budgetSubFields = ((BudgetElement) layoutConstraint.getElement()).getBudgetSubFields();
											if (budgetSubFields != null) {
												FlexibleElement parent = layoutConstraint.getElement();
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
			projectModel.setPhaseModels(phases);
		}
	}

}
