package org.sigmah.shared.computation.instruction;

import org.sigmah.shared.dto.ProjectFundingDTO;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class FundedProjects extends AbstractScopeFunction {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "fundedProjects";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function instantiate() {
		return new FundedProjects();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectFundingDTO.LinkedProjectType getLinkedProjectType() {
		return ProjectFundingDTO.LinkedProjectType.FUNDED_PROJECT;
	}

}
