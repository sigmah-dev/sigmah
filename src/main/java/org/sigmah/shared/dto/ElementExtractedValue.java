package org.sigmah.shared.dto;

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
import java.util.HashMap;
import java.util.Map;

import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.referential.ElementExtractedValueStatus;

import com.extjs.gxt.ui.client.data.BaseModel;
import org.sigmah.shared.dto.base.DTO;

/**
 * Represents the association between a {@link FlexibleElementDTO} and its new value for the importation
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ElementExtractedValue extends BaseModel implements Serializable, DTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2460171806391173264L;

	private FlexibleElementDTO element;
	private Serializable oldValue;
	private Serializable newValue;
	private ElementExtractedValueStatus status;
	private Map<Integer, String> oldBudgetValues = new HashMap<Integer, String>();
	private Map<Integer, Serializable> newBudgetValues = new HashMap<Integer, Serializable>();

	public ElementExtractedValue() {
		// Serialization.
	}

	/**
	 * @return the element
	 */
	public FlexibleElementDTO getElement() {
		return element;
	}

	/**
	 * @param element
	 *          the element to set
	 */
	public void setElement(FlexibleElementDTO element) {
		this.element = element;
	}

	/**
	 * @return the status
	 */
	public ElementExtractedValueStatus getStatus() {
		return status;
	}

	/**
	 * @param status
	 *          the status to set
	 */
	public void setStatus(ElementExtractedValueStatus status) {
		this.status = status;
	}

	/**
	 * @return the oldValue
	 */
	public Serializable getOldValue() {
		return oldValue;
	}

	/**
	 * @param oldValue
	 *          the oldValue to set
	 */
	public void setOldValue(Serializable oldValue) {
		this.oldValue = oldValue;
	}

	/**
	 * @return the newValue
	 */
	public Serializable getNewValue() {
		return newValue;
	}

	/**
	 * @param newValue
	 *          the newValue to set
	 */
	public void setNewValue(Serializable newValue) {
		this.newValue = newValue;
	}

	/**
	 * @return the newBudgetValues
	 */
	public Map<Integer, Serializable> getNewBudgetValues() {
		return newBudgetValues;
	}

	/**
	 * @param newBudgetValues
	 *          the newBudgetValues to set
	 */
	public void setNewBudgetValues(Map<Integer, Serializable> newBudgetValues) {
		this.newBudgetValues = newBudgetValues;
	}

	/**
	 * @return the oldBudgetValues
	 */
	public Map<Integer, String> getOldBudgetValues() {
		return oldBudgetValues;
	}

	/**
	 * @param oldBudgetValues
	 *          the oldBudgetValues to set
	 */
	public void setOldBudgetValues(Map<Integer, String> oldBudgetValues) {
		this.oldBudgetValues = oldBudgetValues;
	}

}
