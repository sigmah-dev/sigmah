package org.sigmah.shared.computation.instruction;

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

import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.dependency.Scope;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.dto.ProjectFundingDTO;

/**
 * Base class of scope functions.
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
