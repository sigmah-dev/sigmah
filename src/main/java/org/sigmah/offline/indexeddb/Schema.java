package org.sigmah.offline.indexeddb;

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
