package org.sigmah.offline.js;

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

import com.google.gwt.core.client.JsArray;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.shared.command.UpdateMonitoredPoints;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;

/**
 * JavaScript version of the {@link UpdateMonitoredPoints} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class UpdateMonitoredPointsJS extends CommandJS {
	
	protected UpdateMonitoredPointsJS() {
	}
	
	public static UpdateMonitoredPointsJS toJavaScript(UpdateMonitoredPoints updateMonitoredPoints) {
		final UpdateMonitoredPointsJS updateMonitoredPointsJS = Values.createJavaScriptObject(UpdateMonitoredPointsJS.class);
		
		updateMonitoredPointsJS.setList(updateMonitoredPoints.getList());
		
		return updateMonitoredPointsJS;
	}
	
	public UpdateMonitoredPoints toUpdateMonitoredPoints() {
		return new UpdateMonitoredPoints(getList());
	}
	
	public native JsArray<MonitoredPointJS> getArray() /*-{
		return this.list;
	}-*/;
	
	public native void setArray(JsArray<MonitoredPointJS> list) /*-{
		this.list = list;
	}-*/;
	
	public List<MonitoredPointDTO> getList() {
		if(getArray() != null) {
			final ArrayList<MonitoredPointDTO> list = new ArrayList<MonitoredPointDTO>();
			
			final JsArray<MonitoredPointJS> array = getArray();
			for(int index = 0; index < array.length(); index++) {
				list.add(array.get(index).toDTO());
			}
			
			return list;
		}
		return null;
	}
	
	public void setList(List<MonitoredPointDTO> list) {
		if(list != null) {
			final JsArray<MonitoredPointJS> array = Values.createTypedJavaScriptArray(MonitoredPointJS.class);
			
			for(final MonitoredPointDTO monitoredPoint : list) {
				array.push(MonitoredPointJS.toJavaScript(monitoredPoint));
			}
			
			setArray(array);
		}
	}
}
