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

import org.sigmah.server.dao.ReportDefinitionDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetReportDef;
import org.sigmah.shared.command.result.StringResult;
import org.sigmah.shared.dispatch.CommandException;

import com.google.inject.Inject;

/**
 * Handler for {@link GetReportDef} command
 * 
 * @author Alex Bertram (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 */
public class GetReportDefHandler extends AbstractCommandHandler<GetReportDef, StringResult> {

	protected ReportDefinitionDAO reportDAO;

	@Inject
	public void setReportDAO(ReportDefinitionDAO dao) {
		this.reportDAO = dao;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StringResult execute(final GetReportDef cmd, final UserExecutionContext context) throws CommandException {
		return new StringResult(reportDAO.findById(cmd.getId()).getXml());
	}

}
