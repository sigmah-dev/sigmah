package org.sigmah.server.domain.logframe;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Logframe activity domain entity.
 * </p>
 * <p>
 * Represents the activity of an expected result of a log frame.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LOGFRAME_ACTIVITY_TABLE)
public class LogFrameActivity extends LogFrameElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -2247266774443718302L;

	@Column(name = EntityConstants.LOGFRAME_ACTIVITY_COLUMN_TITLE, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String title;

	@Column(name = EntityConstants.LOGFRAME_ACTIVITY_COLUMN_START_DATE)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name = EntityConstants.LOGFRAME_ACTIVITY_COLUMN_END_DATE)
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date endDate;

	@Column(name = EntityConstants.LOGFRAME_ACTIVITY_COLUMN_ADVANCEMENT)
	private Integer advancement;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.LOGFRAME_ACTIVITY_COLUMN_EXPECTED_RESULT_ID, nullable = false)
	@NotNull
	private ExpectedResult parentExpectedResult;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Duplicates this activity (omits its ID).
	 * 
	 * @param parentExpectedResult
	 *          Expected result that will contains this copy.
	 * @param context
	 *          Map of copied groups.
	 * @return A copy of this activity.
	 */
	public LogFrameActivity copy(final ExpectedResult parentExpectedResult, final LogFrameCopyContext context) {
		final LogFrameActivity copy = new LogFrameActivity();
		copy.code = this.code;
		// copy.content = this.content;
		copy.parentExpectedResult = parentExpectedResult;
		copy.group = context.getGroupCopy(this.group);
		copy.title = this.title;
		copy.startDate = this.startDate;
		copy.endDate = this.endDate;
		copy.position = this.position;
		copy.advancement = this.advancement;
		copy.indicators = copyIndicators(context);
		return copy;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public ExpectedResult getParentExpectedResult() {
		return parentExpectedResult;
	}

	public void setParentExpectedResult(ExpectedResult parentExpectedResult) {
		this.parentExpectedResult = parentExpectedResult;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Integer getAdvancement() {
		return advancement;
	}

	public void setAdvancement(Integer advancement) {
		this.advancement = advancement;
	}

}
