package org.sigmah.client.ui.view.project.dashboard;

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

import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.ProjectFundingDTO.LinkedProjectType;

/**
 * Abstract layer for linked projects view components.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
abstract class LinkedProjectsAbstractProvider {

	/**
	 * The project dashboard view.
	 */
	protected final ProjectDashboardView view;

	/**
	 * Linked projects type.
	 */
	protected final LinkedProjectType projectType;

	protected LinkedProjectsAbstractProvider(final ProjectDashboardView view, final LinkedProjectType projectType) {
		this.view = view;
		this.projectType = projectType;
	}

	/**
	 * Returns the given {@code model} corresponding <em>funding</em> or <em>funded</em> project based on
	 * {@link #projectType} value.
	 * 
	 * @param model
	 *          The model element.
	 * @return The given {@code model} corresponding <em>funding</em> or <em>funded</em> project based on
	 *         {@link #projectType} value.
	 */
	protected final ProjectDTO getProject(final ProjectFundingDTO model) {
		switch (projectType) {

			case FUNDING_PROJECT:
				return model.getFunding();

			case FUNDED_PROJECT:
				return model.getFunded();

			default:
				throw new IllegalArgumentException("Invalid linked project type.");
		}
	}

}
