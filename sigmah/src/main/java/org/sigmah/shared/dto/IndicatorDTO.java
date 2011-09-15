/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package	org.sigmah.shared.dto;

import com.extjs.gxt.ui.client.data.BaseModelData;

import java.util.List;


/**
 * One-to-one DTO for the {@link org.sigmah.shared.domain.Indicator} domain object.
 *
 * @author Alex Bertram
 */
public final class IndicatorDTO extends BaseModelData implements EntityDTO {
	private static final long serialVersionUID = -2992445195546815844L;
    public final static int AGGREGATE_SUM = 0;
	public final static int AGGREGATE_AVG = 1;
	public final static int AGGREGATE_SITE_COUNT = 2;
	public final static int AGGREGATE_MULTINOMIAL = 3;

    public static final String PROPERTY_PREFIX = "I";
	
	public IndicatorDTO() {
        super();
	}

    /**
     *
     * @param name  the name of the Indicator
     * @param units  string describing this Indicator's units
     */
	public IndicatorDTO(String name, String units) {
        super();
		set("name", name);
		set("units", units);		
	}

    /**
     * Constructs a copy of the given IndicatorDTO
     */
    public IndicatorDTO(IndicatorDTO dto) {
        super(dto.getProperties());
    }

    /**
     *
     * @return the id of the Indicator
     */
    public int getId() {
        Object value = get("id");
		return value == null ? 0 : (Integer)value;
    }

    /**
     * Sets the Indicator's id
     */
    public void setId(int id) {
        set("id", id);
    }

    /**
     * Sets the Indicator's name
     */
    public void setName(String name) {
		set("name", name);
	}

    /**
     * @return the Indicator's name
     */
	public String getName() {
		return get("name");		
	}

    /**
     * Sets the Indicator's units, for example, "household" or  "%"
     */
	public void setUnits(String units) {
		set("units",units);
	}

    /**
     * @return the Indicator's units
     */
	public String getUnits() {
		return get("units");	
	}

    /**
     * @return the short list header used when displaying this Indicator in a grid
     */
	public String getCode() {
		return get("code");
	}

    /**
     * Sets the short list header that is used when this Indicator's values are displayed
     * in a grid.
     */
	public void setCode(String value) {
		set("code", value);
	}

    /**
     * Full description of this Indicator, used to aid users entering data.
     */
	public void setDescription(String description) {
		set("description", description);
	}

    /**
     * @return this Indicator's description, principally used to aid users entering data
     */
	public String getDescription() {
		return get("description");
	}

    /**
     * Sets whether this indicator is collected during the intervention.
     * (Some indicators are only collected during the monitoring phase)
     */
	public void setCollectIntervention(boolean collected) {
		set("collectIntervention", collected);
	}

    /**
     * @return true if this indicator is collected during the intervention.
     * (Some indicators are only collected during the monitoring phase)
     */
	public Boolean isCollectIntervention() {
		return (Boolean)get("collectIntervention");
	}

    /**
     * Sets whether this indicator is collected during the monitoring phase/
     */
	public void setCollectMonitoring(boolean collected) {
		set("collectMonitoring",collected);
	}

    /**
     * @return true if this indicator is collected during the monitoring phase
     */
	public Boolean isCollectMonitoring() {
		return (Boolean)get("collectMonitoring");
	}

    /**
     * Sets the aggregation method for this indicator
     */
	public void setAggregation(int aggregation) {
		set("aggregation", aggregation);
	}

    /**
     * @return the aggregation method for this indicator
     */
	public int getAggregation() {
		return (Integer)get("aggregation");
	}

    /**
     * @return this Indicator's category
     */
	public String getCategory() {
		return get("category");
	}

    /**
     * Sets this Indicator's category
     */
	public void setCategory(String category) {
		set("category", category);
	}

	/**
     * Sets this Indicator's objective
     */
	public void setObjective(double objective) {
		set("objective", objective);
	}


	public void setCurrentValue(Double value) {
		set("currentValue", value);
	}
	
	public Double getCurrentValue() {
		return (Double)get("currentValue");
	}

	public List<String> getLabels() {
		return (List<String>)get("labels");
	}

	public void setLabels(List<String> labels) {
		set("labels", labels);
	}
	
	public List<Integer> getLabelCounts() {
		return (List<Integer>)get("labelCounts");
	}

    public int getLabelCount(int index) {
        return getLabelCounts().get(index);
    }
	
	public void setLabelCounts(List<Integer> counts) {
		set("labelCounts", counts);
	}

	/**
     *
     * @return this Indicator's objective value 
     *
     */
	public Double getObjective() { 
		return (Double)get("objective");
	}
	
	public Integer getGroupId() {
		return (Integer)get("groupId");
	}
	
	public void setGroupId(Integer id) {
		set("groupId", id);
	}
	
    /**
     *
     * @return the name of the property in which values for this indicator are stored, for
     * example in the {@link org.sigmah.shared.dto.SiteDTO} object.
     *
     */
	public String getPropertyName() { 
		return getPropertyName(this.getId());
	}
	
    /**
     * Returns the name of the property in which values for Indicators of this id are stored, for
     * example in the {@link org.sigmah.shared.dto.SiteDTO} object.
     *
     * For example, an indicator with the id of 3 will be stored as I3 => 1432.32 in a
     * SiteDTO.
     *
     * @param id
     * @return the property name for
     */
	public static String getPropertyName(int id) {
		return PROPERTY_PREFIX + id;
	}

    /**
     * Parses an Indicator property name, for example "I432" or "I565" for the referenced
     * indicator Id.
     *
     * @return the id of referenced Indicator
     */
	public static int indicatorIdForPropertyName(String propertyName) {
		return Integer.parseInt(propertyName.substring(PROPERTY_PREFIX.length()));
	}

    public String getEntityName() {
        return "Indicator";
    }

	public boolean isQualitative() {
		return getAggregation() == AGGREGATE_MULTINOMIAL;
	}

	public Integer getDatabaseId() {
		return (Integer)get("databaseId");
	}
	
	public void setDatabaseId(Integer id) {
		set("databaseId", id);
	}
	
	public String getSourceOfVerification() {
		return get("sourceOfVerification");
	}
	
	public void setSourceOfVerification(String source) {
		set("sourceOfVerification", source);
	}

    /**
     * @return for indicators with multinomial aggregation,
     * computes the total result count across categories (denominator)
     */
    private int totalResultCount() {
        int sum = 0;
        for(Integer count : getLabelCounts()) {
            sum += count;
        }
        return sum;
    }

    /**
     * @return for indicators with multinomial aggregation,
     * finds the category that occurs most frequently
     */
    private int modeIndex() {
        int max = 0;
        int modeIndex = -1;
        for(int i=0;i!=getLabelCounts().size();++i) {
            if(getLabelCount(i) > max) {
                max = getLabelCount(i);
                modeIndex = i;
            }
        }
        return modeIndex;
    }

    /**
     *
     * @return a formatted description of the mode
     */
    public String formatMode() {
        int modeIndex = modeIndex();
        if(modeIndex >= 0) {
            double percentage = getLabelCount(modeIndex) / (double)totalResultCount() * 100d;
            return getLabels().get(modeIndex) + " (" + (int)percentage + "%)";
        } else {
            return "";
        }
    }
}
