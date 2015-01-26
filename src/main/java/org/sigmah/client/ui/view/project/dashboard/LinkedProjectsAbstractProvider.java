package org.sigmah.client.ui.view.project.dashboard;

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
