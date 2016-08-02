package org.sigmah.shared.computation.instruction;

import org.sigmah.shared.computation.dependency.Relation;

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
	public Relation getRelation() {
		return Relation.FUNDED_PROJECTS;
	}
	
}
