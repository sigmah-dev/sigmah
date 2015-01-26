package org.sigmah.offline.js;

import com.google.gwt.core.client.JsArray;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.shared.command.UpdateMonitoredPoints;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;

/**
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
			final JsArray<MonitoredPointJS> array = Values.createJavaScriptArray(JsArray.class);
			
			for(final MonitoredPointDTO monitoredPoint : list) {
				array.push(MonitoredPointJS.toJavaScript(monitoredPoint));
			}
			
			setArray(array);
		}
	}
}
