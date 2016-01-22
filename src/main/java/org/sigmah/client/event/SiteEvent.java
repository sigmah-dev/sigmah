package org.sigmah.client.event;

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


import com.google.gwt.event.shared.GwtEvent;
import org.sigmah.client.event.handler.SiteHandler;
import org.sigmah.shared.dto.SiteDTO;

/**
 * Application-wide event that signals a change to a Site. 
 *
 * @author Alexander Bertram (akbertram@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class SiteEvent extends GwtEvent<SiteHandler> {
	
	private static Type<SiteHandler> TYPE;

	public static enum Action {
		CREATED,
		UPDATED,
		SELECTED,
		DELETED,
		MAIN_SITE_UPDATED,
		MAIN_SITE_CREATED
	}
	
	private Action action;
    private int id;
    private String entityName;
    private SiteDTO entity;

    /**
     *
     * @param action 
     * @param source the component which fired the event
     * @param site
     */
    public SiteEvent(Action action, Object source, SiteDTO site) {
		this(action, source, site.getEntityName(), site.getId());
		this.entity = site;
    }

    public SiteEvent(Action action, Object source, String entityName, int entityId) {
        this.action = action;
        this.setSource(source);
        this.entityName = entityName;
        this.id = entityId;
    }
	
	// --
	// GWT event method
	// --
	
	public static Type<SiteHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<SiteHandler>();
		}
		return TYPE;
	}
	
	@Override
	public Type<SiteHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(SiteHandler handler) {
		handler.handleEvent(this);
	}
    
	// --
	// Specific methods
	// --

	public Action getAction() {
		return action;
	}
	
	/**
	 * Get the target entity name.
	 * @return 
	 */
    public String getEntityName() {
    	return entityName;
    }

    /**
     * 
     * @return the affected entity. May be {@code null} if only the {@code id} was available.
     */
    public SiteDTO getEntity() {
        return entity;
    }

    public int getSiteId() {
        return id;
    }
}
