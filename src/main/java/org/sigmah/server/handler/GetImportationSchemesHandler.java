package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.importation.ImportationScheme;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetImportationSchemes;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

/**
 * {@link GetImportationSchemes} command execution.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr) (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 */
public class GetImportationSchemesHandler extends AbstractCommandHandler<GetImportationSchemes, ListResult<ImportationSchemeDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ImportationSchemeDTO> execute(final GetImportationSchemes cmd, final UserExecutionContext context) throws CommandException {

		final List<ImportationScheme> schemeToExclude = new ArrayList<ImportationScheme>();

		if (cmd.getExcludeExistent()) {

			final TypedQuery<ImportationScheme> query;

			if (cmd.getProjectModelId() != null) {
				query =
						em().createQuery("SELECT sm.importationScheme FROM ImportationSchemeModel sm WHERE sm.projectModel.id = :projectModelId", ImportationScheme.class);
				query.setParameter("projectModelId", cmd.getProjectModelId());

			} else {
				query = em().createQuery("SELECT sm.importationScheme FROM ImportationSchemeModel sm WHERE sm.orgUnitModel.id = :orgUnitId", ImportationScheme.class);
				query.setParameter("orgUnitId", cmd.getOrgUnitModelId());
			}

			schemeToExclude.addAll(query.getResultList());
		}

		final TypedQuery<ImportationScheme> query = em().createQuery("FROM ImportationScheme sm", ImportationScheme.class);
		final List<ImportationScheme> schemasList = query.getResultList();

		final List<ImportationSchemeDTO> schemaDTOList = new ArrayList<ImportationSchemeDTO>();
		if (!schemasList.isEmpty()) {
			for (final ImportationScheme importationScheme : schemasList) {
				if (!schemeToExclude.contains(importationScheme)) {
					schemaDTOList.add(mapper().map(importationScheme, ImportationSchemeDTO.class));
				}
			}
		}

		return new ListResult<ImportationSchemeDTO>(schemaDTOList);
	}

}
