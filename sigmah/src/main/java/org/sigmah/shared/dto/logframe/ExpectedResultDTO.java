package org.sigmah.shared.dto.logframe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.sigmah.client.page.project.logframe.grid.Row.Positionable;
import org.sigmah.shared.dto.EntityDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * DTO mapping class for entity logframe.ExpectedResult.
 * 
 * @author tmi
 * 
 */
public class ExpectedResultDTO extends LogFrameElementDTO {

    private static final long serialVersionUID = 2394670766294049525L;

    public ExpectedResultDTO() {
    	setActivities(new ArrayList<LogFrameActivityDTO>());
    }
    
    @Override
    public String getEntityName() {
        return "logframe.ExpectedResult";
    }

    // Result intervention logic.
    public String getInterventionLogic() {
        return get("interventionLogic");
    }

    public void setInterventionLogic(String interventionLogic) {
        set("interventionLogic", interventionLogic);
    }

    // Result activities.
    public List<LogFrameActivityDTO> getActivities() {
        List<LogFrameActivityDTO> list = get("activities");
        if(list == null) {
        	return Collections.emptyList();
        } else {
        	return list;
        }
    }

    public void setActivities(List<LogFrameActivityDTO> activitiesDTO) {
        set("activities", activitiesDTO);
    }


    // Result parent objective.
    public SpecificObjectiveDTO getParentSpecificObjective() {
        return get("parentSpecificObjective");
    }

    public void setParentSpecificObjective(SpecificObjectiveDTO parentSpecificObjectiveDTO) {
        set("parentSpecificObjective", parentSpecificObjectiveDTO);
    }

    // Display label.
    /**
     * Sets the attribute <code>label</code> to display this element in a
     * selection window.
     */
    public void setLabel(String label) {
        set("label", label);
    }

    public String getLabel() {
        return get("label");
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ExpectedResultDTO [");
        sb.append("entity name = ");
        sb.append(getEntityName());
        sb.append(" ; id = ");
        sb.append(getId());
        sb.append(" ; group id = ");
        if (getGroup() != null) {
            sb.append(getGroup().getId() != -1 ? getGroup().getId() : getGroup()
                    .getClientSideId());
        }
        sb.append(" ; dlabel = ");
        sb.append(getLabel());
        sb.append(" ; code = ");
        sb.append(getCode());
        sb.append(" ; date deleted = ");
        sb.append(" ; intervention logic = ");
        sb.append(getInterventionLogic());
        sb.append(" ; risks = ");
        sb.append(getRisks());
        sb.append(" ; assumptions = ");
        sb.append(getAssumptions());
        sb.append(" ; activities = (\n");
        if (getActivities() != null) {
            for (final LogFrameActivityDTO a : getActivities()) {
                sb.append(a);
                sb.append("\n");
            }
        }
        sb.append(")]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        return getClientSideId();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof ExpectedResultDTO)) {
            return false;
        }

        final ExpectedResultDTO other = (ExpectedResultDTO) obj;
        return getClientSideId() == other.getClientSideId();
    }

    /**
     * Adds a new activity to this log frame.
     * 
     * @return The new activity.
     */
    public LogFrameActivityDTO addActivity() {

        List<LogFrameActivityDTO> activities = getActivities();

        // Retrieves the higher code.
        int max = 0;
        for (final LogFrameActivityDTO activity : activities) {
            max = activity.getCode() > max ? activity.getCode() : max;
        }

        // Creates the activity.
        final LogFrameActivityDTO newActivity = new LogFrameActivityDTO();
        newActivity.setCode(max + 1);
        newActivity.setParentExpectedResult(this);

        // Adds it to the local list.
        activities.add(newActivity);

        return newActivity;
    }

    /**
     * Removes an activity from this result.
     * 
     * @param activity
     *            The activity to remove.
     * @return If the activity has been removed.
     */
    public boolean removeActivity(LogFrameActivityDTO activity) {
        return getActivities().remove(activity);
    }
}
