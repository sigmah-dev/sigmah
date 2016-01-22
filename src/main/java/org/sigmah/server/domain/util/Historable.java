package org.sigmah.server.domain.util;

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

import org.sigmah.server.domain.base.EntityId;

/**
 * Determines if an element can history of its values.
 * 
 * @author tmi
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public interface Historable {

	/**
	 * If the element manages an history.
	 * 
	 * @return If the element manages an history.
	 */
	boolean isHistorable();

	/**
	 * Transforms a input value as an historable value.
	 * 
	 * @param value
	 *          The actual value.
	 * @return The historable value.
	 */
	String asHistoryToken(String value);

	/**
	 * Transforms a input value as an historable value.
	 * 
	 * @param value
	 *          The actual value.
	 * @return The historable value.
	 */
	String asHistoryToken(EntityId<?> value);

}
