package org.sigmah.shared.dto.logframe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sigmah.client.util.ToStringBuilder;

/**
 * DTO mapping class for entity logframe.ExpectedResult.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ExpectedResultDTO extends LogFrameElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2394670766294049525L;

	public ExpectedResultDTO() {
		setActivities(new ArrayList<LogFrameActivityDTO>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "logframe.ExpectedResult";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("label", getLabel());
		builder.append("groupId", getGroup() != null ? getGroup().getId() != null ? getGroup().getId() : getGroup().getClientSideId() : null);
		builder.append("code", getCode());
		builder.append("interventionLogic", getInterventionLogic());
		builder.append("risksAndAssumptions", getRisksAndAssumptions());
		builder.append("activities", getActivities());
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
	 *          The activity to remove.
	 * @return If the activity has been removed.
	 */
	public boolean removeActivity(LogFrameActivityDTO activity) {
		return getActivities().remove(activity);
	}

	@Override
	public String getFormattedCode() {

		final StringBuilder sb = new StringBuilder();

		final SpecificObjectiveDTO parent;
		if ((parent = getParentSpecificObjective()) != null) {
			sb.append(parent.getFormattedCode());
		}

		sb.append(getCode());
		sb.append(".");

		return sb.toString();
	}

	@Override
	public String getDescription() {
		return getInterventionLogic();
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
		final List<LogFrameActivityDTO> list = get("activities");
		if (list == null) {
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
	 * Sets the attribute <code>label</code> to display this element in a selection window.
	 */
	public void setLabel(String label) {
		set("label", label);
	}

	@Override
	public String getLabel() {
		return get("label");
	}

}
