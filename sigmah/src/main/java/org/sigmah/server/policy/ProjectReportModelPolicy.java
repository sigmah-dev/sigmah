package org.sigmah.server.policy;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.server.policy.admin.ModelUtil;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.report.ProjectReportModel;
import org.sigmah.shared.domain.report.ProjectReportModelSection;
import org.sigmah.shared.dto.report.ProjectReportModelSectionDTO;
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
				
		//get User that need to be saved from properties	
		String name = properties.get(AdminUtil.PROP_REPORT_MODEL_NAME);
		List<ProjectReportModelSectionDTO> sectionsDTO = 
			(List<ProjectReportModelSectionDTO>) properties.get(AdminUtil.PROP_REPORT_SECTION_MODEL);
		log.debug("Sections to save : " + sectionsDTO.size());
		//Save report
		if(name != null){
			
			final Query query = em.createQuery("SELECT r FROM ProjectReportModel r WHERE r.name = :name ORDER BY r.id");
			query.setParameter("name", name);
			try{
				if(query.getSingleResult() != null){
					reportModel = (ProjectReportModel) query.getSingleResult();
					reportModel.setName(name);
					reportModel = em.merge(reportModel);
				}else{
					reportModel = new ProjectReportModel();
					reportModel.setName(name);
					em.persist(reportModel);
				}
			}catch(Exception e){
				reportModel = new ProjectReportModel();
				reportModel.setName(name);
				em.persist(reportModel);
			}
		}
		
		//Save sections
		//List<ProjectReportModelSection> sections = reportModel.getSections();
		int id = 1;
		final Query query = em.createQuery("SELECT r FROM ProjectReportModelSection r");
		if(query.getResultList()!=null)
			id = query.getResultList().size();
		for(ProjectReportModelSectionDTO sectionDTO : sectionsDTO){
			id++;			
			ProjectReportModelSection reportModelSection = mapper.map(sectionDTO, ProjectReportModelSection.class);
			
			if(sectionDTO.getReportModelName()!=null && !sectionDTO.getReportModelName().isEmpty()){
				reportModelSection.setProjectModelId(reportModel.getId());		
			}
			if(reportModelSection.getId() == -1)
				reportModelSection.setId(null);
			/*for(ProjectReportModelSection sectionI : sections){
				if(sectionI.getId().equals(reportModelSection.getId())){
					reportModelSection.setId(sectionI.getId());
					sections.remove(sectionI);
				}
			}*/
			log.debug("In Policy");
			log.debug("Section Id : " + reportModelSection.getId());
			log.debug("Name : " + reportModelSection.getName());
			log.debug("Index : " + reportModelSection.getIndex());
			log.debug("Section nbText : " + reportModelSection.getNumberOfTextarea());
			log.debug("Section parent : " + reportModelSection.getParentSectionModelId());
			log.debug("Section parent name : " + sectionDTO.getParentSectionModelName());
			log.debug("Section report model: " + reportModelSection.getProjectModelId());
			if(reportModelSection.getId() == null){
				if(reportModelSection.getParentSectionModelId() == null){
					em.createNativeQuery("Insert into ProjectReportModelSection (id, name, numberoftextarea, projectmodelid, sort_order) " +
				            "values (:id, :name, :numberoftextarea, :projectmodelid, :sort_order)")
				            .setParameter("id", id)
				            .setParameter("name", reportModelSection.getName())
				            .setParameter("numberoftextarea", reportModelSection.getNumberOfTextarea())
				            .setParameter("projectmodelid", reportModelSection.getProjectModelId())
				            .setParameter("sort_order", reportModelSection.getIndex())
				            .executeUpdate();
				}else{
					em.createNativeQuery("Insert into into ProjectReportModelSection (id, name, numberoftextarea, parentsectionmodelid, sort_order) " +
				            "values (:id, :name, :numberoftextarea, :parentsectionmodelid, :sort_order)")
				            .setParameter("id", id)
				            .setParameter("name", reportModelSection.getName())
				            .setParameter("numberoftextarea", reportModelSection.getNumberOfTextarea())
				            .setParameter("parentsectionmodelid", reportModelSection.getParentSectionModelId())
				            .setParameter("sort_order", reportModelSection.getIndex())
				            .executeUpdate();
				}
				
			}else{
				reportModelSection = em.merge(reportModelSection);
			}
			//em.persist(reportModelSection);
			//sections.add(reportModelSection);
			
		}
		em.flush();
		em.clear();
		reportModel = em.find(ProjectReportModel.class, reportModel.getId());
		//reportModel.setSections(sections);
		//reportModel = em.merge(reportModel);
		
		ReportModelDTO reportModelDTO = null;
		if(reportModel != null){
			reportModelDTO = mapper.map(reportModel, ReportModelDTO.class);
		}
		
		return reportModelDTO;
	}

	@Override
	public void update(User user, Object entityId, PropertyMap changes) {
		// TODO Auto-generated method stub
		
	}

	public BaseModelData createDraft(Map<String, Object> properties) {
		// TODO Auto-generated method stub
		return null;
	}

}
