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

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * One-to-one DTO for the {@link org.sigmah.server.domain.Indicator} domain object.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class IndicatorDTO extends AbstractModelDataEntityDTO<Integer> implements IndicatorElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2992445195546815844L;
	
	public static final String ENTITY_NAME = "Indicator";

	public static final int AGGREGATE_SUM = 0;
	public static final int AGGREGATE_AVG = 1;
	public static final int AGGREGATE_SITE_COUNT = 2;
	public static final int AGGREGATE_MULTINOMIAL = 3;

	public static final String PROPERTY_PREFIX = "I";
	
	public static final String NAME = "name";
	public static final String UNITS = "units";
	public static final String CODE = "code";
	public static final String CATEGORY = "category";
	public static final String CURRENT_VALUE = "currentValue";
	public static final String OBJECTIVE = "objective";
	public static final String DESCRIPTION = "description";
	public static final String DIRECT_DATA_ENTRY_ENABLED = "directDataEntryEnabled";
	public static final String COLLECT_INTERVENTION = "collectIntervention";
	public static final String COLLECT_MONITORING = "collectMonitoring";
	public static final String AGGREGATION = "aggregation";
	public static final String LABELS = "labels";
	public static final String LABEL_COUNTS = "labelCounts";
	public static final String GROUP_ID = "groupId";
	public static final String DATABASE_ID = "databaseId";
	public static final String SOURCE_OF_VERIFICATION = "sourceOfVerification";
	
	public static final String DATA_SOURCE_IDS = "dataSourceIds";
	public static final String SORT_ORDER = "sortOrder";

	public IndicatorDTO() {
		// Required for RPC serialization.
		super();
	}

	/**
	 * @param name
	 *          the name of the Indicator
	 * @param units
	 *          string describing this Indicator's units
	 */
	public IndicatorDTO(String name, String units) {
		super();
		set(NAME, name);
		set(UNITS, units);
	}

	/**
	 * Constructs a copy of the given IndicatorDTO
	 */
	public IndicatorDTO(IndicatorDTO dto) {
		super(dto.getProperties());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(NAME, getName());
		builder.append(UNITS, getUnits());
		builder.append(CODE, getCode());
		builder.append(CATEGORY, getCategory());
		builder.append(CURRENT_VALUE, getCurrentValue());
		builder.append(OBJECTIVE, getObjective());
	}

	/**
	 * @return the id of the Indicator
	 */
	@Override
	public Integer getId() {
		Object value = get(ID);
		return value == null ? 0 : (Integer) value;
	}

	/**
	 * Sets the Indicator's id
	 */
	public void setId(Integer id) {
		set(ID, id);
	}

	/**
	 * Sets the Indicator's name
	 */
	public void setName(String name) {
		set(NAME, name);
	}

	/**
	 * @return the Indicator's name
	 */
	public String getName() {
		return get(NAME);
	}

	/**
	 * Sets the Indicator's units, for example, "household" or "%"
	 */
	public void setUnits(String units) {
		set(UNITS, units);
	}

	/**
	 * @return the Indicator's units
	 */
	public String getUnits() {
		return get(UNITS);
	}

	/**
	 * @return the short list header used when displaying this Indicator in a grid
	 */
	public String getCode() {
		return get(CODE);
	}

	/**
	 * Sets the short list header that is used when this Indicator's values are displayed in a grid.
	 */
	public void setCode(String value) {
		set(CODE, value);
	}

	/**
	 * Full description of this Indicator, used to aid users entering data.
	 */
	public void setDescription(String description) {
		set(DESCRIPTION, description);
	}

	/**
	 * @return this Indicator's description, principally used to aid users entering data
	 */
	public String getDescription() {
		return get(DESCRIPTION);
	}

	/**
	 * @return true if the user can associate indicator values with this project, or false if this indicator takes its
	 *         value exclusively from its data sources
	 */
	public boolean isDirectDataEntryEnabled() {
		return get(DIRECT_DATA_ENTRY_ENABLED, true);
	}

	public void setDirectDataEntryEnabled(boolean directDataEntryEnabled) {
		set(DIRECT_DATA_ENTRY_ENABLED, directDataEntryEnabled);
	}

	/**
	 * Sets whether this indicator is collected during the intervention. (Some indicators are only collected during the
	 * monitoring phase)
	 */
	public void setCollectIntervention(boolean collected) {
		set(COLLECT_INTERVENTION, collected);
	}

	/**
	 * @return true if this indicator is collected during the intervention. (Some indicators are only collected during the
	 *         monitoring phase)
	 */
	public Boolean isCollectIntervention() {
		return (Boolean) get(COLLECT_INTERVENTION);
	}

	/**
	 * Sets whether this indicator is collected during the monitoring phase/
	 */
	public void setCollectMonitoring(boolean collected) {
		set(COLLECT_MONITORING, collected);
	}

	/**
	 * @return true if this indicator is collected during the monitoring phase
	 */
	public Boolean isCollectMonitoring() {
		return (Boolean) get(COLLECT_MONITORING);
	}

	/**
	 * Sets the aggregation method for this indicator
	 */
	public void setAggregation(int aggregation) {
		set(AGGREGATION, aggregation);
	}

	/**
	 * @return the aggregation method for this indicator
	 */
	public int getAggregation() {
		return (Integer) get(AGGREGATION);
	}

	/**
	 * @return this Indicator's category
	 */
	public String getCategory() {
		return get(CATEGORY);
	}

	/**
	 * Sets this Indicator's category
	 */
	public void setCategory(String category) {
		set(CATEGORY, category);
	}

	/**
	 * Sets this Indicator's objective
	 */
	public void setObjective(Double objective) {
		set(OBJECTIVE, objective);
	}

	public void setCurrentValue(Double value) {
		set(CURRENT_VALUE, value);
	}

	public Double getCurrentValue() {
		return (Double) get(CURRENT_VALUE);
	}

	@SuppressWarnings("unchecked")
	public List<String> getLabels() {
		return (List<String>) get(LABELS);
	}

	public void setLabels(List<String> labels) {
		set(LABELS, labels);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> getLabelCounts() {
		return (List<Integer>) get(LABEL_COUNTS);
	}

	public int getLabelCount(int index) {
		return getLabelCounts().get(index);
	}

	public void setLabelCounts(List<Integer> counts) {
		set(LABEL_COUNTS, counts);
	}

	/**
	 * @return this Indicator's objective value
	 */
	public Double getObjective() {
		return (Double) get(OBJECTIVE);
	}

	public Integer getGroupId() {
		return (Integer) get(GROUP_ID);
	}

	public void setGroupId(Integer id) {
		set(GROUP_ID, id);
	}

	/**
	 * @return the name of the property in which values for this indicator are stored, for example in the
	 *         {@link org.sigmah.shared.dto.SiteDTO} object.
	 */
	public String getPropertyName() {
		return getPropertyName(this.getId());
	}

	/**
	 * Returns the name of the property in which values for Indicators of this id are stored, for example in the
	 * {@link org.sigmah.shared.dto.SiteDTO} object. For example, an indicator with the id of 3 will be stored as I3 =>
	 * 1432.32 in a SiteDTO.
	 * 
	 * @param id
	 * @return the property name for
	 */
	public static String getPropertyName(int id) {
		return PROPERTY_PREFIX + id;
	}

	/**
	 * Parses an Indicator property name, for example "I432" or "I565" for the referenced indicator Id.
	 * 
	 * @return the id of referenced Indicator
	 */
	public static int indicatorIdForPropertyName(String propertyName) {
		return Integer.parseInt(propertyName.substring(PROPERTY_PREFIX.length()));
	}

	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public boolean isQualitative() {
		return getAggregation() == AGGREGATE_MULTINOMIAL;
	}

	public Integer getDatabaseId() {
		return (Integer) get(DATABASE_ID);
	}

	public void setDatabaseId(Integer id) {
		set(DATABASE_ID, id);
	}

	public String getSourceOfVerification() {
		return get(SOURCE_OF_VERIFICATION);
	}

	public void setSourceOfVerification(String source) {
		set(SOURCE_OF_VERIFICATION, source);
	}

	/**
	 * @return for indicators with multinomial aggregation, computes the total result count across categories
	 *         (denominator)
	 */
	private int totalResultCount() {
		int sum = 0;
		for (Integer count : getLabelCounts()) {
			sum += count;
		}
		return sum;
	}

	/**
	 * @return for indicators with multinomial aggregation, finds the category that occurs most frequently
	 */
	private int modeIndex() {
		int max = 0;
		int modeIndex = -1;
		for (int i = 0; i != getLabelCounts().size(); ++i) {
			if (getLabelCount(i) > max) {
				max = getLabelCount(i);
				modeIndex = i;
			}
		}
		return modeIndex;
	}

	/**
	 * @return a formatted description of the mode
	 */
	public String formatMode() {
		int modeIndex = modeIndex();
		if (modeIndex >= 0) {
			double percentage = getLabelCount(modeIndex) / (double) totalResultCount() * 100d;
			return getLabels().get(modeIndex) + " (" + (int) percentage + "%)";
		} else {
			return "";
		}
	}
}
