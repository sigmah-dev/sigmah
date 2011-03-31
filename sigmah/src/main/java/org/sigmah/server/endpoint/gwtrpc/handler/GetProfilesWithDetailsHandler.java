package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetProfilesWithDetails;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ProfileWithDetailsListResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.GlobalPermission;
import org.sigmah.shared.domain.profile.GlobalPermissionEnum;
import org.sigmah.shared.domain.profile.PrivacyGroupPermission;
import org.sigmah.shared.domain.profile.PrivacyGroupPermissionEnum;
import org.sigmah.shared.domain.profile.Profile;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetProfilesWithDetailsHandler implements CommandHandler<GetProfilesWithDetails> {

	private static final Log log = LogFactory.getLog(GetProfilesWithDetailsHandler.class);
	
	private final EntityManager em;
	private final Mapper mapper;
	
	@Inject
    public GetProfilesWithDetailsHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }
			
	@SuppressWarnings("unchecked")
	@Override
	public CommandResult execute(GetProfilesWithDetails cmd, User user)
			throws CommandException {
		List<ProfileDTO> profiles = new ArrayList<ProfileDTO>();
		
		final Query query = em.createQuery("SELECT p FROM Profile p ORDER BY p.id");
		
		final List<Profile> resultProfiles = (List<Profile>) query.getResultList();
		
		if(resultProfiles != null){
			log.debug("Found " + resultProfiles.size() + " profiles!");
			for(final Profile oneProfile : resultProfiles){
				ProfileDTO profile = mapper.map(oneProfile, ProfileDTO.class);
				//Global Permissions
				Set<GlobalPermissionEnum> permissions = new HashSet<GlobalPermissionEnum>();
				for(final GlobalPermission globalPermission : oneProfile.getGlobalPermissions()){
					permissions.add(globalPermission.getPermission());
				}				
				profile.setGlobalPermissions(permissions);
				//Privacy Groups
				Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> privacyGroups = new HashMap<PrivacyGroupDTO, PrivacyGroupPermissionEnum>();
				for(final PrivacyGroupPermission privacyGroupPermission : oneProfile.getPrivacyGroupPermissions()){
					if(privacyGroupPermission.getPrivacyGroup() != null){
						PrivacyGroupDTO privacyGroupDTO = mapper.map(privacyGroupPermission.getPrivacyGroup(), PrivacyGroupDTO.class);
						privacyGroups.put(privacyGroupDTO, privacyGroupPermission.getPermission());
					}				
				}
				profile.setPrivacyGroups(privacyGroups);
				profiles.add(profile);
			}
		}
		
		return new ProfileWithDetailsListResult(profiles);
	}

}
