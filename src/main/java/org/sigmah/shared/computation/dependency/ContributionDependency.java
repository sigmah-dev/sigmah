package org.sigmah.shared.computation.dependency;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ContributionDependency implements Dependency {
	
	private Scope scope;
	
	private Integer projectModelId;

	public ContributionDependency() {
	}

	public ContributionDependency(Scope scope) {
		this.scope = scope;
	}

	public Scope getScope() {
		return scope;
	}

	@Override
	public boolean isResolved() {
		return scope.getModelName() == null || projectModelId != null;
	}
	
	@Override
	public String toHumanReadableString() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(DependencyVisitor visitor) {
		visitor.visit(this);
	}

	public void setProjectModelId(Integer projectModelId) {
		this.projectModelId = projectModelId;
	}

	public Integer getProjectModelId() {
		return projectModelId;
	}
	
}
