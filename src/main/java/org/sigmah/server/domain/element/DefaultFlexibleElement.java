package org.sigmah.server.domain.element;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
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
	
	/**
	 * Returns the value of this element for the given project.
	 * 
	 * @param container Project to read.
	 * @return Value as a string of this element for the given project.
	 */
	public String getValue(Project container) {
		if(container == null) {
			return null;
		}
		
		final String valueAsString;
		
		switch(type) {
			// Project code.
			case CODE:
				valueAsString = container.getName();
				break;
				
			// Project title.
			case TITLE:
				valueAsString = container.getFullName();
				break;

			case START_DATE:
				valueAsString = container.getStartDate() != null ? Long.toString(container.getStartDate().getTime()) : null;
				break;
				
			case END_DATE:
				valueAsString = container.getEndDate() != null ? Long.toString(container.getEndDate().getTime()) : null;
				break;
				
			case COUNTRY:
				valueAsString = container.getCountry() != null && container.getCountry().getId() != null ? container.getCountry().getId().toString() : null;
				break;
				
			case OWNER:
				valueAsString = User.getUserCompleteName(container.getOwner());
				break;
				
			case MANAGER:
				valueAsString = container.getManager() != null && container.getManager().getId() != null ? container.getManager().getId().toString() : null;
				break;
				
			case ORG_UNIT:
				final OrgUnit orgUnit = container.getPartners() != null && !container.getPartners().isEmpty() ? container.getPartners().iterator().next() : null;
				valueAsString = orgUnit != null && orgUnit.getId() != null ? orgUnit.getId().toString() : null;
				break;
				
			default:
				valueAsString = null;
				break;
		}
		
		if(valueAsString != null && !valueAsString.isEmpty()) {
			return valueAsString;
		} else {
			return null;
		}
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
