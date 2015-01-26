package org.sigmah.server.domain.base;

import java.io.Serializable;

/**
 * Domain entities <b>with id</b> interface.
 * 
 * @param <K>
 *          Entity primary key type.
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface EntityId<K extends Serializable> extends Entity {

	/**
	 * Returns the entity id value.
	 * 
	 * @return the entity id value, or {@code null}.
	 */
	K getId();

	/**
	 * Sets the entity id value.
	 * 
	 * @param id
	 *          The new entity id value.
	 */
	void setId(K id);

}
