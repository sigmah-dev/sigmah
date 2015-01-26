package org.sigmah.server.domain.element;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.util.EntityConstants;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;

/**
 * <p>
 * Default flexible element domain entity.
 * </p>
 * <p>
 * Defines a flexible element which has no proper value but which is directly linked to a property of the project.
 * </p>
 * 
 * @author tmi
 */
@Entity
@Table(name = EntityConstants.DEFAULT_FLEXIBLE_ELEMENT_TABLE)
public class DefaultFlexibleElement extends FlexibleElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8911957237038539783L;

	@Column(name = EntityConstants.DEFAULT_FLEXIBLE_ELEMENT_COLMUN_TYPE, nullable = true)
	@Enumerated(EnumType.STRING)
	private DefaultFlexibleElementType type;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	public boolean isHistorable() {

		if (type != null) {
			switch (type) {
				case OWNER:
					return false;
				default:
					return true;
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendToString(final ToStringBuilder builder) {
		builder.append("type", type);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public DefaultFlexibleElementType getType() {
		return type;
	}

	public void setType(DefaultFlexibleElementType type) {
		this.type = type;
	}
}
