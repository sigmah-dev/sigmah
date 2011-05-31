package org.sigmah.shared.domain.logframe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Represents an item of the expected results of a specific objective of a log
 * frame.<br/>
 * An expected result contains one activity.
 * 
 * @author tmi
 * 
 */
@Entity
@Table(name="log_frame_expected_result")
public class ExpectedResult extends LogFrameElement {

    private static final long serialVersionUID = -4913269192377942381L;

    private String interventionLogic;
    private SpecificObjective parentSpecificObjective;
    private List<LogFrameActivity> activities;

    /**
     * Duplicates this expected result (omits its ID).
     * @param parentSpecificObjective Specific objective that will contains this copy.
     * @param context Map of copied groups.
     * @return A copy of this expected result.
     */
    public ExpectedResult copy(final SpecificObjective parentSpecificObjective, final LogFrameCopyContext context) {
        final ExpectedResult copy = new ExpectedResult();
        copy.code = this.code;
        copy.interventionLogic = this.interventionLogic;
        copy.risks = this.risks;
        copy.assumptions = this.assumptions;
        copy.parentSpecificObjective = parentSpecificObjective;
        copy.indicators = copyIndicators(context);

        copy.activities = new ArrayList<LogFrameActivity>();
        for(final LogFrameActivity activity : activities)
            copy.activities.add(activity.copy(copy, context));

        copy.group = context.getGroupCopy(this.group);
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
    @org.hibernate.annotations.Sort
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN) 
    public List<LogFrameActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<LogFrameActivity> activities) {
        this.activities = activities;
    }
}
