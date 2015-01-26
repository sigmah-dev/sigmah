package org.sigmah.client.event;

import java.util.Map;
import java.util.Map.Entry;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;

/**
 * Application-wide event that signals a change to
 * indicators that allows listeners to update their UI
 * 
 * @author alexander
 *
 */
public class IndicatorEvent extends BaseEvent {
	

	public static final EventType CHANGED = new EventType();
	
	
	public enum ChangeType {
		CREATED, UPDATED, DELETED
	}
	
	private int entityId;
	private ChangeType changeType;
	private Map<String, Object> changes;
	
	/**
	 * 
	 * @param eventType the entity-specific event type
	 * @param source the component originating this event. This can be checked to avoid processing events 
	 * sent by yourself
	 */
	public IndicatorEvent(EventType eventType, Object source) {
		super(eventType);
		this.setSource(source);
	}

	public int getEntityId() {
		return entityId;
	}

	public void setEntityId(int id) {
		this.entityId = id;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	public void setChanges(Map<String, Object> properties) {
		this.changes = properties;
	}
	
	public Map<String, Object> getChanges() {
		return changes;
	}
	
	public void applyChanges(ModelData indicator) {
		for(Entry<String, Object> change : changes.entrySet()) {
			indicator.set(change.getKey(), change.getValue());
		}
	}
}
