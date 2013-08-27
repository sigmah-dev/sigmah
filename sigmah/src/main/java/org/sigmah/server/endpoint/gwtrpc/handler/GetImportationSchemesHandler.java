package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.dozer.Mapper;
import org.sigmah.shared.command.GetImportationSchemes;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ImportationSchemeListResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.importation.ImportationScheme;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * {@link GetImportationSchemes} command execution
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * 
 */
public class GetImportationSchemesHandler implements CommandHandler<GetImportationSchemes> {

	protected EntityManager em;
	protected Mapper mapper;

	@Inject
	public GetImportationSchemesHandler(EntityManager em, Mapper mapper) {
		this.em = em;
		this.mapper = mapper;
	}

	@SuppressWarnings("unchecked")
	@Override
	public CommandResult execute(GetImportationSchemes cmd, User user) throws CommandException {
		List<ImportationScheme> schemeToExclude = new ArrayList<ImportationScheme>();
		if (cmd.getExcludeExistent()) {
			Query query = null;
			if (cmd.getProjectModelId() != null) {
				query = em.createQuery("SELECT sm.importationScheme FROM ImportationSchemeModel sm WHERE sm.projectModel.id = :projectModelId");
				query.setParameter("projectModelId", cmd.getProjectModelId());
			} else {
				query = em.createQuery("SELECT sm.importationScheme FROM ImportationSchemeModel sm WHERE sm.orgUnitModel.id = :orgUnitId");
				query.setParameter("orgUnitId", cmd.getOrgUnitModelId());
			}
			schemeToExclude = query.getResultList();
		}
		Query query = em.createQuery("from ImportationScheme");
		List<ImportationScheme> schemasList = query.getResultList();
		List<ImportationSchemeDTO> schemaDTOList = new ArrayList<ImportationSchemeDTO>();
		if (!schemasList.isEmpty()) {
			for (ImportationScheme importationScheme : schemasList) {
				if (!schemeToExclude.contains(importationScheme)) {
					ImportationSchemeDTO importationSchemeDTO = mapper.map(importationScheme,
					                ImportationSchemeDTO.class);
					schemaDTOList.add(importationSchemeDTO);
				}
			}
		}
		ImportationSchemeListResult result = new ImportationSchemeListResult(schemaDTOList);
		return result;
	}

}
