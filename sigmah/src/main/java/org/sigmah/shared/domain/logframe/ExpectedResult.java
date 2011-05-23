package org.sigmah.shared.domain.logframe;

import org.hibernate.annotations.Filter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an item of the expected results of a specific objective of a log
 * frame.<br/>
 * An expected result contains one activity.
 * 
 * @author tmi
 * 
 */
@Entity
public class ExpectedResult extends LogFrameElement {

    private static final long serialVersionUID = -4913269192377942381L;

    private String interventionLogic;
    private SpecificObjective parentSpecificObjective;
    private List<LogFrameActivity> activities;

    /**
     * Duplicates this expected result (omits its ID).
     * @param parentSpecificObjective Specific objective that will contains this copy.
     * @param groupMap Map of copied groups.
     * @return A copy of this expected result.
     */
    public ExpectedResult copy(final SpecificObjective parentSpecificObjective, final Map<Integer, LogFrameGroup> groupMap) {
        final ExpectedResult copy = new ExpectedResult();
        copy.code = this.code;
        copy.interventionLogic = this.interventionLogic;
        copy.risks = this.risks;
        copy.assumptions = this.assumptions;
        copy.parentSpecificObjective = parentSpecificObjective;

        copy.activities = new ArrayList<LogFrameActivity>();
        for(final LogFrameActivity activity : activities)
            copy.activities.add(activity.copy(copy, groupMap));

        copy.group = groupMap.get(this.group.getId());
        copy.dateDeleted = this.dateDeleted;
        copy.position = this.position;

        return copy;
    }

  @Column(name = "intervention_logic", columnDefinition = "TEXT")
    public String getInterventionLogic() {
        return interventionLogic;
    }

    public void setInterventionLogic(String interventionLogic) {
        this.interventionLogic = interventionLogic;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_specific_objective", nullable = false)
    public SpecificObjective getParentSpecificObjective() {
        return parentSpecificObjective;
    }

    public void setParentSpecificObjective(SpecificObjective parentSpecificObjective) {
        this.parentSpecificObjective = parentSpecificObjective;
    }

    @OneToMany(mappedBy = "parentExpectedResult", cascade = CascadeType.ALL)
    @OrderBy(value = "code asc")
    @Filter(name = "hideDeleted", condition = "DateDeleted is null")
    public List<LogFrameActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<LogFrameActivity> activities) {
        this.activities = activities;
    }
}
