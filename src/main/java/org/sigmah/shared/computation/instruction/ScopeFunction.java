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

import org.sigmah.shared.computation.dependency.Scope;

/**
 * Defines a scope function.
 * 
 * This type of function is used to scope the arguments of an other function.
 * It does nothing on its own and is used only to parse the format defined by
 * Olivier Sarrat (osarrat@urd.org).
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface ScopeFunction extends Function {
	
	/**
	 * Defines the project model name used in the scope.
	 * 
	 * @param modelName 
	 *			Name of the project model to use.
	 */
	void setModelName(String modelName);
	
	/**
	 * Creates a new scope defined by the state of this function.
	 * 
	 * @return A new scope.
	 */
	Scope toScope();
}
