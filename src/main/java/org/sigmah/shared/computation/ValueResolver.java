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

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collection;
import java.util.Map;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.value.ComputedValue;

/**
 * Retrieve values of the dependencies of a <code>Computation</code> element.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public interface ValueResolver {
	
	/**
	 * Resolve the values of the given elements.
	 * 
	 * @param elements Elements to resolve.
	 * @param containerId Identifier of the container (project or orgunit).
	 * @param callback To be called when every value has been retrieved.
	 */
	void resolve(Collection<Dependency> elements, int containerId, AsyncCallback<Map<Dependency, ComputedValue>> callback);
	
}
