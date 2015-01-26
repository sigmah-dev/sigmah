package org.sigmah.server.dao.base;

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
