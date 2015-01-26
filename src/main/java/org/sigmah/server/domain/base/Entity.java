package org.sigmah.server.domain.base;

import java.io.Serializable;

/**
 * <p>
 * Domain entities interface.
 * </p>
 * <p>
 * Each domain entity must implement {@code hashCode} and {@code equals} methods.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface Entity extends Serializable {

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
