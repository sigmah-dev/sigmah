package org.sigmah.shared.computation.instruction;

import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.dependency.Scope;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.dto.ProjectFundingDTO;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public abstract class AbstractScopeFunction implements ScopeFunction {
	
	private String modelName;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * Model name of this scope.
	 * 
	 * @return The model name.
	 */
	public String getModelName() {
		return modelName;
	}
	
	public abstract ProjectFundingDTO.LinkedProjectType getLinkedProjectType();

	@Override
	public Scope toScope() {
		return new Scope(getLinkedProjectType(), modelName);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Stack<ComputedValue> stack, Map<Dependency, ComputedValue> variables) {
		throw new UnsupportedOperationException(toString() + " function is not executable.");
	}
	
}
