package org.sigmah.server.handler;

import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.importation.ImportationSchemeModel;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetImportationSchemeModels;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;

/**
 * {@link GetImportationSchemeModels} command exectution
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr) v2.0
 */
public class GetImportationSchemeModelsHandler extends AbstractCommandHandler<GetImportationSchemeModels, ListResult<ImportationSchemeModelDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ImportationSchemeModelDTO> execute(final GetImportationSchemeModels cmd, final UserExecutionContext context) throws CommandException {

		final TypedQuery<ImportationSchemeModel> query;

		if (cmd.getImportationSchemeId() == null && cmd.getProjectModelId() == null && cmd.getOrgUnitModelId() == null) {
			query = em().createQuery("FROM ImportationSchemeModel", ImportationSchemeModel.class);

		} else {

			final StringBuilder builder = new StringBuilder("SELECT sm FROM ImportationSchemeModel sm WHERE ");

			if (cmd.getImportationSchemeId() != null) {
				builder.append(" sm.importationScheme.id = ").append(cmd.getImportationSchemeId());

			} else if (cmd.getProjectModelId() != null) {
				builder.append(" sm.projectModel.id = ").append(cmd.getProjectModelId());

			} else if (cmd.getOrgUnitModelId() != null) {
				builder.append(" sm.orgUnitModel.id = ").append(cmd.getOrgUnitModelId());
			}

			query = em().createQuery(builder.toString(), ImportationSchemeModel.class);
		}

		final List<ImportationSchemeModel> importationSchemeModelsList = query.getResultList();

		return new ListResult<ImportationSchemeModelDTO>(mapper().mapCollection(importationSchemeModelsList, ImportationSchemeModelDTO.class));
	}

}
