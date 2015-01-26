package org.sigmah.server.domain.value;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sigmah.server.domain.Indicator;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.INDICATORS_LIST_VALUE_TABLE)
public class IndicatorsListValue extends AbstractEntityId<IndicatorsListValueId> {

	private static final long serialVersionUID = -8267821835924810690L;

	@EmbeddedId
	@AttributeOverrides({
												@AttributeOverride(name = "idList", column = @Column(name = EntityConstants.INDICATORS_LIST_VALUE_COLUMN_ID_INDICATORS_LIST, nullable = false)),
												@AttributeOverride(name = "indicatorId", column = @Column(name = EntityConstants.INDICATORS_LIST_VALUE_COLUMN_ID_INDICATOR, nullable = false))
	})
	private IndicatorsListValueId id;

	@Column(name = EntityConstants.INDICATORS_LIST_VALUE_COLUMN_ID_INDICATORS_LIST, nullable = false, insertable = false, updatable = false)
	private Integer idList;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.INDICATORS_LIST_VALUE_COLUMN_ID_INDICATOR, nullable = false, insertable = false, updatable = false)
	private Indicator indicator;

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public IndicatorsListValueId getId() {
		return this.id;
	}

	@Override
	public void setId(IndicatorsListValueId id) {
		this.id = id;
	}

	public void setIdList(Integer id) {
		this.idList = id;
	}

	public Integer getIdList() {
		return idList;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}

	public Indicator getIndicator() {
		return indicator;
	}
}
