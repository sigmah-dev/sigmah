package org.sigmah.shared.dto.value;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * IndicatorsListValueDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class IndicatorsListValueDTO extends AbstractModelDataEntityDTO<Integer> implements ListableValue {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;
	
	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "value.IndicatorsListValue";

	// DTO attributes keys.
	public static final String ID_LIST = "idList";
	public static final String INDICATOR = "indicator";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(ID_LIST, getIdList());
	}

	// Indicators list value list id
	public int getIdList() {
		return (Integer) get(ID_LIST);
	}

	public void setIdList(int idList) {
		set(ID_LIST, idList);
	}

	// Indicator's reference
	public IndicatorDTO getIndicatorDTO() {
		return (IndicatorDTO) get(INDICATOR);
	}

	public void setIndicatorDTO(IndicatorDTO indicator) {
		set(INDICATOR, indicator);
	}

}
