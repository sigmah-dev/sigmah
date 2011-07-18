package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.server.endpoint.export.sigmah.handler.Realizer;
import org.sigmah.shared.command.GetOrgUnitModelCopy;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.OrgUnitModel;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.category.CategoryElement;
import org.sigmah.shared.domain.category.CategoryType;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.domain.element.QuestionChoiceElement;
import org.sigmah.shared.domain.element.QuestionElement;
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.domain.layout.LayoutGroup;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetOrgUnitModelCopyHandler implements CommandHandler<GetOrgUnitModelCopy>  {
	
	private static final Log log = LogFactory.getLog(GetOrgUnitModelCopyHandler.class);
	
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
    public GetOrgUnitModelCopyHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

	@Override
	public CommandResult execute(GetOrgUnitModelCopy cmd, User user)
			throws CommandException {
		if (log.isDebugEnabled()) {
            log.debug("[execute] Retrieving organizational unit model with id '" + cmd.getOrgUnitModelId() + "'.");
        }
		
		Integer existentOrgUnitModelId = Integer.valueOf(String.valueOf(cmd.getOrgUnitModelId()));

        final OrgUnitModel existentOrgUnitModel = em.find(OrgUnitModel.class, existentOrgUnitModelId);
        
        if (existentOrgUnitModel == null) {
            if (log.isDebugEnabled()) {
                log.debug("[execute] Organizational unit model model id#" + cmd.getOrgUnitModelId() + " doesn't exist.");
            }

            return null;
        } else {
        	if (log.isDebugEnabled()) {
                log.debug("[execute] Found organizational unit model model" + cmd.getOrgUnitModelId());
            }
        	
        	OrgUnitModel copyOrgUnitModel = Realizer.realize(existentOrgUnitModel);
        	copyOrgUnitModel.resetImport();
        	copyOrgUnitModel.setStatus(ProjectModelStatus.DRAFT);
    		saveFlexibleElement(copyOrgUnitModel, em);
    		copyOrgUnitModel.setName(cmd.getNewModelName());
    		copyOrgUnitModel.setOrganization(existentOrgUnitModel.getOrganization());
    		em.persist(copyOrgUnitModel);

        	return mapper.map(copyOrgUnitModel, OrgUnitModelDTO.class);      
        }
	}
	
	
	/**
	 * Save the flexible elements of imported organizational unit model
	 * 
	 * @param orgUnitModel
	 *            the imported organizational unit model
	 * @param em
	 *            the entity manager
	 */
	private void saveFlexibleElement(OrgUnitModel orgUnitModel,
			EntityManager em) {
		// OrgUnitModel --> Banner --> Layout --> Groups --> Constraints
		if (orgUnitModel.getBanner() != null
				&& orgUnitModel.getBanner().getLayout() != null) {
			List<LayoutGroup> bannerLayoutGroups = orgUnitModel.getBanner()
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
														saveCategoryElement(categoryElement, em);
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
		// OrgUnitModel --> Detail --> Layout --> Groups --> Constraints
		if (orgUnitModel.getDetails() != null
				&& orgUnitModel.getDetails().getLayout() != null) {
			List<LayoutGroup> detailLayoutGroups = orgUnitModel.getDetails()
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
														saveCategoryElement(categoryElement, em);
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
	}
	
	/**
	 * Save the category element of a question choice element.
	 * 
	 * @param categoryElement
	 *            the category element to save.
	 * @param em
	 *            the entity manager.
	 */
	private void saveCategoryElement(CategoryElement categoryElement, EntityManager em){
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
								saveCategoryElement(element, em);
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
