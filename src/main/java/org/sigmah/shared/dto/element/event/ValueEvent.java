package org.sigmah.shared.dto.element.event;

import java.io.Serializable;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.referential.ValueEventChangeType;
import org.sigmah.shared.dto.value.TripletValueDTO;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event transmitted to the {@link org.sigmah.client.ui.presenter.orgunit.OrgUnitPresenter OrgUnitPresenter} when a
 * flexible element value changes.
 * 
 * @author HUZHE
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ValueEvent extends GWTImmortalEvent<ValueHandler> implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6920472009097129066L;

	private final static GwtEvent.Type<ValueHandler> TYPE = new GwtEvent.Type<ValueHandler>();

	private FlexibleElementDTO sourceElement;
	private TripletValueDTO listValue;
	private String singleValue;
	// Only used for the elements part of a list.
	private ValueEventChangeType changeType;
	boolean isProjectCountryChanged = false;

	public ValueEvent(FlexibleElementDTO sourceElement, String singleValue) {
		this.sourceElement = sourceElement;
		this.singleValue = singleValue;
	}

	/**
	 * ValueEvent that is unique for OrgUnit DefaultFlexibleElement
	 * 
	 * @param orgUnitElement
	 *          OrgUnit DefaultFlxibleElement
	 * @param singleValue
	 *          Name of OrgUnit
	 * @param isProjectCountryChanged
	 *          If the OrgUnit element is attached to a project,specify if you want to change the OrgUnit of the project
	 *          by the new orgunit's country,or just change the OrgUnit without touching the country of project.
	 */
	public ValueEvent(FlexibleElementDTO orgUnitElement, String singleValue, boolean isProjectCountryChanged) {
		this.sourceElement = orgUnitElement;
		this.singleValue = singleValue;
		this.isProjectCountryChanged = isProjectCountryChanged;
	}

	public ValueEvent(FlexibleElementDTO sourceElement, TripletValueDTO listValue) {
		this.sourceElement = sourceElement;
		this.listValue = listValue;
		this.changeType = ValueEventChangeType.ADD;
	}

	public ValueEvent(FlexibleElementDTO sourceElement, TripletValueDTO listValue, ValueEventChangeType changeType) {
		this.sourceElement = sourceElement;
		this.listValue = listValue;
		if (changeType == null) {
			this.changeType = ValueEventChangeType.ADD;
		} else {
			this.changeType = changeType;
		}
	}

	@Override
	protected void dispatch(ValueHandler handler) {
		handler.onValueChange(this);
	}

	@Override
	public GwtEvent.Type<ValueHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<ValueHandler> getType() {
		return TYPE;
	}

	public void setSourceElement(FlexibleElementDTO sourceElement) {
		this.sourceElement = sourceElement;
	}

	public FlexibleElementDTO getSourceElement() {
		return sourceElement;
	}

	public ValueEventChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(ValueEventChangeType changeType) {
		this.changeType = changeType;
	}

	public TripletValueDTO getListValue() {
		return listValue;
	}

	public void setListValue(TripletValueDTO listValue) {
		this.listValue = listValue;
	}

	public String getSingleValue() {
		return singleValue;
	}

	public void setSingleValue(String singleValue) {
		this.singleValue = singleValue;
	}

	/**
	 * @return the isProjectCountryChanged
	 */
	public boolean isProjectCountryChanged() {
		return isProjectCountryChanged;
	}

	/**
	 * @param isProjectCountryChanged
	 *          the isProjectCountryChanged to set
	 */
	public void setProjectCountryChanged(boolean isProjectCountryChanged) {
		this.isProjectCountryChanged = isProjectCountryChanged;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);

		builder.append("source", sourceElement.getId());
		builder.append("value", singleValue);
		builder.append("values", listValue);
		builder.append("changeType", changeType);

		return builder.toString();
	}

}
