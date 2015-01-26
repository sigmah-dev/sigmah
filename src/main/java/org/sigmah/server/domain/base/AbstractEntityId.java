package org.sigmah.server.domain.base;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Abstract entity <b>with id</b>, parent class of all domain entities possessing a primary key.
 * 
 * @param <K>
 *          Entity primary key type.
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@MappedSuperclass
public abstract class AbstractEntityId<K extends Serializable> extends AbstractEntity implements EntityId<K> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8178013236450058302L;

	/**
	 * <p>
	 * <em><b>Entity {@code id} property has already been appended to the {@code builder}.</b></em>
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		// Default implementation does nothing. Override this method to append specific properties.
	}

	/**
	 * <p>
	 * <b><em>Default {@code hashCode} method only relies on {@code id} property.</em></b>
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	/**
	 * <p>
	 * <b><em>Default {@code equals} method only relies on {@code id} property.</em></b>
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!obj.getClass().equals(getClass())) {
			return false;
		}
		AbstractEntityId<?> other = (AbstractEntityId<?>) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

}
