package org.sigmah.server.dao.hibernate;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.sigmah.shared.dao.UserPermissionDAO;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.UserDatabase;
import org.sigmah.shared.domain.UserPermission;

import com.google.inject.Inject;

public class UserPermissionHibernateDAO implements UserPermissionDAO{
	
	final EntityManager em;
	
	@Inject
	public UserPermissionHibernateDAO(EntityManager em){
		this.em=em;
	}
	
	  

	@Override
	public void deleteByUser(int userId) {
		em.createNativeQuery("delete from UserPermission where UserId=?1")
		.setParameter(1, userId).executeUpdate();
		
	}

	@Override
	public void deleteByProject(int projectId) {
		em.createNativeQuery("delete from UserPermission where DatabaseId=?1")
		.setParameter(1, projectId).executeUpdate();
		
	}
	
	@Override
	public void deleteByOrgUnits(List<OrgUnit> orgUnitList) {
		em.createNativeQuery("DELETE FROM UserPermission WHERE partner IN (?1)")
		.setParameter(1, orgUnitList).executeUpdate();
		
	}
	
	@Override
 	public void deleteByProjects(List<Project> projects) {
		em.createQuery(
				"DELETE FROM UserPermission WHERE database IN (?1)")
				.setParameter(1, projects).executeUpdate();

	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Project> getOrgUnitProjects(OrgUnit orgUnit){
		Query query = em.createQuery(
				"SELECT p FROM Project p WHERE :unit MEMBER OF p.partners AND p.projectModel.status != 'DRAFT'"
				);
		query.setParameter("unit", orgUnit);
		return (List<Project>) query.getResultList();
	}

 
	@Override
	public void createAndPersist(User user, OrgUnit orgUnit, Integer projectId) {
		UserPermission permission=new UserPermission((UserDatabase)em.find(UserDatabase.class, projectId),user);
		permission.setPartner(orgUnit);		
		permission.setAllowDesign(true);
		permission.setAllowEdit(true);
		permission.setAllowEditAll(true);
		permission.setAllowManageAllUsers(true);
		permission.setAllowManageUsers(true);
		permission.setAllowView(true);
		permission.setAllowViewAll(true);
		permission.setLastSchemaUpdate(new Date());
		
		persist(permission);
		
	}



	@Override
	public void persist(UserPermission entity) {
		em.persist(entity);
	}



	@Override
	public UserPermission findById(Integer primaryKey) {
		// TODO Auto-generated method stub
		return null;
	}

	 


}
