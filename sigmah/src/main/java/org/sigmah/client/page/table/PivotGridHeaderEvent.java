package org.sigmah.client.page.table;

import org.sigmah.shared.report.content.PivotTableData;

import com.extjs.gxt.ui.client.event.GridEvent;
import com.google.gwt.dom.client.Element;

public class PivotGridHeaderEvent extends GridEvent<PivotGridPanel.PivotTableRow> {

	private PivotTableData.Axis axis;
	
	public enum IconTarget {
		ZOOM,
		EDIT,
		NONE
	}
	
	public PivotGridHeaderEvent(GridEvent<PivotGridPanel.PivotTableRow> event, PivotTableData.Axis axis) {
		super(event.getGrid(), event.getEvent());
		this.axis = axis;
	}

	public PivotTableData.Axis getAxis() {
		return axis;
	}
	
	public IconTarget getIconTarget() {
		Element targetElement = event.getEventTarget().cast();
		String targetClass = targetElement.getClassName();
		
		if(PivotResources.INSTANCE.css().zoomIcon().equals(targetClass)) {
			return IconTarget.ZOOM;
		} else if(PivotResources.INSTANCE.css().editIcon().equals(targetClass)) {
			return IconTarget.EDIT;
		} else {
			return IconTarget.NONE;
		}
	}

}
