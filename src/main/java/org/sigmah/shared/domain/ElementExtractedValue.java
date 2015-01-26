package org.sigmah.shared.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.sigmah.shared.dto.ElementExtractedValueStatus;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

import com.extjs.gxt.ui.client.data.BaseModel;

/**
 * Represents the association between a {@link FlexibleElementDTO} and its new
 * value for the importation
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * 
 */
public class ElementExtractedValue extends BaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2460171806391173264L;

	private FlexibleElementDTO element;
	private Serializable oldValue;
	private Serializable newValue;
	private ElementExtractedValueStatus status;
	private Map<Integer, String> oldBudgetValues = new HashMap<Integer, String>();
	private Map<Integer, Serializable> newBudgetValues = new HashMap<Integer, Serializable>();

	public ElementExtractedValue() {

	}

	/**
	 * @return the element
	 */
	public FlexibleElementDTO getElement() {
		return element;
	}

	/**
	 * @param element
	 *            the element to set
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
	 * @param status the status to set
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
	 *            the oldValue to set
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
	 *            the newValue to set
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
	 *            the newBudgetValues to set
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
	 *            the oldBudgetValues to set
	 */
	public void setOldBudgetValues(Map<Integer, String> oldBudgetValues) {
		this.oldBudgetValues = oldBudgetValues;
	}

}
