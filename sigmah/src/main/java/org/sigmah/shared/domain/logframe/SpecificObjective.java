package org.sigmah.shared.domain.logframe;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Sort;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Represents an item of the specific objectives of a log frame.<br/>
 * A specific objective contains a list of expected results.
 * 
 * @author tmi
 * 
 */
@Entity
@Table(name = "log_frame_specific_objective")
public class SpecificObjective extends LogFrameElement {

    private static final long serialVersionUID = 7534655171979110984L;

    private String interventionLogic;
    private LogFrame parentLogFrame;
    private List<ExpectedResult> expectedResults = new ArrayList<ExpectedResult>();

    /**
     * Duplicates this objective (omits its ID).
     * @param parentLogFrame Log frame that will contains this copy.
     * @param groupMap Map of copied groups.
     * @return A copy of this specific objective.
     */
    public SpecificObjective copy(final LogFrame parentLogFrame, final Map<Integer, LogFrameGroup> groupMap) {
        final SpecificObjective copy = new SpecificObjective();
        copy.code = this.code;
        copy.interventionLogic = this.interventionLogic;
        copy.risks = this.risks;
        copy.assumptions = this.assumptions;
        copy.parentLogFrame = parentLogFrame;

        copy.expectedResults = new ArrayList<ExpectedResult>();
        for(final ExpectedResult result : this.expectedResults)
            copy.expectedResults.add(result.copy(copy, groupMap));
        
        copy.group = groupMap.get(this.group.getId());
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
    @JoinColumn(name = "id_log_frame", nullable = false)
    public LogFrame getParentLogFrame() {
        return parentLogFrame;
    }

    public void setParentLogFrame(LogFrame parentLogFrame) {
        this.parentLogFrame = parentLogFrame;
    }

    @OneToMany(mappedBy = "parentSpecificObjective", cascade = CascadeType.ALL)
    // use @Sort instead of @OrderBy as hibernate biffs because the code lives in the log_frame_element table
    @org.hibernate.annotations.Sort 
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)     
    public List<ExpectedResult> getExpectedResults() {
        return expectedResults;
    }

    public void setExpectedResults(List<ExpectedResult> expectedResults) {
        this.expectedResults = expectedResults;
    }
}
