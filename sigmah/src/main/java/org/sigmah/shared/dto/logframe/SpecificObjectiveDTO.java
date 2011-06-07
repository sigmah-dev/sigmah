package org.sigmah.shared.dto.logframe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.sigmah.client.page.project.logframe.CodePolicy;
import org.sigmah.client.page.project.logframe.grid.Row.Positionable;
import org.sigmah.shared.dto.EntityDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * DTO mapping class for entity logframe.SpecificObjective.
 * 
 * @author tmi
 * 
 */
public class SpecificObjectiveDTO extends LogFrameElementDTO implements EntityDTO, Positionable {

    private static final long serialVersionUID = -5441820698955180264L;

    public SpecificObjectiveDTO() {
    	setExpectedResults(new ArrayList<ExpectedResultDTO>());
    }
    
    @Override
    public String getEntityName() {
        return "logframe.SpecificObjective";
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
     * Sets the attribute <code>label</code> to display this element in a
     * selection window.
     */
    public void setLabel(String label) {
        set("label", label);
    }

    @Override
    public String getLabel() {
        return get("label");
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("SpecificObjectiveDTO [");
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
        sb.append(" ; intervention logic = ");
        sb.append(getInterventionLogic());
        sb.append(" ; risks = ");
        sb.append(getRisks());
        sb.append(" ; assumptions = ");
        sb.append(getAssumptions());
        sb.append(" ; expected results = (\n");
        if (getExpectedResults() != null) {
            for (final ExpectedResultDTO r : getExpectedResults()) {
                sb.append(r);
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

        if (!(obj instanceof SpecificObjectiveDTO)) {
            return false;
        }

        final SpecificObjectiveDTO other = (SpecificObjectiveDTO) obj;
        return getClientSideId() == other.getClientSideId();
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
     *            The result to remove.
     * @return If the result has been removed.
     */
    public boolean removeExpectedResult(ExpectedResultDTO result) {
    	return getExpectedResults().remove(result);
    }

    @Override
    public String getFormattedCode() {
    	final StringBuilder sb = new StringBuilder();
    	sb.append(CodePolicy.getLetter(getCode(), true, 1));
    	sb.append(".");

    	return sb.toString();
    }
    
    @Override
    public String getDescription() {
    	return getInterventionLogic();
    }
    
}
