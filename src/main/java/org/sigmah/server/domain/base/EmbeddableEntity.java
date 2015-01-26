package org.sigmah.server.domain.base;

import java.io.Serializable;

/**
 * <p>
 * Embeddable entity interface.
 * </p>
 * <p>
 * Embeddable elements must implement {@code hashCode} and {@code equals} methods.<br/>
 * Ex: Composite id (primary key).
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface EmbeddableEntity extends Serializable {

	/**
	 * {@inheritDoc}
	 */
	@Override
	String toString();

	/**
	 * {@inheritDoc}
	 */
	@Override
	int hashCode();

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean equals(Object obj);

}
