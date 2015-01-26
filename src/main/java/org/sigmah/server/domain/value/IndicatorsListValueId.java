package org.sigmah.server.domain.value;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.EmbeddableEntity;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Indicators List Value Id domain entity (Embedded).
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Embeddable
public class IndicatorsListValueId implements EmbeddableEntity {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 7309173168011463617L;

	@Column(name = EntityConstants.INDICATORS_LIST_VALUE_COLUMN_ID_INDICATORS_LIST, nullable = false)
	@NotNull
	private Integer idList;

	@Column(name = EntityConstants.INDICATORS_LIST_VALUE_COLUMN_ID_INDICATOR, nullable = false)
	@NotNull
	private int indicatorId;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public IndicatorsListValueId() {
	}

	public IndicatorsListValueId(Integer idList, int indicatorId) {
		this.idList = idList;
		this.indicatorId = indicatorId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idList == null) ? 0 : idList.hashCode());
		result = prime * result + indicatorId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IndicatorsListValueId other = (IndicatorsListValueId) obj;
		if (idList == null) {
			if (other.idList != null)
				return false;
		} else if (!idList.equals(other.idList))
			return false;
		if (indicatorId != other.indicatorId)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);

		builder.append("indicatorId", indicatorId);
		builder.append("idList", idList);

		return builder.toString();
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public Integer getIdList() {
		return this.idList;
	}

	public void setIdList(Integer idList) {
		this.idList = idList;
	}

	public int getIndicatorId() {
		return this.indicatorId;
	}

	public void setIndicatorId(int indicatorId) {
		this.indicatorId = indicatorId;
	}

}
