package org.sigmah.shared.computation.dependency;

import org.sigmah.shared.computation.instruction.Instructions;
import org.sigmah.shared.util.ValueResultUtils;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ContributionDependency implements Dependency {
	
	public static final String REFERENCE = "@contribution";
	
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
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder()
				.append(scope.getLinkedProjectTypeName())
				.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR);
		
		if (projectModelId != null) {
			stringBuilder.append(Instructions.ID_PREFIX).append(projectModelId);
		} else {
			stringBuilder.append(scope.getModelName());
		}
		
		stringBuilder.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR)
				.append(REFERENCE);
		
		return stringBuilder.toString();
	}
	
	@Override
	public String toHumanReadableString() {
		return new StringBuilder()
				.append(scope.getLinkedProjectTypeName())
				.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR)
				.append(scope.getModelName())
				.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR)
				.append(REFERENCE)
				.toString();
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
