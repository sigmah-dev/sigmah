package org.sigmah.server.servlet.exporter.models;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.category.CategoryElement;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.QuestionChoiceElement;
import org.sigmah.server.domain.element.QuestionElement;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * Exports and imports organizational units models.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) V1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)v2.0
 */
public class OrgUnitModelHandler implements ModelHandler {

	private final static Log LOG = LogFactory.getLog(OrgUnitModelHandler.class);

	/**
	 * The map of imported objects (original object, transformed object)
	 */
	public static HashMap<Object, Object> modelesReset = new HashMap<Object, Object>();

	/**
	 * The list of imported objects which are transformed or being transformed.
	 */
	public static HashSet<Object> modelesImport = new HashSet<Object>();

	@Override
	public void importModel(InputStream inputStream, EntityManager em, User user) throws Exception {
		ObjectInputStream objectInputStream;
		em.getTransaction().begin();
		try {
			objectInputStream = new ObjectInputStream(inputStream);
			OrgUnitModel orgUnitModel = (OrgUnitModel) objectInputStream.readObject();
			orgUnitModel.resetImport();
			saveOrgUnitFlexibleElement(orgUnitModel, em);

			// Set the staus to DRAFT
			orgUnitModel.setStatus(ProjectModelStatus.DRAFT);
			orgUnitModel.setOrganization(user.getOrganization());
			em.persist(orgUnitModel);
			em.getTransaction().commit();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public String exportModel(OutputStream outputStream, String identifier, EntityManager em) throws Exception {
		String name = "";

		if (identifier != null) {
			final Integer orgUnitModelId = Integer.parseInt(identifier);

			final OrgUnitModel hibernateModel = em.find(OrgUnitModel.class, orgUnitModelId);

			if (hibernateModel == null)
				throw new Exception("No orgUnit model is associated with the identifier '" + identifier + "'.");

			name = hibernateModel.getName();

			// Stripping hibernate proxies from the model.

			final OrgUnitModel realModel = Realizer.realize(hibernateModel);

			// Serialization
			try {
				final ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
				objectOutputStream.writeObject(realModel);

			} catch (Exception ex) {
				throw new Exception("An error occured while serializing the orgUnit model " + orgUnitModelId, ex);
			}

		} else {
			throw new Exception("The identifier is missing.");
		}

		return name;
	}

	/**
	 * Save the flexible elements of imported organizational unit model
	 * 
	 * @param orgUnitModel
	 *          the imported organizational unit model
	 * @param em
	 *          the entity manager
	 */
	private void saveOrgUnitFlexibleElement(OrgUnitModel orgUnitModel, EntityManager em) {
		// OrgUnitModel --> Banner --> Layout --> Groups --> Constraints
		if (orgUnitModel.getBanner() != null && orgUnitModel.getBanner().getLayout() != null) {
			List<LayoutGroup> bannerLayoutGroups = orgUnitModel.getBanner().getLayout().getGroups();
			if (bannerLayoutGroups != null) {
				for (LayoutGroup layoutGroup : bannerLayoutGroups) {
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

										// Save QuestionChoiceElement with their QuestionElement parent(saved above)
										if (questionChoiceElements != null) {
											for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
												if (questionChoiceElement != null) {
													questionChoiceElement.setId(null);
													questionChoiceElement.setParentQuestion((QuestionElement) parent);
													CategoryElement categoryElement = questionChoiceElement.getCategoryElement();
													if (categoryElement != null) {
														questionChoiceElement.setCategoryElement(null);

														em.persist(questionChoiceElement);
														saveOrgUnitModelCategoryElement(categoryElement, em);
														questionChoiceElement.setCategoryElement(categoryElement);
														em.merge(questionChoiceElement);
													} else {
														em.persist(questionChoiceElement);
													}
												}
											}
											// Set saved QuestionChoiceElement to QuestionElement parent and update it
											((QuestionElement) parent).setChoices(questionChoiceElements);
										}

										// Save the Category type of QuestionElement parent(saved above)
										if (type != null) {
											if (em.find(CategoryType.class, type.getId()) == null) {
												List<CategoryElement> typeElements = type.getElements();
												if (typeElements != null) {
													type.setElements(null);
													em.merge(type);
													for (CategoryElement element : typeElements) {
														if (em.find(CategoryElement.class, element.getId()) == null) {
															element.setParentType(type);
															saveOrgUnitModelCategoryElement(element, em);
														}
													}
													type.setElements(typeElements);
													em.merge(type);
												}
											}
											// Set the saved CategoryType to QuestionElement parent and update it
											((QuestionElement) parent).setCategoryType(type);
										}
										// Update the QuestionElement parent
										em.merge(parent);
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
		// OrgUnitModel --> Detail --> Layout --> Groups --> Constraints
		if (orgUnitModel.getDetails() != null && orgUnitModel.getDetails().getLayout() != null) {
			List<LayoutGroup> detailLayoutGroups = orgUnitModel.getDetails().getLayout().getGroups();
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

										// Save QuestionChoiceElement with their QuestionElement parent(saved above)
										if (questionChoiceElements != null) {
											for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
												if (questionChoiceElement != null) {
													questionChoiceElement.setId(null);
													questionChoiceElement.setParentQuestion((QuestionElement) parent);
													CategoryElement categoryElement = questionChoiceElement.getCategoryElement();
													if (categoryElement != null) {
														questionChoiceElement.setCategoryElement(null);

														em.persist(questionChoiceElement);
														saveOrgUnitModelCategoryElement(categoryElement, em);
														questionChoiceElement.setCategoryElement(categoryElement);
														em.merge(questionChoiceElement);
													} else {
														em.persist(questionChoiceElement);
													}
												}
											}
											// Set saved QuestionChoiceElement to QuestionElement parent and update it
											((QuestionElement) parent).setChoices(questionChoiceElements);
										}

										// Save the Category type of QuestionElement parent(saved above)
										if (type != null) {
											if (em.find(CategoryType.class, type.getId()) == null) {
												List<CategoryElement> typeElements = type.getElements();
												if (typeElements != null) {
													type.setElements(null);
													em.merge(type);
													for (CategoryElement element : typeElements) {
														if (em.find(CategoryElement.class, element.getId()) == null) {
															element.setParentType(type);
															saveOrgUnitModelCategoryElement(element, em);
														}
													}
													type.setElements(typeElements);
													em.merge(type);
												}
											}
											// Set the saved CategoryType to QuestionElement parent and update it
											((QuestionElement) parent).setCategoryType(type);
										}
										// Update the QuestionElement parent
										em.merge(parent);
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
	}

	/**
	 * Save the category element of a question choice element.
	 * 
	 * @param categoryElement
	 *          the category element to save.
	 * @param em
	 *          the entity manager.
	 */
	private void saveOrgUnitModelCategoryElement(CategoryElement categoryElement, EntityManager em) {
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
								saveOrgUnitModelCategoryElement(element, em);
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
