/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.dto;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BaseTreeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience class for groups of Indicators, which are currently not modeled as
 * entities by as properties of Indicator.
 *
 * See {@link ActivityDTO#groupIndicators()}
 *
 * @author Alex Bertram (akbertram@gmail.com)
 */
public final class IndicatorGroup extends BaseTreeModel {

    private static final long serialVersionUID = -4402642905140940245L;
    
    public IndicatorGroup(){}
    
    public IndicatorGroup(String name) {
        set("name", name);
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

    public List<IndicatorDTO> getIndicators() {
        return (List)getChildren();         
    }

    public void addIndicator(IndicatorDTO indicator) {
        getChildren().add(indicator);
    }

 
}
