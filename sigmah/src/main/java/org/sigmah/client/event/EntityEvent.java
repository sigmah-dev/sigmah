/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.event;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;

import org.sigmah.client.EventBus;
import org.sigmah.client.EventBus.NamedEventType;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.SiteDTO;

/**
 * Application-wide event that signals a change to an entity. 
 * 
 *
 * @author Alex Bertram
 */
public class EntityEvent<T extends EntityDTO> extends BaseEvent {
    private int id;
    private String entityName;
    private T entity;

	public static final EventType CREATED = new EventBus.NamedEventType("SiteCreated");
    public static final EventType UPDATED = new EventBus.NamedEventType("SiteChanged");
	public static final EventType SELECTED = new EventBus.NamedEventType("SiteSelected");

    /**
     *
     * @param type
     * @param source the component which fired the event
     * @param site
     */
    public EntityEvent(EventType type, Object source, T site) {
        super(type);
        this.setSource(source);
        this.entity = site;
        this.entityName = site.getEntityName();
        this.id = site.getId();
    }

    public EntityEvent(EventType type, Object source, String entityName, int entityId) {
        super(type);
        this.setSource(source);
        this.entityName = entityName;
        this.id = entityId;
    }
    
    public String getEntityName() {
    	return entityName;
    }

    /**
     * 
     * @return the affected entity. May be {@code null} if only the {@code id} was available.
     */
    public T getEntity() {
        return entity;
    }

    public int getSiteId() {
        return id;
    }
}
