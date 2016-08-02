package org.sigmah.shared.computation.dependency;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ContributionDependency implements Dependency {
	
	private Scope scope;

	public ContributionDependency() {
	}

	public ContributionDependency(Scope scope) {
		this.scope = scope;
	}

	@Override
	public String toHumanReadableString() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visitBy(DependencyVisitor visitor) {
		visitor.visit(this);
	}
}
