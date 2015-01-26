package org.sigmah.offline.js;

import java.util.List;

import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.logframe.ExpectedResultDTO;
import org.sigmah.shared.dto.logframe.LogFrameActivityDTO;
import org.sigmah.shared.dto.logframe.LogFrameGroupDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsDate;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class LogFrameActivityJS extends JavaScriptObject {// LogFrameElementJS {
	
	protected LogFrameActivityJS() {
	}
	
	public static LogFrameActivityJS toJavaScript(LogFrameActivityDTO logFrameActivityDTO) {
		final LogFrameActivityJS logFrameActivityJS = Values.createJavaScriptObject(LogFrameActivityJS.class);
		
		logFrameActivityJS.setId(logFrameActivityDTO.getId());
		logFrameActivityJS.setCode(logFrameActivityDTO.getCode());
		logFrameActivityJS.setPosition(logFrameActivityDTO.getPosition());
		logFrameActivityJS.setRisksAndAssumptions(logFrameActivityDTO.getRisksAndAssumptions());
		logFrameActivityJS.setGroup(logFrameActivityDTO.getGroup());
		logFrameActivityJS.setIndicators(logFrameActivityDTO.getIndicators());
		logFrameActivityJS.setAdvancement(logFrameActivityDTO.getAdvancement());
		logFrameActivityJS.setTitle(logFrameActivityDTO.getTitle());
		logFrameActivityJS.setStartDate(Values.toJsDate(logFrameActivityDTO.getStartDate()));
		logFrameActivityJS.setEndDate(Values.toJsDate(logFrameActivityDTO.getEndDate()));
		logFrameActivityJS.setParentExpectedResult(logFrameActivityDTO.getParentExpectedResult());
		logFrameActivityJS.setLabel(logFrameActivityDTO.getLabel());
		
		return logFrameActivityJS;
	}
	
	public LogFrameActivityDTO toDTO() {
		final LogFrameActivityDTO logFrameActivityDTO = new LogFrameActivityDTO();
		
		logFrameActivityDTO.setId(getId());
		logFrameActivityDTO.setCode(getCode());
		logFrameActivityDTO.setPosition(getPosition());
		logFrameActivityDTO.setRisksAndAssumptions(getRisksAndAssumptions());
		logFrameActivityDTO.setAdvancement(getAdvancement());
		logFrameActivityDTO.setTitle(getTitle());
		logFrameActivityDTO.setStartDate(Values.toDate(getStartDate()));
		logFrameActivityDTO.setEndDate(Values.toDate(getEndDate()));
		logFrameActivityDTO.setLabel(getLabel());
		
		return logFrameActivityDTO;
	}
	
	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

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

	public Integer getAdvancement() {
		return Values.getInteger(this, "advancement");
	}

	public void setAdvancement(Integer advancement) {
		Values.setInteger(this, "advancement", advancement);
	}

	public native String getTitle() /*-{
		return this.title;
	}-*/;

	public native void setTitle(String title) /*-{
		this.title = title;
	}-*/;

	public native JsDate getStartDate() /*-{
		return this.startDate;
	}-*/;

	public native void setStartDate(JsDate startDate) /*-{
		this.startDate = startDate;
	}-*/;

	public native JsDate getEndDate() /*-{
		return this.endDate;
	}-*/;

	public native void setEndDate(JsDate endDate) /*-{
		this.endDate = endDate;
	}-*/;

	public native int getParentExpectedResult() /*-{
		return this.parentExpectedResult;
	}-*/;

	public native void setParentExpectedResult(int parentExpectedResult) /*-{
		this.parentExpectedResult = parentExpectedResult;
	}-*/;
	public void setParentExpectedResult(ExpectedResultDTO parentExpectedResult) {
		if(parentExpectedResult != null) {
			setParentExpectedResult(parentExpectedResult.getId());
		}
	}

	public native String getLabel() /*-{
		return this.label;
	}-*/;

	public native void setLabel(String label) /*-{
		this.label = label;
	}-*/;
}
