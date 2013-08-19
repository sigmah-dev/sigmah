package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetOrgUnitsByModel;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.OrgUnitListResult;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.OrgUnitDTOLight;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class GetOrgUnitsByModelHandler  implements CommandHandler<GetOrgUnitsByModel> {

    private final static Log LOG = LogFactory.getLog(GetOrgUnitsByModelHandler.class);

    private final EntityManager em;
    private final Mapper mapper;
    private final Injector injector;

    @Inject
    public GetOrgUnitsByModelHandler(EntityManager em, Mapper mapper, Injector injector) {
        this.em = em;
        this.mapper = mapper;
        this.injector=injector;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult execute(GetOrgUnitsByModel cmd, User user) throws CommandException {
    	if(cmd==null || cmd.getOrgUnitModelId()==null)
		{
			return null;
		}
		
		String qlString = "SELECT o from OrgUnit p WHERE o.orgUnitModel.id= :orgUnitId";
		Query query = em.createQuery(qlString);
		query.setParameter("orgUnitId", cmd.getOrgUnitModelId());
	
		List<OrgUnit> orgUnitList = (List<OrgUnit>)query.getResultList();
		List<OrgUnitDTOLight> orgUnitDTOList = new ArrayList<OrgUnitDTOLight>();
		
		if(orgUnitList==null)
		{
			return null;
		}
		
		for(OrgUnit o : orgUnitList)
		{
			orgUnitDTOList.add(mapper.map(o, OrgUnitDTOLight.class));
		}
		
		
		OrgUnitListResult result = new OrgUnitListResult();
		result.setOrgUnitLightDTOList(orgUnitDTOList);
		
		
		return  result;
	}


}
