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
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.domain.layout.LayoutGroup;


/**
 * Exports and imports project models.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ProjectModelHandler implements ModelHandler {
	
	/**
	 * The map of imported objects (original object, transformed object)
	 */
	public static HashMap<Object, Object> modelesReset = new HashMap<Object, Object>();
	
	/**
	 * The list of imported objects which are transformed or being transformed.
	 */
	public static HashSet<Object> modelesImport = new HashSet<Object>();
	
	private ProjectModelType projectModelType = ProjectModelType.NGO;
	
    @Override
    public void importModel(InputStream inputStream, EntityManager em, Authentication authentication) throws ExportException {
    	ObjectInputStream objectInputStream;
    	em.getTransaction().begin();
		try {
			objectInputStream = new ObjectInputStream(inputStream);
			ProjectModel projectModel = (ProjectModel) objectInputStream.readObject();
			projectModel.resetImport(modelesReset, modelesImport);
			saveProjectFlexibleElement(projectModel, em) ;
			
			// Attaching the new model to the current user's organization
			final ProjectModelVisibility visibility = new ProjectModelVisibility();
			visibility.setModel(projectModel);
			visibility.setType(projectModelType);
			visibility.setOrganization(authentication.getUser().getOrganization());
			
			final ArrayList<ProjectModelVisibility> visibilities = new ArrayList<ProjectModelVisibility>();
			visibilities.add(visibility);
			projectModel.setVisibilities(visibilities);
			
			//Set the staus to DRAFT
			projectModel.setStatus(ProjectModelStatus.DRAFT);
			
			em.merge(projectModel);
			em.getTransaction().commit();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}    	
    }

    @Override
    public String exportModel(OutputStream outputStream, String identifier,
            EntityManager em) throws ExportException {

        String name ="";

        if(identifier != null) {
            final Long projectModelId = Long.parseLong(identifier);

            final ProjectModel hibernateModel = em.find(ProjectModel.class, projectModelId);

            if(hibernateModel == null)
                throw new ExportException("No project model is associated with the identifier '"+identifier+"'.");

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
                throw new ExportException("An error occured while serializing the project model "+projectModelId, ex);
            }

        } else {
            throw new ExportException("The identifier is missing.");
        }

        return name;
    }
    
    /**
     * Define the default project model type used when importing a project model.
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
	private void saveProjectFlexibleElement(ProjectModel projectModel,
			EntityManager em) {		
		// ProjectModel --> Banner --> Layout --> Groups --> Constraints
		if (projectModel.getProjectBanner() != null
				&& projectModel.getProjectBanner().getLayout() != null) {
			List<LayoutGroup> bannerLayoutGroups = projectModel.getProjectBanner()
					.getLayout().getGroups();
			if (bannerLayoutGroups != null) {
				for (LayoutGroup layoutGroup : bannerLayoutGroups) {
					List<LayoutConstraint> layoutConstraints = layoutGroup
							.getConstraints();
					if (layoutConstraints != null) {
						for (LayoutConstraint layoutConstraint : layoutConstraints) {
							if (layoutConstraint.getElement() != null) {
								if (layoutConstraint.getElement() instanceof QuestionElement) {
									List<QuestionChoiceElement> questionChoiceElements = ((QuestionElement) layoutConstraint
											.getElement()).getChoices();
									CategoryType  type = ((QuestionElement) layoutConstraint.getElement()).getCategoryType();
									if (questionChoiceElements != null || type!=null ) {
										
										FlexibleElement parent = (FlexibleElement) layoutConstraint
												.getElement();
										((QuestionElement) parent)
												.setChoices(null);
										((QuestionElement) parent).setCategoryType(null);
										em.persist(parent);
										
										// Save QuestionChoiceElement with their QuestionElement parent(saved above)
										if(questionChoiceElements!=null){
											for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
												if (questionChoiceElement != null) {
													questionChoiceElement
															.setId(null);
													questionChoiceElement
															.setParentQuestion((QuestionElement) parent);
													CategoryElement categoryElement = questionChoiceElement.getCategoryElement();
													if(categoryElement!=null){
														questionChoiceElement.setCategoryElement(null);
														
														em.persist(questionChoiceElement);
														saveProjectModelCategoryElement(categoryElement, em);
														questionChoiceElement.setCategoryElement(categoryElement);
														em.merge(questionChoiceElement);
													}else{
														em.persist(questionChoiceElement);
													}
												}
											}
											// Set saved QuestionChoiceElement to QuestionElement parent and update it
											((QuestionElement) parent).setChoices(questionChoiceElements);
										}
							
										// Save the Category type of QuestionElement parent(saved above)
										if(type!=null){
											if(em.find(CategoryType.class, type.getId())==null){								
												List<CategoryElement> typeElements = type.getElements();
												if(typeElements!=null){
													type.setElements(null);
													em.merge(type);
													for(CategoryElement element : typeElements){
														if(em.find(CategoryElement.class, element.getId())==null){
															element.setParentType(type);
															saveProjectModelCategoryElement(element, em);
														}
													}
													type.setElements(typeElements);
													em.merge(type);
												}
											}												
											//Set the saved CategoryType to QuestionElement parent and update it
											((QuestionElement) parent).setCategoryType(type);
										}
										//Update the QuestionElement parent 
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
		
		// ProjectModel --> Detail --> Layout --> Groups --> Constraints
		if (projectModel.getProjectDetails() != null
				&& projectModel.getProjectDetails().getLayout() != null) {
			List<LayoutGroup> detailLayoutGroups = projectModel.getProjectDetails()
					.getLayout().getGroups();
			if (detailLayoutGroups != null) {
				for (LayoutGroup layoutGroup : detailLayoutGroups) {
					List<LayoutConstraint> layoutConstraints = layoutGroup
							.getConstraints();
					if (layoutConstraints != null) {
						for (LayoutConstraint layoutConstraint : layoutConstraints) {
							if (layoutConstraint.getElement() != null) {
								if (layoutConstraint.getElement() instanceof QuestionElement) {
									List<QuestionChoiceElement> questionChoiceElements = ((QuestionElement) layoutConstraint
											.getElement()).getChoices();
									CategoryType  type = ((QuestionElement) layoutConstraint.getElement()).getCategoryType();
									if (questionChoiceElements != null || type!=null ) {
										
										FlexibleElement parent = (FlexibleElement) layoutConstraint
												.getElement();
										((QuestionElement) parent)
												.setChoices(null);
										((QuestionElement) parent).setCategoryType(null);
										em.persist(parent);
										
										// Save QuestionChoiceElement with their QuestionElement parent(saved above)
										if(questionChoiceElements!=null){
											for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
												if (questionChoiceElement != null) {
													questionChoiceElement
															.setId(null);
													questionChoiceElement
															.setParentQuestion((QuestionElement) parent);
													CategoryElement categoryElement = questionChoiceElement.getCategoryElement();
													if(categoryElement!=null){
														questionChoiceElement.setCategoryElement(null);
														
														em.persist(questionChoiceElement);
														saveProjectModelCategoryElement(categoryElement, em);
														questionChoiceElement.setCategoryElement(categoryElement);
														em.merge(questionChoiceElement);
													}else{
														em.persist(questionChoiceElement);
													}
												}
											}
											// Set saved QuestionChoiceElement to QuestionElement parent and update it
											((QuestionElement) parent).setChoices(questionChoiceElements);
										}
							
										// Save the Category type of QuestionElement parent(saved above)
										if(type!=null){
											if(em.find(CategoryType.class, type.getId())==null){								
												List<CategoryElement> typeElements = type.getElements();
												if(typeElements!=null){
													type.setElements(null);
													em.merge(type);
													for(CategoryElement element : typeElements){
														if(em.find(CategoryElement.class, element.getId())==null){
															element.setParentType(type);
															saveProjectModelCategoryElement(element, em);
														}
													}
													type.setElements(typeElements);
													em.merge(type);
												}
											}												
											//Set the saved CategoryType to QuestionElement parent and update it
											((QuestionElement) parent).setCategoryType(type);
										}
										//Update the QuestionElement parent 
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
		
		// ProjectModel --> Phases --> Layout --> Groups --> Constraints
		List<PhaseModel> phases = projectModel.getPhases();
		if (phases != null) {
			projectModel.setPhases(null);
			em.persist(projectModel);
			for (PhaseModel phase : phases) {
				phase.setParentProjectModel(projectModel);
				if (phase.getLayout() != null) {
					List<LayoutGroup> phaseLayoutGroups = phase.getLayout()
							.getGroups();
					if (phaseLayoutGroups != null) {
						for (LayoutGroup layoutGroup : phaseLayoutGroups) {
							List<LayoutConstraint> layoutConstraints = layoutGroup
									.getConstraints();
							if (layoutConstraints != null) {
								for (LayoutConstraint layoutConstraint : layoutConstraints) {
									if (layoutConstraint.getElement() != null) {
										
										// Save parent QuestionElement like a FlexibleElement
										if (layoutConstraint.getElement() instanceof QuestionElement) {
											List<QuestionChoiceElement> questionChoiceElements = ((QuestionElement) layoutConstraint
													.getElement()).getChoices();
											CategoryType  type = ((QuestionElement) layoutConstraint.getElement()).getCategoryType();
											if (questionChoiceElements != null || type!=null ) {
												
												FlexibleElement parent = (FlexibleElement) layoutConstraint
														.getElement();
												((QuestionElement) parent)
														.setChoices(null);
												((QuestionElement) parent).setCategoryType(null);
												em.persist(parent);
												
												// Save QuestionChoiceElement with their QuestionElement parent(saved above)
												if(questionChoiceElements!=null){
													for (QuestionChoiceElement questionChoiceElement : questionChoiceElements) {
														if (questionChoiceElement != null) {
															questionChoiceElement
																	.setId(null);
															questionChoiceElement
																	.setParentQuestion((QuestionElement) parent);
															CategoryElement categoryElement = questionChoiceElement.getCategoryElement();
															if(categoryElement!=null){
																questionChoiceElement.setCategoryElement(null);
																
																em.persist(questionChoiceElement);
																saveProjectModelCategoryElement(categoryElement, em);
																questionChoiceElement.setCategoryElement(categoryElement);
																em.merge(questionChoiceElement);
															}else{
																em.persist(questionChoiceElement);
															}
														}
													}
													// Set saved QuestionChoiceElement to QuestionElement parent and update it
													((QuestionElement) parent).setChoices(questionChoiceElements);
												}
									
												// Save the Category type of QuestionElement parent(saved above)
												if(type!=null){
													if(em.find(CategoryType.class, type.getId())==null){								
														List<CategoryElement> typeElements = type.getElements();
														if(typeElements!=null){
															type.setElements(null);
															em.merge(type);
															for(CategoryElement element : typeElements){
																if(em.find(CategoryElement.class, element.getId())==null){
																	element.setParentType(type);
																	saveProjectModelCategoryElement(element, em);
																}
															}
															type.setElements(typeElements);
															em.merge(type);
														}
													}												
													//Set the saved CategoryType to QuestionElement parent and update it
													((QuestionElement) parent).setCategoryType(type);
												}
												//Update the QuestionElement parent 
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
				if(phase.getDefinition()!=null){
					em.persist(phase.getDefinition());
				}
				em.persist(phase);
			}
			projectModel.setPhases(phases);
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
	private void saveProjectModelCategoryElement(CategoryElement categoryElement, EntityManager em){
		if(!modelesImport.contains(categoryElement)){
			modelesImport.add(categoryElement);
			
			if(!modelesReset.containsKey(categoryElement)){
				CategoryElement key = categoryElement;
				categoryElement.setId(null);
				
				CategoryType parentType = categoryElement.getParentType();
				if(!modelesImport.contains(parentType)){
					modelesImport.add(parentType);
					
					if(!modelesReset.containsKey(parentType)){
						CategoryType parentKey = parentType;
						parentType.setId(null);
						
						List<CategoryElement> elements = parentType.getElements();
						if(elements!=null){
							parentType.setElements(null);
							em.persist(parentType);
							for(CategoryElement element : elements){
								categoryElement.setParentType(parentType);
								saveProjectModelCategoryElement(element, em);
							}
							parentType.setElements(elements);
							em.merge(parentType);
						}else{
							em.persist(parentType);
						}						
						modelesReset.put(parentKey, parentType);
					}else{
						parentType = (CategoryType)modelesReset.get(parentType);
					}
				}
				categoryElement.setParentType(parentType);
				em.persist(categoryElement);
				modelesReset.put(key, categoryElement);
			}else{
				categoryElement = (CategoryElement)modelesReset.get(categoryElement);
			}
		}
	}	
}
