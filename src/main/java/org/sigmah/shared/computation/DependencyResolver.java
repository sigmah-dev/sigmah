package org.sigmah.shared.computation;

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

import org.sigmah.shared.computation.dependency.Dependency;

/**
 * Describes a resolver of computation dependencies external to a model.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public interface DependencyResolver {
	
	/**
	 * Resolve every unresolved dependencies of the given computation.
	 * 
	 * @param computation 
	 *			Computation to resolve.
	 */
	void resolve(Computation computation);
	
	/**
	 * Resolve the given dependency.
	 * 
	 * @param dependency 
	 *			Dependency to resolve.
	 */
	void resolve(Dependency dependency);
}
