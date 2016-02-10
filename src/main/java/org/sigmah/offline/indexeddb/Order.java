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

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public enum Order {
	ASCENDING {
		@Override
		public String toString() {
			return IDBCursor.ORDER_ASCENDING;
		}
		
		
	}, DESCENDING {
		@Override
		public String toString() {
			return IDBCursor.ORDER_DESCENDING;
		}
		
	}, UNIQUE_ASCENDING {
		@Override
		public String toString() {
			return IDBCursor.ORDER_UNIQUE_ASCENDING;
		}
		
	}, UNIQUE_DESCENDING {
		@Override
		public String toString() {
			return IDBCursor.ORDER_UNIQUE_DESCENDING;
		}
	};
	
}
