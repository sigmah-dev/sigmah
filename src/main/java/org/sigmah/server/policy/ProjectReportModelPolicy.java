package org.sigmah.server.policy;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.report.ProjectReportModel;
import org.sigmah.shared.dto.report.ReportModelDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.inject.Inject;
/**
 * Create project report model policy.
 * 
 * @author nrebiai
 * 
 */
public class ProjectReportModelPolicy implements EntityPolicy<ProjectReportModel> {
	
	private final Mapper mapper;
	private final EntityManager em;
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(ProjectReportModelPolicy.class);
	
	
	@Inject
    public ProjectReportModelPolicy(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

	@Override
	public Object create(User executingUser, PropertyMap properties) {

		ProjectReportModel reportModel  = null;
				
		//get report name
		String name = properties.get(AdminUtil.PROP_REPORT_MODEL_NAME);
				
		final Query query = em
				.createQuery("SELECT r FROM ProjectReportModel r WHERE r.name = :name "
						+ " and r.organization.id = :orgid ORDER BY r.id");
		query.setParameter("orgid", executingUser.getOrganization().getId());
		query.setParameter("name", name);
		try {
			if (query.getSingleResult() != null) {
				// Report model already exists,return the model
				reportModel = (ProjectReportModel) query.getSingleResult();
				reportModel.setOrganization(executingUser.getOrganization());
				reportModel.setName(name);
				reportModel = em.merge(reportModel);
			} else {
				// Create a new report model
				reportModel = new ProjectReportModel();
				reportModel.setName(name);
				reportModel.setOrganization(executingUser.getOrganization());
				em.persist(reportModel);
			}
		} catch (Exception e) {
			reportModel = new ProjectReportModel();
			reportModel.setName(name);
			reportModel.setOrganization(executingUser.getOrganization());
			em.persist(reportModel);
		}
		
		//Commit the changes
		em.flush();
		
		final Query query1 = em
		.createQuery("SELECT r FROM ProjectReportModel r WHERE r.name = :name "
				+ " ORDER BY r.id");
		query1.setParameter("name", name);
		
		ProjectReportModel newReportModel = (ProjectReportModel) query1.getSingleResult();
	
	    ReportModelDTO reportModelDTOToReturned = mapper.map(reportModel, ReportModelDTO.class);
		reportModelDTOToReturned.setId(newReportModel.getId());
		return  reportModelDTOToReturned;
		
	
	
	}
	
		
		

 

	@Override
	public void update(User user, Object entityId, PropertyMap changes) {
		
		
		
		
	}

	public BaseModelData createDraft(Map<String, Object> properties) {
		// TODO Auto-generated method stub
		return null;
	}

}
