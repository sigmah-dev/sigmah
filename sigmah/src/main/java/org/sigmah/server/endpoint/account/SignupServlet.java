package org.sigmah.server.endpoint.account;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sigmah.server.auth.impl.BCrypt;
import org.sigmah.server.dao.Transactional;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.OrgUnitBanner;
import org.sigmah.shared.domain.OrgUnitModel;
import org.sigmah.shared.domain.Organization;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.GlobalPermission;
import org.sigmah.shared.domain.profile.GlobalPermissionEnum;
import org.sigmah.shared.domain.profile.OrgUnitProfile;
import org.sigmah.shared.domain.profile.Profile;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * 
 * Creates a new organization on the server with an initial owner assigned 
 * an Administrator role. 
 * 
 * @author alexander
 *
 */
@Singleton
public class SignupServlet extends HttpServlet {

	/**
	 * sigmah.properties entry determining whether new organization signup is permitted
	 * for this deployment
	 */
	private static final String ENABLED_PROPERTY = "multi.tenant.signup";

	public static final String END_POINT = "/signup";
	
	private final Provider<EntityManager> entityManager;
	
	/**
	 * Whether new organization signup is permitted for this deployment
	 */
	private final boolean enabled;

	@Inject
	public SignupServlet(Provider<EntityManager> entityManager, Properties config) {
		super();
		this.entityManager = entityManager;
		this.enabled = "enabled".equals(config.getProperty(ENABLED_PROPERTY));
	}

	@Override
	@Transactional
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		if(!enabled) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
		} else {
			try {
				checkThatAccountDoesntAlreadyExist(req);
				createNewOrganization(req);
			} catch(AccountAlreadyExistsException e) {
				resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Account already exists");
			}
		}
	}

	private void checkThatAccountDoesntAlreadyExist(HttpServletRequest req) throws AccountAlreadyExistsException {
		// first check to see if this user already exists
		long userCount = (Long) entityManager.get().createQuery("select count(u) from User u where u.email = :email")
			.setParameter("email", req.getParameter("userEmail"))
			.getSingleResult();
			
		if(userCount != 0) {
			throw new AccountAlreadyExistsException();
		}
	}	

	// Visible for Testing
	void createNewOrganization(HttpServletRequest req) {

		
		Organization org = new Organization();
		org.setName(req.getParameter("organizationName"));
		entityManager.get().persist(org);
		
		OrgUnitModel orgUnitModel = new OrgUnitModel();
		orgUnitModel.setCanContainProjects(true);
		orgUnitModel.setTitle("Modèle de zone d'intervention");
		orgUnitModel.setName("Zone d'Intervention");
		orgUnitModel.setHasBudget(true);
		orgUnitModel.setStatus(ProjectModelStatus.READY);
		orgUnitModel.setOrganization(org);
		entityManager.get().persist(orgUnitModel);
		
		OrgUnit rootOrgUnit = new OrgUnit();
		rootOrgUnit.setOrganization(org);
		rootOrgUnit.setName("HQ");
		rootOrgUnit.setFullName("Empty Headquarters");
		rootOrgUnit.setOrgUnitModel(orgUnitModel);
		org.setRoot(rootOrgUnit);		
		
		entityManager.get().persist(rootOrgUnit);
		
		Profile adminProfile = new Profile();
		adminProfile.setName("Administrator");
		adminProfile.setOrganization(org);
		adminProfile.setGlobalPermissions(createGlobalPermissions(adminProfile));
		entityManager.get().persist(adminProfile);

		
		User user = new User();
		user.setActive(true);
		user.setEmail(req.getParameter("userEmail"));
		user.setName(req.getParameter("userName"));
		user.setHashedPassword(BCrypt.hashpw(req.getParameter("userPass"), BCrypt.gensalt()));
		user.setOrganization(org);
		user.setLocale("fr");
		entityManager.get().persist(user);

		
		OrgUnitProfile orgUnitProfile = new OrgUnitProfile();
		orgUnitProfile.setOrgUnit(rootOrgUnit);
		orgUnitProfile.setProfiles(Arrays.asList(adminProfile));
		orgUnitProfile.setUser(user);

		entityManager.get().persist(orgUnitProfile);
		
		user.setOrgUnitWithProfiles(orgUnitProfile);
	}

	private List<GlobalPermission> createGlobalPermissions(Profile profile) {
		List<GlobalPermission> list = new ArrayList<GlobalPermission>();
		for(GlobalPermissionEnum p : GlobalPermissionEnum.values()) {
			GlobalPermission entity = new GlobalPermission();
			entity.setPermission(p);
			entity.setProfile(profile);
			list.add(entity);
		}
		return list;
	}
	
}
