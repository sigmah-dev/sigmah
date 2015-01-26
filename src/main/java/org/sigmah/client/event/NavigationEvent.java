/*
 * All Sigmah code is released under the GNU General Public License v3 See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.event;

import org.sigmah.client.page.PageState;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;

/**
 * Encapsulates information related to page navigation events.
 * 
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class NavigationEvent extends BaseEvent {

    public enum NavigationError {
        WORK_NOT_SAVED,
        EXECUTION_ERROR,
        NONE
    }

    private final PageState place;
    // An event can be created from another event, so this event is the parent of the first one
    private final NavigationEvent parentEvent;
    private NavigationError navigationError;
    private Object parentObject;

    public NavigationEvent(EventType type, PageState place, NavigationEvent parentEvent) {
        super(type);
        this.place = place;
        assert this.place != null;
        this.parentEvent = parentEvent;
        this.parentObject = null;
    }

    public NavigationEvent(EventType type, PageState place, NavigationEvent parentEvent, Object parentObject) {
        this(type, place, parentEvent);
        this.parentObject = parentObject;
    }

    public PageState getPlace() {
        return place;
    }

    public NavigationError getNavigationError() {
        if (navigationError == null)
            return NavigationError.EXECUTION_ERROR;
        return navigationError;
    }

    public void setNavigationError(NavigationError navigationError) {
        this.navigationError = navigationError;
    }

    public NavigationEvent getParentEvent() {
        return parentEvent;
    }

    public Object getParentObject() {
        return parentObject;
    }

    @Override
    public String toString() {
        return place.getPageId() + "/" + place.serializeAsHistoryToken();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NavigationEvent that = (NavigationEvent) o;
        if (place != null ? !place.equals(that.place) : that.place != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return place != null ? place.hashCode() : 0;
    }
}
