package org.sigmah.shared.computation.instruction;

import org.sigmah.shared.computation.dependency.Relation;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class FundingSources extends AbstractScopeFunction {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "fundingSources";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Function instantiate() {
		return new FundingSources();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Relation getRelation() {
		return Relation.FUNDING_SOURCES;
	}
	
}
