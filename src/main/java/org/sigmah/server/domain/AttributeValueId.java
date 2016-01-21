package org.sigmah.server.domain;

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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.EmbeddableEntity;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Attribute value id - Composite primary key for {@link AttributeValue}.<br/>
 * Not an entity.
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Embeddable
public class AttributeValueId implements EmbeddableEntity {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8255967204777550586L;

	@Column(name = EntityConstants.SITE_COLUMN_ID, nullable = false)
	@NotNull
	private int siteId;

	@Column(name = EntityConstants.ATTRIBUTE_COLUMN_ID, nullable = false)
	@NotNull
	private int attributeId;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public AttributeValueId() {
		// Required empty constructor.
	}

	public AttributeValueId(int siteId, int attributeId) {
		this.siteId = siteId;
		this.attributeId = attributeId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);

		builder.append("siteId", siteId);
		builder.append("attributeId", attributeId);

		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + attributeId;
		result = prime * result + siteId;
		return result;
	}

	/**
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
		if (!(obj instanceof AttributeValueId)) {
			return false;
		}
		AttributeValueId other = (AttributeValueId) obj;
		if (attributeId != other.attributeId) {
			return false;
		}
		if (siteId != other.siteId) {
			return false;
		}
		return true;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public int getSiteId() {
		return this.siteId;
	}

	public void setSiteId(int siteId) {
		this.siteId = siteId;
	}

	public int getAttributeId() {
		return this.attributeId;
	}

	public void setAttributeId(int attributeId) {
		this.attributeId = attributeId;
	}

}
