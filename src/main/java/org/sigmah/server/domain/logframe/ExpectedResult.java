package org.sigmah.server.domain.logframe;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Expected result domain entity.
 * </p>
 * <p>
 * Represents an item of the expected results of a specific objective of a log frame.<br/>
 * An expected result contains one activity.
 * </p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.LOGFRAME_EXPECTED_RESULT_TABLE)
public class ExpectedResult extends LogFrameElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -4913269192377942381L;

	@Column(name = EntityConstants.LOGFRAME_EXPECTED_RESULT_COLUMN_INTERVENTION_LOGIC, columnDefinition = EntityConstants.COLUMN_DEFINITION_TEXT)
	private String interventionLogic;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.LOGFRAME_EXPECTED_RESULT_COLUMN_SPECIFIC_OBJ_ID, nullable = false)
	@NotNull
	private SpecificObjective parentSpecificObjective;

	@OneToMany(mappedBy = "parentExpectedResult", cascade = CascadeType.ALL, orphanRemoval = true)
	// In-memory sort (@Sort) is applied to activities collection.
	@org.hibernate.annotations.Sort
	private List<LogFrameActivity> activities;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Duplicates this expected result (omits its ID).
	 * 
	 * @param parentSpecificObjective
	 *          Specific objective that will contains this copy.
	 * @param context
	 *          Map of copied groups.
	 * @return A copy of this expected result.
	 */
	public ExpectedResult copy(final SpecificObjective parentSpecificObjective, final LogFrameCopyContext context) {

		final ExpectedResult copy = new ExpectedResult();

		copy.code = this.code;
		copy.interventionLogic = this.interventionLogic;
		copy.risksAndAssumptions = this.risksAndAssumptions;
		copy.parentSpecificObjective = parentSpecificObjective;
		copy.indicators = copyIndicators(context);

		copy.activities = new ArrayList<LogFrameActivity>();

		for (final LogFrameActivity activity : activities) {
			copy.activities.add(activity.copy(copy, context));
		}

		copy.group = context.getGroupCopy(this.group);
		copy.position = this.position;

		return copy;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public String getInterventionLogic() {
		return interventionLogic;
	}

	public void setInterventionLogic(String interventionLogic) {
		this.interventionLogic = interventionLogic;
	}

	public SpecificObjective getParentSpecificObjective() {
		return parentSpecificObjective;
	}

	public void setParentSpecificObjective(SpecificObjective parentSpecificObjective) {
		this.parentSpecificObjective = parentSpecificObjective;
	}

	public List<LogFrameActivity> getActivities() {
		return activities;
	}

	public void setActivities(List<LogFrameActivity> activities) {
		this.activities = activities;
	}

}
