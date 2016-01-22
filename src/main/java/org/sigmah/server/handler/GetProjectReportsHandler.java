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

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.profile.PrivacyGroup;
import org.sigmah.server.domain.profile.PrivacyGroupPermission;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.domain.report.ProjectReport;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProjectReports;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.report.ReportReference;

/**
 * Handler for {@link GetProjectReports} command
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetProjectReportsHandler extends AbstractCommandHandler<GetProjectReports, ListResult<ReportReference>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ReportReference> execute(final GetProjectReports cmd, final UserExecutionContext context) throws CommandException {

		final List<ReportReference> references = new ArrayList<ReportReference>();

		final TypedQuery<ProjectReport> query;

		if (cmd.getProjectId() != null) {
			query = em().createQuery("SELECT r FROM ProjectReport r WHERE r.project.id = :projectId", ProjectReport.class);
			query.setParameter("projectId", cmd.getProjectId());

		} else if (cmd.getOrgUnitId() != null) {
			query = em().createQuery("SELECT r FROM ProjectReport r WHERE r.orgUnit.id = :orgUnitId", ProjectReport.class);
			query.setParameter("orgUnitId", cmd.getOrgUnitId());

		} else if (cmd.getReportId() != null) {
			query = em().createQuery("SELECT r FROM ProjectReport r WHERE r.id = :reportId", ProjectReport.class);
			query.setParameter("reportId", cmd.getReportId());

		} else {
			throw new IllegalArgumentException("GetProjectReports should either specify a project id or a report id.");
		}

		try {
			final List<ProjectReport> reports = query.getResultList();

			for (final ProjectReport report : reports) {
				if (isViewableByUser(report, context.getUser())) {

					final ReportReference reportRef = new ReportReference();

					reportRef.setId(report.getId());
					reportRef.setName(report.getName());
					reportRef.setLastEditDate(report.getCurrentVersion().getEditDate());
					reportRef.setEditorName(report.getCurrentVersion().getEditorShortName());
					reportRef.setPhaseName(report.getCurrentVersion().getPhaseName());
					if (report.getFlexibleElement() != null) {
						reportRef.setFlexibleElementLabel(report.getFlexibleElement().getLabel());
					}

					references.add(reportRef);
				}
			}

		} catch (NoResultException e) {
			// No reports in the current project
		}

		return new ListResult<ReportReference>(references);
	}

	/***
	 * Checks if the given {@code user} has the right to see the given {@code report}.
	 * 
	 * @param projectReport
	 *          The project report.
	 * @param user
	 *          The user.
	 * @return {@code true} if the user has the right to see the report, {@code false} otherwise.
	 */
	public static boolean isViewableByUser(final ProjectReport projectReport, final User user) {

		final PrivacyGroup documentPG = projectReport.getFlexibleElement() != null ? projectReport.getFlexibleElement().getPrivacyGroup() : null;

		if (documentPG == null) {
			return true;
		}

		for (final Profile profile : user.getOrgUnitWithProfiles().getProfiles()) {
			for (final PrivacyGroupPermission pgp : profile.getPrivacyGroupPermissions()) {
				if (documentPG.equals(pgp.getPrivacyGroup())) {
					return true;
				}
			}
		}

		return false;
	}

}
