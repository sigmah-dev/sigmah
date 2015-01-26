package org.sigmah.server.handler;

import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetReportModels;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.report.ReportModelDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.persist.Transactional;

/**
 * Handler for {@link GetReportModels} command
 * 
 * @author nrebiai (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetReportModelsHandler extends AbstractCommandHandler<GetReportModels, ListResult<ReportModelDTO>> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetReportModelsHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ReportModelDTO> execute(final GetReportModels cmd, final UserExecutionContext context) throws CommandException {

		List<ProjectReportModel> models =
				findReportModels(context);

		final List<ReportModelDTO> reportsDTO = mapper().mapCollection(models, ReportModelDTO.class);

		LOG.debug("Found {} report models.", reportsDTO.size());

		return new ListResult<ReportModelDTO>(reportsDTO);
	}

	/**
	 * Find all the project models visible by the current user.
	 * <p/>
	 * The query is made in a transaction.
	 * @param context Execution context.
	 * @return The list of all visibles project report models.
	 */
	@Transactional
	protected List<ProjectReportModel> findReportModels(final UserExecutionContext context) {
		final TypedQuery<ProjectReportModel> query =
			em().createQuery("SELECT r FROM ProjectReportModel r WHERE r.organization.id = :orgid ORDER BY r.id", ProjectReportModel.class);
		
		query.setParameter("orgid", context.getUser().getOrganization().getId());
		
		return query.getResultList();
	}

}
