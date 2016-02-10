package org.sigmah.server.domain.value;

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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.element.CheckboxElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.element.TextAreaElement;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Activy domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.VALUE_TABLE, uniqueConstraints = { @UniqueConstraint(columnNames = {
																																																	EntityConstants.FLEXIBLE_ELEMENT_COLUMN_ID,
																																																	EntityConstants.VALUE_COLUMN_ID_PROJECT
})
})
public class Value extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2578586689736955636L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.VALUE_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.VALUE_COLUMN_ID_PROJECT, nullable = false)
	@NotNull
	private Integer containerId;

	@Column(name = EntityConstants.VALUE_COLUMN_VALUE, nullable = true, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String value;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = EntityConstants.VALUE_COLUMN_DATE_LAST_MODIF, nullable = false)
	@NotNull
	private Date lastModificationDate;

	@Column(name = EntityConstants.VALUE_COLUMN_ACTION_LAST_MODIF, nullable = false)
	@NotNull
	private Character lastModificationAction;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.FLEXIBLE_ELEMENT_COLUMN_ID, nullable = false)
	private FlexibleElement element;

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.VALUE_COLUMN_ID_USER_LAST_MODIF, nullable = false)
	private User lastModificationUser;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------
	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("containerId", containerId);
		builder.append("value", value);
		builder.append("lastModificationDate", lastModificationDate);
		builder.append("lastModificationAction", lastModificationAction);
	}

	/**
	 * Returns if the value can be consider as <code>valid</code> (correctly filled) according to its type.
	 * 
	 * @return if the value is valid.
	 */
	@Transient
	public boolean isValid() {

		if (!element.isValidates()) {
			return true;
		}

		if (element instanceof CheckboxElement) {
			if (value.equals("true")) {
				return true;
			}
		} else if (element instanceof TextAreaElement) {
			if (!value.equals("")) {
				return true;
			}
		}

		return false;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public void setElement(FlexibleElement element) {
		this.element = element;
	}

	public FlexibleElement getElement() {
		return element;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setContainerId(Integer containerId) {
		this.containerId = containerId;
	}

	public Integer getContainerId() {
		return containerId;
	}

	public void setLastModificationUser(User lastModificationUser) {
		this.lastModificationUser = lastModificationUser;
	}

	public User getLastModificationUser() {
		return lastModificationUser;
	}

	public void setLastModificationDate(Date lastModificationDate) {
		this.lastModificationDate = lastModificationDate;
	}

	public Date getLastModificationDate() {
		return lastModificationDate;
	}

	public void setLastModificationAction(Character lastModificationAction) {
		this.lastModificationAction = lastModificationAction;
	}

	public Character getLastModificationAction() {
		return lastModificationAction;
	}
}
