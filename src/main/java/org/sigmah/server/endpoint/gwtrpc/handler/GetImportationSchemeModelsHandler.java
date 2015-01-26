package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.dozer.Mapper;
import org.sigmah.shared.command.GetImportationSchemeModels;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ImportationSchemeModelListResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.importation.ImportationSchemeModel;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * {@link GetImportationSchemeModels} command exectution
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * 
 */
public class GetImportationSchemeModelsHandler implements CommandHandler<GetImportationSchemeModels> {

	protected EntityManager em;
	protected Mapper mapper;

	@Inject
	public GetImportationSchemeModelsHandler(EntityManager em, Mapper mapper) {
		this.em = em;
		this.mapper = mapper;
	}

	@Override
	public CommandResult execute(GetImportationSchemeModels cmd, User user) throws CommandException {
		final Query query;
		if (cmd.getImportationSchemeId() == null && cmd.getProjectModelId() == null && cmd.getOrgUnitModelId() == null) {
			query = em.createQuery("FROM ImportationSchemeModel");
		} else {
			String queryString = "SELECT sm FROM ImportationSchemeModel sm WHERE ";
			if (cmd.getImportationSchemeId() != null) {
				queryString += "sm.importationScheme.id = " + cmd.getImportationSchemeId();
			} else {
				if (cmd.getProjectModelId() != null) {
					queryString += "sm.projectModel.id = " + cmd.getProjectModelId();
				} else if (cmd.getOrgUnitModelId() != null) {
					queryString += "sm.orgUnitModel.id = " + cmd.getOrgUnitModelId();
				}
			}
			query = em.createQuery(queryString);
		}

		@SuppressWarnings("unchecked")
		List<ImportationSchemeModel> importationSchemeModelsList = query.getResultList();
		List<ImportationSchemeModelDTO> importationSchemeModelDTOsList = new ArrayList<ImportationSchemeModelDTO>();
		if (!importationSchemeModelsList.isEmpty()) {
			for (ImportationSchemeModel schema : importationSchemeModelsList) {
				ImportationSchemeModelDTO importationSchemeModelDTO = mapper.map(schema,
				                ImportationSchemeModelDTO.class);
				importationSchemeModelDTOsList.add(importationSchemeModelDTO);
			}
		}
		ImportationSchemeModelListResult result = new ImportationSchemeModelListResult(importationSchemeModelDTOsList);
		return result;
	}

}
