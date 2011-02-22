package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetPrivacyGroups;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.PrivacyGroupsListResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.PrivacyGroup;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetPrivacyGroupsHandler implements CommandHandler<GetPrivacyGroups> {

	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(GetPrivacyGroupsHandler.class);
	
	private final EntityManager em;
	private final Mapper mapper;
	
	@Inject
    public GetPrivacyGroupsHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }
			
	@SuppressWarnings("unchecked")
	@Override
	public CommandResult execute(GetPrivacyGroups cmd, User user)
			throws CommandException {
		List<PrivacyGroupDTO> privacyGroups = new ArrayList<PrivacyGroupDTO>();
		
		final Query query = em.createQuery("SELECT p FROM PrivacyGroup p ORDER BY p.id");
		
		final List<PrivacyGroup> resultPrivacyGroups = (List<PrivacyGroup>) query.getResultList();
		
		if(resultPrivacyGroups != null){
			for(final PrivacyGroup onePrivacyGroup : resultPrivacyGroups){
				PrivacyGroupDTO privacyGroupDTO = mapper.map(onePrivacyGroup, PrivacyGroupDTO.class);				
				privacyGroups.add(privacyGroupDTO);
			}
		}
		
		return new PrivacyGroupsListResult(privacyGroups);
	}

}
