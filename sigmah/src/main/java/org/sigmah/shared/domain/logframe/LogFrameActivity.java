package org.sigmah.shared.domain.logframe;

import javax.persistence.*;
import java.util.Date;
import java.util.Map;

/**
 * Represents the activity of an expected result of a log frame.
 * 
 * @author tmi
 * 
 */
@Entity
public class LogFrameActivity extends LogFrameElement {

    private static final long serialVersionUID = -2247266774443718302L;

    private ExpectedResult parentExpectedResult;
    private String title;
    private Date startDate;
    private Date endDate;
    private Integer advancement;

    /**
     * Duplicates this activity (omits its ID).
     * @param parentExpectedResult Expected result that will contains this copy.
     * @param groupMap Map of copied groups.
     * @return A copy of this activity.
     */
    public LogFrameActivity copy(final ExpectedResult parentExpectedResult, final Map<Integer, LogFrameGroup> groupMap) {
        final LogFrameActivity copy = new LogFrameActivity();
        copy.code = this.code;
//        copy.content = this.content;
        copy.parentExpectedResult = parentExpectedResult;
        copy.group = groupMap.get(this.group.getId());
        copy.dateDeleted = this.dateDeleted;
        copy.title = this.title;
        copy.startDate = this.startDate;
        copy.endDate = this.endDate;
        copy.position = this.position;
        copy.advancement = this.advancement;
        return copy;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_result", nullable = false)
    public ExpectedResult getParentExpectedResult() {
        return parentExpectedResult;
    }

    public void setParentExpectedResult(ExpectedResult parentExpectedResult) {
        this.parentExpectedResult = parentExpectedResult;
    }

  @Column(name = "title", columnDefinition = "TEXT")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = "advancement")
    public Integer getAdvancement() {
      return advancement;
    }

	  public void setAdvancement(Integer advancement) {
		  this.advancement = advancement;
	  }
}
