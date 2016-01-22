package org.sigmah.server.service;

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

import javax.persistence.Query;

import org.sigmah.client.util.AdminUtil;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.report.ReportModelDTO;

import com.google.inject.Singleton;

/**
 * Create project report model policy.
 * 
 * @author nrebiai
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectReportModelService extends AbstractEntityService<ProjectReportModel, Integer, ReportModelDTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectReportModel create(PropertyMap properties, final UserExecutionContext context) {

		final User executingUser = context.getUser();
		ProjectReportModel reportModel = null;

		// get report name
		final String name = properties.get(AdminUtil.PROP_REPORT_MODEL_NAME);

		final Query query = em().createQuery("SELECT r FROM ProjectReportModel r WHERE r.name = :name and r.organization.id = :orgid ORDER BY r.id");
		query.setParameter("orgid", executingUser.getOrganization().getId());
		query.setParameter("name", name);

		try {

			if (query.getSingleResult() != null) {
				// Report model already exists,return the model
				reportModel = (ProjectReportModel) query.getSingleResult();
				reportModel.setOrganization(executingUser.getOrganization());
				reportModel.setName(name);
				reportModel = em().merge(reportModel);

			} else {
				// Create a new report model
				reportModel = new ProjectReportModel();
				reportModel.setName(name);
				reportModel.setOrganization(executingUser.getOrganization());
				em().persist(reportModel);
			}

		} catch (Exception e) {
			reportModel = new ProjectReportModel();
			reportModel.setName(name);
			reportModel.setOrganization(executingUser.getOrganization());
			em().persist(reportModel);
		}

		// Commit the changes
		em().flush();

		return reportModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectReportModel update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) {
		throw new UnsupportedOperationException("No policy update operation implemented for '" + entityClass.getSimpleName() + "' entity.");
	}

}
