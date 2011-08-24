package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.shared.command.result.ValueResultUtils;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Phase;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.ProjectFunding;
import org.sigmah.shared.domain.ProjectModelVisibility;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.category.CategoryElement;
import org.sigmah.shared.domain.category.CategoryType;
import org.sigmah.shared.domain.element.QuestionChoiceElement;
import org.sigmah.shared.domain.value.Value;
import org.sigmah.shared.dto.ProjectDTOLight;
import org.sigmah.shared.dto.ProjectModelVisibilityDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;

import com.google.inject.Inject;

/**
 * Responsible for the mapping of project objects.
 * 
 * @author tmi
 * 
 */
public class ProjectMapper {

    private final static Log LOG = LogFactory.getLog(GetProjectsHandler.class);

    private final Query orgUnitQuery;
    private final Query categoriesQuery;
    private final Query choicesQuery;
    

    @Inject
    public ProjectMapper(EntityManager em) {

        // Queries.
        orgUnitQuery = em.createQuery("SELECT o FROM OrgUnit o WHERE :project MEMBER OF o.databases");
        categoriesQuery = em.createQuery("SELECT v FROM Value v JOIN v.element e WHERE v.containerId = :projectId AND "
                + "e.id IN (SELECT q.id FROM QuestionElement q WHERE q.categoryType IS NOT NULL)");
        choicesQuery = em.createQuery("SELECT c FROM QuestionChoiceElement c WHERE c.id IN (:ids)");
              
    }

    /**
     * Map a project into a project light DTO.
     * 
     * @param project
     *            The project.
     * @param mapChildren
     *            If the children projects must be retrieved.
     * @return The light DTO.
     */
    @SuppressWarnings("unchecked")
    public ProjectDTOLight map(final Project project, final boolean mapChildren) {

        final StringBuilder sb = new StringBuilder();
        sb.append("Project light mapping:\n");

        final ProjectDTOLight pLight = new ProjectDTOLight();

        // ---------------
        // -- SIMPLE FIELDS
        // ---------------

        long start = new Date().getTime();

        pLight.setId(project.getId());
        pLight.setName(project.getName());
        pLight.setFullName(project.getFullName());
        pLight.setPlannedBudget(project.getPlannedBudget());
        pLight.setReceivedBudget(project.getReceivedBudget());
        pLight.setSpendBudget(project.getSpendBudget());
        pLight.setStartDate(project.getStartDate());
        pLight.setEndDate(project.getEndDate());
        pLight.setCloseDate(project.getCloseDate());
        pLight.setActivityAdvancement(project.getActivityAdvancement());
        pLight.setOrgUnitName(project.getCountry().getName());
        sb.append("- SIMPLE FIELDS: ");
        sb.append(new Date().getTime() - start);
        sb.append("ms.\n");

        // ---------------
        // -- CURRENT PHASE
        // ---------------

        start = new Date().getTime();

        final Phase currentPhase = project.getCurrentPhase();
        if (currentPhase != null) {
            pLight.setCurrentPhaseName(currentPhase.getModel().getName());
        }

        sb.append("- CURRENT PHASE: ");
        sb.append(new Date().getTime() - start);
        sb.append("ms.\n");

        // ---------------
        // -- VISIBILITIES
        // ---------------

        start = new Date().getTime();

        final ArrayList<ProjectModelVisibilityDTO> visibilities = new ArrayList<ProjectModelVisibilityDTO>();
        for (final ProjectModelVisibility v : project.getProjectModel().getVisibilities()) {
            final ProjectModelVisibilityDTO vDTO = new ProjectModelVisibilityDTO();
            vDTO.setId(v.getId());
            vDTO.setType(v.getType());
            vDTO.setOrganizationId(v.getOrganization().getId());
            visibilities.add(vDTO);
        }
        pLight.setVisibilities(visibilities);

        sb.append("- VISIBILITIES: ");
        sb.append(new Date().getTime() - start);
        sb.append("ms.\n");

        // ---------------
        // -- ORG UNIT
        // ---------------

        start = new Date().getTime();

        // Fill the org unit.
        orgUnitQuery.setParameter("project", project);

        for (final OrgUnit orgUnit : (List<OrgUnit>) orgUnitQuery.getResultList()) {
            pLight.setOrgUnitName(orgUnit.getName() + " - " + orgUnit.getFullName());
            break;
        }

        sb.append("- ORG UNIT: ");
        sb.append(new Date().getTime() - start);
        sb.append("ms.\n");

        // ---------------
        // -- CATEGORIES
        // ---------------

        start = new Date().getTime();

        categoriesQuery.setParameter("projectId", project.getId());

        final HashSet<CategoryElementDTO> elements = new HashSet<CategoryElementDTO>();

        for (final Value value : (List<Value>) categoriesQuery.getResultList()) {

            choicesQuery.setParameter("ids", ValueResultUtils.splitValuesAsLong(value.getValue()));

            for (final QuestionChoiceElement choice : (List<QuestionChoiceElement>) choicesQuery.getResultList()) {

                final CategoryType parent = choice.getCategoryElement().getParentType();
                final CategoryTypeDTO parentDTO = new CategoryTypeDTO();
                parentDTO.setId(parent.getId());
                parentDTO.setLabel(parent.getLabel());
                parentDTO.setIcon(parent.getIcon());

                final CategoryElement element = choice.getCategoryElement();
                final CategoryElementDTO elementDTO = new CategoryElementDTO();
                elementDTO.setId(element.getId());
                elementDTO.setLabel(element.getLabel());
                elementDTO.setColor(element.getColor());
                elementDTO.setParentCategoryDTO(parentDTO);

                elements.add(elementDTO);
            }
        }
        pLight.setCategoryElements(elements);

        sb.append("- CATEGORIES: ");
        sb.append(new Date().getTime() - start);
        sb.append("ms.\n");

        // ---------------
        // -- CHILDREN
        // ---------------

        start = new Date().getTime();

        final ArrayList<ProjectDTOLight> children = new ArrayList<ProjectDTOLight>();

        // Maps the funding projects.
        if (mapChildren && project.getFunding() != null) {
            for (final ProjectFunding funding : project.getFunding()) {

                final Project pFunding = funding.getFunding();

                if (pFunding != null) {
                    // Recursive call to retrieve the child (without its
                    // children).
                    children.add(map(pFunding, false));
                }
            }
        }

        // Maps the funded projects.
        if (mapChildren && project.getFunded() != null) {
            for (final ProjectFunding funded : project.getFunded()) {

                final Project pFunded = funded.getFunded();

                if (pFunded != null) {
                    // Recursive call to retrieve the child (without its
                    // children).
                    children.add(map(pFunded, false));
                }
            }
        }

        pLight.setChildrenProjects(children);

        
       
        // ------------------
        // -- FAVORITE USERS
        // ------------------
        
       if(project.getFavoriteUsers()!=null)
       {
        Set<UserDTO> favoriteUsesSet = new HashSet<UserDTO>();
        for(User u : project.getFavoriteUsers())
        {
        	//favoriteUsesSet.add(dozerMapper.map(u, UserDTO.class));
        	UserDTO uDTO = new UserDTO();
        	uDTO.setId(u.getId());
        	uDTO.setChangePasswordKey(u.getChangePasswordKey());
        	uDTO.setDateChangePasswordKeyIssued(u.getDateChangePasswordKeyIssued());
        	uDTO.setEmail(u.getEmail());
        	uDTO.setFirstName(u.getFirstName());
        	uDTO.setLocale(u.getLocale());
        	if(u.isActive()==null)
        	{
        	  uDTO.setActive(true);
        	}
        	else
        	{
        	  uDTO.setActive(u.isActive().booleanValue());
        	}
        	
        	favoriteUsesSet.add(uDTO);
        	
        	
        	
        }
        
        pLight.setFavoriteUsers(favoriteUsesSet);
        
       }
       else
       {
    	   pLight.setFavoriteUsers(null);
       }
        
       // ---END----
        
        sb.append("- CHILDREN: ");
        sb.append(new Date().getTime() - start);
        sb.append("ms.\n");

        if (LOG.isDebugEnabled()) {
            LOG.debug(sb.toString());
        }

        return pLight;
    }

}
