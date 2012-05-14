/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.sigmah.shared.domain.Activity;
import org.sigmah.shared.domain.Attribute;
import org.sigmah.shared.domain.AttributeGroup;
import org.sigmah.shared.domain.Indicator;
import org.sigmah.shared.domain.LocationType;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.UserDatabase;
import org.sigmah.shared.dto.LocationTypeDTO;
import org.sigmah.shared.exception.IllegalAccessCommandException;

/**
 * Provides functionality common to CreateEntityHandler and
 * UpdateEntityHandler
 *
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class BaseEntityHandler {

    protected final EntityManager em;

    public BaseEntityHandler(EntityManager em) {
        this.em = em;
    }

    protected void updateIndicatorProperties(Indicator indicator, Map<String, Object> changes) {
        if (changes.containsKey("name")) {
            indicator.setName((String) changes.get("name"));
        }

        if (changes.containsKey("aggregation")) {
            indicator.setAggregation((Integer) changes.get("aggregation"));
        }       

        if (changes.containsKey("collectIntervention")) {
            indicator.setCollectIntervention((Boolean) changes.get("collectIntervention"));
        }

        if (changes.containsKey("collectMonitoring")) {
            indicator.setCollectMonitoring((Boolean) changes.get("collectMonitoring"));
        }

        if (changes.containsKey("code")) {
            indicator.setCode((String) changes.get("code"));
        }

        if (changes.containsKey("description")) {
            indicator.setDescription((String) changes.get("description"));
        }

        if (changes.containsKey("units")) {
            indicator.setUnits((String) changes.get("units"));
        }
        
        if (changes.containsKey("objective")) {
        	indicator.setObjective((Double)changes.get("objective"));
        }

        if (changes.containsKey("sortOrder")) {
            indicator.setSortOrder((Integer) changes.get("sortOrder"));
        }
        
        if (changes.containsKey("labels")) {
        	indicator.setLabels((List<String>) changes.get("labels"));
        }
        
        if (changes.containsKey("sourceOfVerification")) {
        	indicator.setSourceOfVerification((String)changes.get("sourceOfVerification"));
        }
        
        if (changes.containsKey("groupId")) {
        	if(changes.get("groupId")!=null){
        		indicator.setActivity( em.getReference(Activity.class, (Integer)changes.get("groupId")));
        	}else{
        		indicator.setActivity(null);
        	}
        }
        
        if (changes.containsKey("directDataEntryEnabled")) {
        	indicator.setDirectDataEntryEnabled( (Boolean)changes.get("directDataEntryEnabled"));
        }
        
        if (changes.containsKey("dataSourceIds")) {
        	Set<Integer> ids = (Set<Integer>) changes.get("dataSourceIds");
        	if(ids.isEmpty()) {
        		indicator.setDataSources(Collections.<Indicator>emptySet());
        	} else {
	        	List<Indicator> dataSources = em.createQuery("select i from Indicator i where i.id in (:ids)")
	        	.setParameter("ids", ids)
	        	.getResultList();
	        	indicator.setDataSources(new HashSet<Indicator>(dataSources));
        	}
        } 

        if (indicator.getActivity() != null) {
        	indicator.getActivity().getDatabase().setLastSchemaUpdate(new Date());
        } else {
        	indicator.getDatabase().setLastSchemaUpdate(new Date());
        }
    }

    protected void updateAttributeProperties(Map<String, Object> changes, Attribute attribute) {
        if (changes.containsKey("name")) {
            attribute.setName((String) changes.get("name"));
        }
        if (changes.containsKey("sortOrder")) {
            attribute.setSortOrder((Integer) changes.get("sortOrder"));
        }

        // TODO: update lastSchemaUpdate
    }

    protected void updateAttributeGroupProperties(AttributeGroup group, Map<String, Object> changes) {
        if (changes.containsKey("name")) {
            group.setName((String) changes.get("name"));
        }

        if (changes.containsKey("multipleAllowed")) {
            group.setMultipleAllowed((Boolean) changes.get("multipleAllowed"));
        }
        if (changes.containsKey("sortOrder")) {
            group.setSortOrder((Integer) changes.get("sortOrder"));
        }


    }

    protected void updateActivityProperties(Activity activity, Map<String, Object> changes) {
        if (changes.containsKey("name")) {
            activity.setName((String) changes.get("name"));
        }

        if (changes.containsKey("assessment")) {
            activity.setAssessment((Boolean) changes.get("assessment"));
        }

        if (changes.containsKey("locationType")) {
            activity.setLocationType(
                    em.getReference(LocationType.class,
                            ((LocationTypeDTO) changes.get("locationType")).getId()));
        }

        if (changes.containsKey("category")) {
            activity.setCategory((String) changes.get("category"));
        }

        if (changes.containsKey("mapIcon")) {
            activity.setMapIcon((String) changes.get("mapIcon"));
        }

        if (changes.containsKey("reportingFrequency")) {
            activity.setReportingFrequency((Integer) changes.get("reportingFrequency"));
        }

        if (changes.containsKey("sortOrder")) {
            activity.setSortOrder((Integer) changes.get("sortOrder"));
        }

        activity.getDatabase().setLastSchemaUpdate(new Date());
    }

    /**
     * Asserts that the user has permission to modify the structure of the given database.
     *
     * @param user     THe user for whom to check permissions
     * @param database The database the user is trying to modify
     * @throws IllegalAccessCommandException If the user does not have permission
     */
    protected void assertDesignPriviledges(User user, UserDatabase database) throws IllegalAccessCommandException {

        if (!database.isAllowedDesign(user)) {
            throw new IllegalAccessCommandException();
        }

    }

}
