package org.sigmah.offline.indexeddb;

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

/**
 * A store is like an IndexedDB table. Each store is made to save one type of
 * object.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface Schema {
	
	/**
	 * Returns <code>true</code> if the store uses an auto-incremented value
	 * for its identifier.
	 * 
	 * @return <code>true</code> to auto-increment, <code>false</code> otherwise.
	 */
	boolean isAutoIncrement();
	
	/**
	 * Returns <code>true</code> if this store should still be used.
	 * <p>
	 * A disabled store will be removed from the user database.
	 * </p>
	 * 
	 * @return <code>true</code> if this store is enabled, <code>false</code>
	 * if it is disabled.
	 */
	boolean isEnabled();
	
	/**
	 * Returns the indexes required by this store.
	 * <p>
	 * Each entry should be &lt;name&gt; =&gt; &lt;path&gt;.
	 * </p>
	 * 
	 * @return A map of every index for this store.
	 */
	Map<String, String> getIndexes();
	
}
