package org.sigmah.server.handler;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.CheckModelUsage;
import org.sigmah.shared.command.result.BooleanResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.IsModel.ModelType;

/**
 * Handler for {@link CheckModelUsage} command.
 * 
 * @author HUZHE (zhe.hu32@gmail.com)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class CheckModelUsageHandler extends AbstractCommandHandler<CheckModelUsage, BooleanResult> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BooleanResult execute(final CheckModelUsage cmd, final UserExecutionContext context) throws CommandException {

		final Integer modelId = cmd.getModelId();
		final ModelType modelType = cmd.getModelType();

		if (modelId == null || modelType == null) {
			throw new CommandException("Invalid command arguments.");
		}

		final TypedQuery<Number> query;

		switch (modelType) {

			case ProjectModel:
				// Check if the project model is ever used by a project.
				query = em().createQuery("SELECT COUNT(p) FROM Project p WHERE p.projectModel.id =:projectModelId", Number.class);
				break;

			case OrgUnitModel:
				// Check if the OrgUnit model is ever used by an OrgUnit.
				query = em().createQuery("SELECT COUNT(o) FROM OrgUnit o WHERE o.orgUnitModel.id =:orgUnitModelId", Number.class);
				break;

			default:
				throw new UnsupportedOperationException("Invalid model type.");
		}

		return new BooleanResult(query.getSingleResult().intValue() > 0);
	}

}
