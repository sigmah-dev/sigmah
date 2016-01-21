package org.sigmah.server.dao.base;

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

import java.io.Serializable;

import org.sigmah.server.domain.base.Entity;

/**
 * DAO interface for DAO manipulating a layout.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface LayoutDAO<E extends Entity, K extends Serializable> extends DAO<E, K> {

	/**
	 * Retrieves the parent entity referencing the given {@code flexibleElementId}.
	 * 
	 * @param flexibleElementId
	 *          The flexible element id.
	 * @return The parent entity referencing the given {@code flexibleElementId}, or {@code null}.
	 */
	E findFromFlexibleElement(final Integer flexibleElementId);

}
