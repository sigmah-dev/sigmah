package org.sigmah.offline.js;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.logframe.ExpectedResultDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.logframe.LogFrameGroupDTO;
import org.sigmah.shared.dto.logframe.SpecificObjectiveDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class SpecificObjectiveJS extends JavaScriptObject {
	
	protected SpecificObjectiveJS() {
	}
	
	public static SpecificObjectiveJS toJavaScript(SpecificObjectiveDTO specificObjectiveDTO) {
		final SpecificObjectiveJS specificObjectiveJS = Values.createJavaScriptObject(SpecificObjectiveJS.class);
		
		specificObjectiveJS.setId(specificObjectiveDTO.getId());
		specificObjectiveJS.setCode(specificObjectiveDTO.getCode());
		specificObjectiveJS.setPosition(specificObjectiveDTO.getPosition());
		specificObjectiveJS.setRisksAndAssumptions(specificObjectiveDTO.getRisksAndAssumptions());
		specificObjectiveJS.setGroup(specificObjectiveDTO.getGroup());
		specificObjectiveJS.setIndicators(specificObjectiveDTO.getIndicators());
		specificObjectiveJS.setInterventionLogic(specificObjectiveDTO.getInterventionLogic());
		specificObjectiveJS.setParentLogFrame(specificObjectiveDTO.getParentLogFrame());
		specificObjectiveJS.setExpectedResults(specificObjectiveDTO.getExpectedResults());
		specificObjectiveJS.setLabel(specificObjectiveDTO.getLabel());
		
		return specificObjectiveJS;
	}
	
	public SpecificObjectiveDTO toDTO(Map<Integer, LogFrameGroupDTO> groupMap) {
		final SpecificObjectiveDTO specificObjectiveDTO = new SpecificObjectiveDTO();
		
		specificObjectiveDTO.setId(getId());
		specificObjectiveDTO.setCode(getCode());
		specificObjectiveDTO.setPosition(getPosition());
		specificObjectiveDTO.setRisksAndAssumptions(getRisksAndAssumptions());
		specificObjectiveDTO.setInterventionLogic(getInterventionLogic());
		specificObjectiveDTO.setExpectedResults(getExpectedResults(specificObjectiveDTO, groupMap));
		specificObjectiveDTO.setLabel(getLabel());
		
		return specificObjectiveDTO;
	}
	
	public Integer getId() {
		return Values.getInteger(this, "id");
	}

	public void setId(Integer id) {
		Values.setInteger(this, "id", id);
	}

	public Integer getCode() {
		return Values.getInteger(this, "code");
	}

	public void setCode(Integer code) {
		Values.setInteger(this, "code", code);
	}

	public Integer getPosition() {
		return Values.getInteger(this, "position");
	}

	public void setPosition(Integer position) {
		Values.setInteger(this, "position", position);
	}

	public native String getRisksAndAssumptions() /*-{
		return this.risksAndAssumptions;
	}-*/;

	public native void setRisksAndAssumptions(String risksAndAssumptions) /*-{
		this.risksAndAssumptions = risksAndAssumptions;
	}-*/;

	public boolean hasGroup() {
		return Values.isDefined(this, "group");
	}
	
	public native int getGroup() /*-{
		return this.group;
	}-*/;

	public native void setGroup(int group) /*-{
		this.group = group;
	}-*/;
	
	public void setGroup(LogFrameGroupDTO logFrameGroupDTO) {
		if(logFrameGroupDTO != null) {
			setGroup(logFrameGroupDTO.getId());
		}
	}

	public native JsArrayInteger getIndicators() /*-{
		return this.indicators;
	}-*/;

	public native void setIndicators(JsArrayInteger indicators) /*-{
		this.indicators = indicators;
	}-*/;
	
	public void setIndicators(List<IndicatorDTO> indicators) {
		if(indicators != null) {
			final JsArrayInteger array = (JsArrayInteger) JavaScriptObject.createArray();

			for(final IndicatorDTO indicator : indicators) {
				array.push(indicator.getId());
			}

			setIndicators(array);
		}
	}

	public native String getInterventionLogic() /*-{
		return this.interventionLogic;
	}-*/;

	public native void setInterventionLogic(String interventionLogic) /*-{
		this.interventionLogic = interventionLogic;
	}-*/;

	public native int getParentLogFrame() /*-{
		return this.parentLogFrame;
	}-*/;

	public native void setParentLogFrame(int parentLogFrame) /*-{
		this.parentLogFrame = parentLogFrame;
	}-*/;

	public void setParentLogFrame(LogFrameDTO parentLogFrame) {
		if(parentLogFrame != null) {
			setParentLogFrame(parentLogFrame.getId());
		}
	}

	public native JsArray<ExpectedResultJS> getExpectedResults() /*-{
		return this.expectedResults;
	}-*/;

	public native void setExpectedResults(JsArray<ExpectedResultJS> expectedResults) /*-{
		this.expectedResults = expectedResults;
	}-*/;
	
	public List<ExpectedResultDTO> getExpectedResults(SpecificObjectiveDTO parentSpecificObjective, Map<Integer, LogFrameGroupDTO> groupMap) {
		final JsArray<ExpectedResultJS> expectedResults = getExpectedResults();
		if(expectedResults != null) {
			final ArrayList<ExpectedResultDTO> list = new ArrayList<ExpectedResultDTO>();
			for(int index = 0; index < expectedResults.length(); index++) {
				final ExpectedResultJS expectedResultJS = expectedResults.get(index);
				final ExpectedResultDTO expectedResultDTO = expectedResultJS.toDTO(groupMap);
				expectedResultDTO.setParentSpecificObjective(parentSpecificObjective);
				if(expectedResultJS.hasGroup()) {
					expectedResultDTO.setGroup(groupMap.get(expectedResultJS.getGroup()));
				}
				list.add(expectedResultDTO);
			}
			return list;
		}
		return null;
	}
	
	public void setExpectedResults(List<ExpectedResultDTO> expectedResults) {
		if(expectedResults != null) {
			final JsArray<ExpectedResultJS> array = (JsArray<ExpectedResultJS>) JavaScriptObject.createArray();

			for(final ExpectedResultDTO expectedResult : expectedResults) {
				array.push(ExpectedResultJS.toJavaScript(expectedResult));
			}
			
			setExpectedResults(array);
		}
	}

	public native String getLabel() /*-{
		return this.label;
	}-*/;

	public native void setLabel(String label) /*-{
		this.label = label;
	}-*/;
}
