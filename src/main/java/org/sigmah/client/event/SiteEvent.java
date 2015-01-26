package org.sigmah.client.event;


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
