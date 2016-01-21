package org.sigmah.server.handler;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetAvailableStatusForModel;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.IsModel.ModelType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * Handler for {@link GetAvailableStatusForModel} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetAvailableStatusForModelHandler extends AbstractCommandHandler<GetAvailableStatusForModel, ListResult<ProjectModelStatus>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ProjectModelStatus> execute(final GetAvailableStatusForModel cmd, final UserExecutionContext context) throws CommandException {

		final ProjectModelStatus status = cmd.getStatus();
		final Integer modelId = cmd.getModelId();
		final ModelType modelType = cmd.getModelType();

		if (modelId == null || modelType == null) {
			throw new CommandException("Invalid command arguments.");
		}

		final List<ProjectModelStatus> availableStatus = new ArrayList<ProjectModelStatus>();

		if (status == null) {
			return new ListResult<ProjectModelStatus>(availableStatus);
		}

		switch (status) {

			case DRAFT:
				availableStatus.add(ProjectModelStatus.DRAFT);

				if (!isTopOrgUnitModel(modelType, modelId)) {
					// Manages the case of the default root OrgUnit's model.
					availableStatus.add(ProjectModelStatus.READY);
					availableStatus.add(ProjectModelStatus.UNAVAILABLE);
				}
				break;

			case READY:
				availableStatus.add(ProjectModelStatus.DRAFT);
				availableStatus.add(ProjectModelStatus.READY);
				availableStatus.add(ProjectModelStatus.UNAVAILABLE);
				break;

			case USED:
				availableStatus.add(ProjectModelStatus.USED);
				availableStatus.add(ProjectModelStatus.UNAVAILABLE);
				break;

			case UNAVAILABLE:
				availableStatus.add(ProjectModelStatus.UNAVAILABLE);

				final TypedQuery<Number> query = buildCountQuery(modelType, modelId);
				final boolean used = query.getSingleResult().intValue() > 0;

				if (used) {
					availableStatus.add(ProjectModelStatus.USED);
				} else {
					availableStatus.add(ProjectModelStatus.READY);
				}
				break;
		}

		return new ListResult<ProjectModelStatus>(availableStatus);
	}

	/**
	 * Returns if the given OrgUnit's {@code modelId} is referenced by a top-parent OrgUnit.<br>
	 * If the given {@code modelType} is not {@link ModelType#OrgUnitModel}, the method returns {@code false}.
	 * 
	 * @param modelType
	 *          The model type.
	 * @param modelId
	 *          The model id.
	 * @return {@code true} if the given OrgUnit's {@code modelId} is referenced by a top-parent OrgUnit, {@code false}
	 *         otherwise.
	 */
	private boolean isTopOrgUnitModel(final ModelType modelType, final Integer modelId) {

		if (modelType != ModelType.OrgUnitModel || modelId == null) {
			return false;
		}

		final String queryStr = "SELECT COUNT(o) FROM OrgUnit o WHERE o.orgUnitModel.id = :modelId AND o.parentOrgUnit IS NULL";
		final TypedQuery<Number> query = em().createQuery(queryStr, Number.class);
		query.setParameter("modelId", modelId);

		return query.getSingleResult().intValue() > 0;
	}

	/**
	 * Builds the query counting the number of Projects/OrgUnits referencing the given {@code modelId}.
	 * 
	 * @param modelType
	 *          The model type.
	 * @param modelId
	 *          The model id.
	 * @return The query counting the number of Projects/OrgUnits referencing the given {@code modelId}.
	 */
	private TypedQuery<Number> buildCountQuery(final ModelType modelType, final Integer modelId) {

		final TypedQuery<Number> query;

		switch (modelType) {

			case ProjectModel:
				query = em().createQuery("SELECT COUNT(p) FROM Project p WHERE p.projectModel.id = :modelId", Number.class);
				break;

			case OrgUnitModel:
				query = em().createQuery("SELECT COUNT(o) FROM OrgUnit o WHERE o.orgUnitModel.id = :modelId", Number.class);
				break;

			default:
				throw new UnsupportedOperationException("Invalid model type.");
		}

		return query.setParameter("modelId", modelId);
	}

}
