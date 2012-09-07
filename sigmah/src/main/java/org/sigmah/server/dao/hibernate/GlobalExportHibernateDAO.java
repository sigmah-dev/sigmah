/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.dao.hibernate;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Organization;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.export.GlobalExport;
import org.sigmah.shared.domain.export.GlobalExportSettings;

import com.google.inject.Inject;

/*  
 * @author sherzod
 */
public class GlobalExportHibernateDAO implements GlobalExportDAO{

	private final EntityManager em;

	@Inject
	public GlobalExportHibernateDAO(EntityManager em) {
		this.em = em;
	}
	 
	@SuppressWarnings("unchecked")
	@Override
	public List<ProjectModel> getProjectModelsByOrganization(
			Organization organization) {
		Query query = em.createQuery(
				"SELECT pmv.model FROM ProjectModelVisibility pmv WHERE pmv.organization=:org"
				);
		query.setParameter("org", organization);
		return (List<ProjectModel>) query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GlobalExportSettings> getGlobalExportSettings() {
		Query query = em.createQuery("FROM GlobalExportSettings ges"
				);
 		return (List<GlobalExportSettings>) query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public GlobalExportSettings getGlobalExportSettingsByOrganization(Integer id) {
		Query query = em.createQuery(
				"FROM GlobalExportSettings ges WHERE ges.organization.id=:id"
				);
		query.setParameter("id",id);
 		return (GlobalExportSettings) query.getSingleResult();
	}
	
	@Override
	public List<Project> getProjects(List<ProjectModel> pmodels){
		Query query = em.createQuery("FROM Project p WHERE  p.projectModel IN (:pmodels)");
		query.setParameter("pmodels", pmodels);
		return (List<Project>) query.getResultList();
	}
	
	@Override
	public List<GlobalExport> getGlobalExports(Date from,Date to) {
		Query query = em.createQuery("FROM GlobalExport e where e.date between :fromDate and :toDate");
		query.setParameter("fromDate", from);
		query.setParameter("toDate", to);
	
 		return (List<GlobalExport>) query.getResultList();
	}
	
	@Override
	public List<GlobalExport> getOlderExports(Date oldDate,Organization organization) {
		Query query = em.createQuery("FROM GlobalExport e " +
				"WHERE e.organization=:org and e.date <:oldDate ");
		query.setParameter("oldDate", oldDate);
		query.setParameter("org",organization);
		return (List<GlobalExport>) query.getResultList();
		
	}


}
