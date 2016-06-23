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

/**
 * Deleteable entities should implement this interface.
 * 
 * @author Alex Bertram
 */
public interface Deleteable {

	/**
	 * Marks this current element as deleted.
	 * The row is not removed from the database.
	 */
	void delete();

	/**
	 * Returns if the current element has been deleted.
	 * 
	 * @return {@code true} if the current element has been deleted, {@code false} otherwise.
	 */
	boolean isDeleted();

}
