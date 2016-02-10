package org.sigmah.client.ui.view.pivot.table;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import com.extjs.gxt.ui.client.event.GridEvent;
import com.google.gwt.dom.client.Element;
import org.sigmah.shared.dto.pivot.content.PivotTableData;

public class PivotGridHeaderEvent extends GridEvent<PivotGridPanel.PivotTableRow> {

	private final PivotTableData.Axis axis;
	
	public enum IconTarget {
		ZOOM,
		EDIT,
		SWAP,
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
		} else if(PivotResources.INSTANCE.css().swapIcon().equals(targetClass)) {
			return IconTarget.SWAP;
		} else {
			return IconTarget.NONE;
		}
	}

}
