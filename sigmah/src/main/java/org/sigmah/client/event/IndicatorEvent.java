package org.sigmah.client.event;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;

public class IndicatorEvent extends BaseEvent {
	

	public static final EventType CHANGED = new EventType();

	public IndicatorEvent(Object source) {
		super(CHANGED);
		this.setSource(source);
	}


}
