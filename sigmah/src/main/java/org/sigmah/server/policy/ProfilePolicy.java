package org.sigmah.server.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.GlobalPermission;
import org.sigmah.shared.domain.profile.GlobalPermissionEnum;
import org.sigmah.shared.domain.profile.PrivacyGroup;
import org.sigmah.shared.domain.profile.PrivacyGroupPermission;
import org.sigmah.shared.domain.profile.PrivacyGroupPermissionEnum;
import org.sigmah.shared.domain.profile.Profile;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import com.google.inject.Inject;

/**
 * Handler for updating profile command.
 * 
 * @author nrebiai
 * 
 */
public class ProfilePolicy implements EntityPolicy<Profile>  {

    private final EntityManager em;

    @SuppressWarnings("unused")
	private final static Log log = LogFactory.getLog(ProfilePolicy.class);

    @Inject
    public ProfilePolicy(EntityManager em) {
        this.em = em;
    }

    @Override
    public Object create(User user, PropertyMap properties) {

    	Profile profileFound = null;
    	Profile profileToPersist = new Profile();
    	List<GlobalPermission> gps = new ArrayList<GlobalPermission>();
    	List<PrivacyGroupPermission> pgs = new ArrayList<PrivacyGroupPermission>();
    	
        ProfileDTO profileDTO = (ProfileDTO)properties.get("profile");
        Set<GlobalPermissionEnum> gpEnumList = profileDTO.getGlobalPermissions();
        Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> privacyGroupsPerms = profileDTO.getPrivacyGroups();
        
        if(profileDTO.getId() > 0){
        	profileFound = em.find(Profile.class, profileDTO.getId());
        	for(GlobalPermission globalPerm : profileFound.getGlobalPermissions()){
        		em.remove(globalPerm);
        	}
        	for(PrivacyGroupPermission privacyGroupPerm : profileFound.getPrivacyGroupPermissions()){
        		em.remove(privacyGroupPerm);
        	}
        }
        	
        if(profileFound != null)
        	profileToPersist = profileFound;
        
        if(profileDTO.getName() != null )
        	profileToPersist.setName(profileDTO.getName()); 
        
            
        for(GlobalPermissionEnum gpEnum : gpEnumList){
        	GlobalPermission gpToPersist = new GlobalPermission();
    		gpToPersist.setPermission(gpEnum);
    		gpToPersist.setProfile(profileToPersist);  
    		gps.add(gpToPersist);         
        }

        profileToPersist.setGlobalPermissions(gps);
        
        for(Entry<PrivacyGroupDTO, PrivacyGroupPermissionEnum> p : privacyGroupsPerms.entrySet()){
        	
        	PrivacyGroupPermission pgp = new PrivacyGroupPermission();
        	
        	PrivacyGroup privacyGroup = em.find(PrivacyGroup.class, p.getKey().getId());
        	pgp.setPermission(p.getValue());      	
        	pgp.setPrivacyGroup(privacyGroup);
        	pgp.setProfile(profileToPersist);
        	pgs.add(pgp);
        	
        }
        profileToPersist.setPrivacyGroupPermissions(pgs);   
        
        profileToPersist.setOrganization(user.getOrganization());
        if(profileFound != null){       	
            //update profile
        	
            profileToPersist = em.merge(profileToPersist);
        }else{
        	em.persist(profileToPersist);
        }
        
        /*profileDTO = mapper.map(profileToPersist, ProfileDTO.class);      

        ProfileWithDetailsListResult result = new ProfileWithDetailsListResult();
        if(profileDTO != null){       	
        	result.getList().add(profileDTO);
        }*/

        return profileDTO;
    }
    
    @SuppressWarnings("unused")
	private GlobalPermission findGlobalPermission(GlobalPermissionEnum gp, Profile p){
    	GlobalPermission gpFound= null;
    	
    	final Query query = em.createQuery("SELECT g FROM GlobalPermission g WHERE g.profile = :profile AND g.permission = :permission ORDER BY p.id");
		query.setParameter("profile", p);
		query.setParameter("permission", gp);
		
		if(query.getSingleResult() != null){
			gpFound = (GlobalPermission) query.getSingleResult();
		}
    	
    	return gpFound;
    }
    
    @SuppressWarnings("unused")
	private PrivacyGroupPermission findPrivacyGroupPermission(PrivacyGroup pg, Profile p){
    	PrivacyGroupPermission pgFound= null;
    	
    	final Query query = em.createQuery("SELECT p FROM PrivacyGroupPermission p WHERE p.profile = :profile AND pg.privacyGroup = :privacyGroup ORDER BY p.id");
		query.setParameter("profile", p);
		query.setParameter("privacyGroup", pg);
		
		if(query.getSingleResult() != null){
			pgFound = (PrivacyGroupPermission) query.getSingleResult();
		}
    	
    	return pgFound;
    }

	@Override
	public void update(User user, Object entityId, PropertyMap changes) {
		// TODO Auto-generated method stub
		
	}

}
