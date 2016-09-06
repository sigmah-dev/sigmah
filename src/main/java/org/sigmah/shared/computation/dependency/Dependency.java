package org.sigmah.shared.computation.dependency;

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

import org.sigmah.shared.computation.instruction.HasHumanReadableFormat;
import org.sigmah.shared.util.Visitable;

/**
 * Describes a dependency to a variable value.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public interface Dependency extends Visitable<DependencyVisitor>, HasHumanReadableFormat {
	
	/**
	 * Returns <code>true</code> if this dependency is resolved and can be used
	 * client side, <code>false</code> otherwise. <br>
	 * <br>
	 * Unresolved dependency should be resolved using a
	 * {@link org.sigmah.shared.computation.DependencyResolver} server-side.
	 * 
	 * @return 
	 */
	boolean isResolved();
}
