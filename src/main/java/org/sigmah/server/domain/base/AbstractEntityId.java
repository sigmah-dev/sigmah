package org.sigmah.server.domain.base;

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
