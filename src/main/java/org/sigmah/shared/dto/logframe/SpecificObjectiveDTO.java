package org.sigmah.shared.dto.logframe;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.ui.presenter.project.logframe.CodePolicy;
import org.sigmah.client.ui.view.project.logframe.grid.Row.Positionable;
import org.sigmah.client.util.ToStringBuilder;

/**
 * DTO mapping class for entity logframe.SpecificObjective.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SpecificObjectiveDTO extends LogFrameElementDTO implements Positionable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -5441820698955180264L;

	public SpecificObjectiveDTO() {
		setExpectedResults(new ArrayList<ExpectedResultDTO>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "logframe.SpecificObjective";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("label", getLabel());
		builder.append("code", getCode());
		builder.append("groupId", getGroup() != null ? getGroup().getId() != null ? getGroup().getId() : getGroup().getClientSideId() : null);
		builder.append("interventionLogic", getInterventionLogic());
		builder.append("risksAndAssumptions", getRisksAndAssumptions());
		builder.append("expectedResults", getExpectedResults());
	}

	/**
	 * Adds a new expected result to this log frame.
	 * 
	 * @return The new expected result.
	 */
	public ExpectedResultDTO addExpectedResult() {

		List<ExpectedResultDTO> expectedResults = getExpectedResults();

		// Retrieves the higher code.
		int max = 0;
		for (final ExpectedResultDTO result : expectedResults) {
			max = result.getCode() > max ? result.getCode() : max;
		}

		// Creates the expected result.
		final ExpectedResultDTO newResult = new ExpectedResultDTO();
		newResult.setCode(max + 1);
		newResult.setParentSpecificObjective(this);

		// Adds it to the local list.
		expectedResults.add(newResult);

		return newResult;
	}

	/**
	 * Removes an expected result from this objective.
	 * 
	 * @param result
	 *          The result to remove.
	 * @return If the result has been removed.
	 */
	public boolean removeExpectedResult(ExpectedResultDTO result) {
		return getExpectedResults().remove(result);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getFormattedCode() {
		final StringBuilder sb = new StringBuilder();
		sb.append(CodePolicy.getLetter(getCode(), true, 1));
		sb.append(".");

		return sb.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return getInterventionLogic();
	}

	// Objective intervention logic.
	public String getInterventionLogic() {
		return get("interventionLogic");
	}

	public void setInterventionLogic(String interventionLogic) {
		set("interventionLogic", interventionLogic);
	}

	// Objective parent log frame.
	public LogFrameDTO getParentLogFrame() {
		return get("parentLogFrame");
	}

	public void setParentLogFrame(LogFrameDTO parentLogFrameDTO) {
		set("parentLogFrame", parentLogFrameDTO);
	}

	// Objective expected results.
	public List<ExpectedResultDTO> getExpectedResults() {
		return get("expectedResults");
	}

	public void setExpectedResults(List<ExpectedResultDTO> expectedResults) {
		set("expectedResults", expectedResults);
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
