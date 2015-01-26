/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.dto;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * Convenience class for groups of Indicators, which are currently not modeled as
 * entities by as properties of Indicator.
 *
 * See {@link ActivityDTO#groupIndicators()}
 *
 * @author Alex Bertram (akbertram@gmail.com)
 */
public final class IndicatorGroup extends BaseModelData implements EntityDTO {

    private static final long serialVersionUID = -4402642905140940245L;
    
    public static final String ENTITY_NAME = "Activity";
    
    private List<IndicatorDTO> indicators = new ArrayList<IndicatorDTO>();
    
    public IndicatorGroup(){}
    
    public IndicatorGroup(String name) {
        set("name", name);
    }
    
    @Override
    public int getId() {
    	return (Integer)get("id");
    }
    
    public void setId(int id) {
    	set("id", id);
    }

    /**
     * Returns the name of the IndicatorGroup; corresponds to
     * {@link IndicatorDTO#getCategory()}
     *
     * @return the name of the IndicatorGroup
     */
    public String getName() {
        return get("name");
    }
    
    public void setName(String value) {
    	set("name" , value);
    }
    
    public List<IndicatorDTO> getIndicators() {
        return indicators;         
    }

    public void addIndicator(IndicatorDTO indicator) {
        indicators.add(indicator);
    }

	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}    
}
