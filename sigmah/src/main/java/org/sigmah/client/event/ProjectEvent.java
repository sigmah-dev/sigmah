package org.sigmah.client.event;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;

/**
 * Event class indicating that a change has been made to a Project.
 * This can be fired via the event bus to alert other active components that they 
 * may need to update their view.
 *  
 * 
 * @author alex@bedatadriven.com
 *
 */
public class ProjectEvent extends BaseEvent {

	public static final EventType CHANGED = new EventType();
	
	private int projectId;
	
	public ProjectEvent(EventType type, int projectId) {
		super(CHANGED);
		this.projectId = projectId;
	}

	public int getProjectId() {
		return projectId;
	}

}
