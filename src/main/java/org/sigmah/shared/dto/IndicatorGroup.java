package org.sigmah.shared.dto;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * Convenience class for groups of Indicators, which are currently not modeled as entities by as properties of
 * Indicator. See {@link ActivityDTO#groupIndicators()}.
 *
 * @author Alex Bertram (akbertram@gmail.com)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @see ActivityDTO#groupIndicators()
 */
public final class IndicatorGroup extends AbstractModelDataEntityDTO<Integer> implements IndicatorElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4402642905140940245L;

	public static final String ENTITY_NAME = "Activity";
	
	public static final String DATABASE_ID = "databaseId";
	public static final String NAME = "name";

	private List<IndicatorDTO> indicators = new ArrayList<IndicatorDTO>();

	public IndicatorGroup() {
	}

	public IndicatorGroup(String name) {
		set(NAME, name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(NAME, getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	@Override
	public Integer getId() {
		return (Integer) get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	/**
	 * Returns the name of the IndicatorGroup; corresponds to {@link IndicatorDTO#getCategory()}
	 *
	 * @return the name of the IndicatorGroup
	 */
	public String getName() {
		return get(NAME);
	}

	public void setName(String value) {
		set(NAME, value);
	}

	public List<IndicatorDTO> getIndicators() {
		return indicators;
	}

	public void addIndicator(IndicatorDTO indicator) {
		indicators.add(indicator);
	}

}
