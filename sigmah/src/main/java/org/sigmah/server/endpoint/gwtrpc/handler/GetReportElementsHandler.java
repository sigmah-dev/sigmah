/**
 * 
 */
package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetReportElements;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ReportElementsResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;
import org.sigmah.shared.domain.element.ReportElement;
import org.sigmah.shared.domain.element.ReportListElement;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;

import com.google.inject.Inject;

/**
 * @author HUZHE
 *
 */
public class GetReportElementsHandler implements CommandHandler<GetReportElements> {
	
		
	 private EntityManager em;
	    private final Mapper mapper;

	    private static final Log log = LogFactory.getLog(GetReportModelsHandler.class);
	    
	    @Inject
	    public GetReportElementsHandler(EntityManager em, Mapper mapper) {
	        this.em = em;
	        this.mapper = mapper;
	    }
	

	@Override
	public CommandResult execute(GetReportElements cmd, User user)
			throws CommandException {
		
		//Query
		 final Query reportElementsQuery = em.createQuery("From ReportElement");
		 final Query reportListElementsQuery = em.createQuery("From ReportListElement");
		 
		 //Get results
		 @SuppressWarnings("unchecked")
		 List<ReportElement> reportElements = reportElementsQuery.getResultList();
		 @SuppressWarnings("unchecked")
		 List<ReportListElement>reportListElements = reportListElementsQuery.getResultList();
		 
		 List<ReportElementDTO> reportElementsDTOs = new ArrayList<ReportElementDTO>();
		 List<ReportListElementDTO>reportListElementsDTOs = new ArrayList<ReportListElementDTO>();
		 
		 //Mapping
		 for(ReportElement r:reportElements)
		 {
			 ReportElementDTO reportElementDTO = mapper.map(r, ReportElementDTO.class);
			 reportElementDTO.setModelId(r.getModelId());
			 reportElementsDTOs.add(reportElementDTO);
			 log.debug("Id after mapping is ID: "+reportElementDTO.getModelId());
		 }
		
		 for(ReportListElement r:reportListElements)
		 {
			 ReportListElementDTO reportListElementDTO = mapper.map(r, ReportListElementDTO.class);
			 reportListElementDTO.setModelId(r.getModelId());
			 reportListElementsDTOs.add(reportListElementDTO);
			 log.debug("Id after mapping is ID: "+reportListElementDTO.getModelId());
			 
		 }
		
		
		//Return the results
		return new ReportElementsResult(reportElementsDTOs,reportListElementsDTOs);
	}

	

}
