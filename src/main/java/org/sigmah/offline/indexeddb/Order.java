package org.sigmah.offline.indexeddb;

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
