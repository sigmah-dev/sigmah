package org.sigmah.shared.command.result;

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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.value.ListableValue;

/**
 * Value result containing the inner value object or the inner values list object of a
 * {@link org.sigmah.server.domain.element.FlexibleElement FlexibleElement}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ValueResult implements Result {

	/**
	 * Inner values list object.
	 */
	private List<ListableValue> valuesObject;

	/**
	 * Single value.
	 */
	private String valueObject;

	/**
	 * Indicates the origin of the value.
	 */
	private boolean amendment;

	public ValueResult() {
		// Serialization.
	}

	/**
	 * Instantiates the local if it isn't done yet.
	 */
	private void ensureListIsNotNull() {
		if (valuesObject == null) {
			valuesObject = new ArrayList<ListableValue>();
		}
	}

	/**
	 * Indicates if the current object contains a valid value.
	 * 
	 * @return {@code true} if the current object contains a valid value, {@code false} otherwise.
	 */
	public boolean isValueDefined() {
		return (valueObject != null) || (valuesObject != null && valuesObject.size() > 0);
	}

	/**
	 * Returns the unique value.
	 * 
	 * @return the unique value.
	 */
	public String getValueObject() {
		return valueObject;
	}

	/**
	 * Sets a unique value.
	 * 
	 * @param valueObject
	 *          the value.
	 */
	public void setValueObject(String valueObject) {
		this.valueObject = valueObject;
	}

	/**
	 * Gets the list of values.
	 * 
	 * @return The list of values.
	 */
	public List<ListableValue> getValuesObject() {
		return valuesObject;
	}

	/**
	 * Sets the list of values.
	 * 
	 * @param valuesObject
	 *          the new list.
	 */
	public void setValuesObject(List<ListableValue> valuesObject) {
		this.valuesObject = valuesObject;
	}

	/**
	 * Adds a value to the list of values.
	 * 
	 * @param valueObject
	 *          the new value.
	 */
	public void addValueObject(ListableValue valueObject) {
		ensureListIsNotNull();
		valuesObject.add(valueObject);
	}

	/**
	 * Determine if the current value comes from an amendment.
	 * 
	 * @return <code>true</code> if the value comes from an amendment, <code>false</code> otherwise.
	 */
	public boolean isAmendment() {
		return amendment;
	}

	/**
	 * Defines the amendment value.
	 * 
	 * @param amendment
	 *          the value to set.
	 * @see #isAmendment()
	 */
	public void setAmendment(boolean amendment) {
		this.amendment = amendment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("valuesObject", valuesObject);
		builder.append("valueObject", valueObject);
		builder.append("amendment", amendment);
		return builder.toString();
	}
}
