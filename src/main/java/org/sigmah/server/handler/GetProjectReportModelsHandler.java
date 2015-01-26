package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProjectReportModels;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ModelReference;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Handler for {@link GetProjectReportModels} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class GetProjectReportModelsHandler extends AbstractCommandHandler<GetProjectReportModels, ListResult<ModelReference>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ModelReference> execute(final GetProjectReportModels cmd, final UserExecutionContext context) throws CommandException {

		final ArrayList<ModelReference> references = new ArrayList<ModelReference>();

		final TypedQuery<ProjectReportModel> query = em().createQuery("SELECT r FROM ProjectReportModel r", ProjectReportModel.class);

		try {

			final List<ProjectReportModel> models = query.getResultList();

			for (final ProjectReportModel model : models) {
				references.add(new ModelReference(model));
			}

		} catch (NoResultException e) {
			// No reports in the current project
		}

		return new ListResult<ModelReference>(references);
	}

}
