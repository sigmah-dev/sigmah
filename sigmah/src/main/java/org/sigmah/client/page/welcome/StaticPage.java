/*
 * All Sigmah code is released under the GNU General Public License v3 See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.welcome;

import com.extjs.gxt.ui.client.widget.ContentPanel;

import org.sigmah.client.event.NavigationEvent.NavigationError;
import org.sigmah.client.page.*;

/*
 * @author Alex Bertram
 */

public class StaticPage extends ContentPanel implements Page {

    public StaticPage() {
        this.setHeaderVisible(false);
    }

    public void navigate(StaticPageState place) {
        this.setUrl("static/" + place.getKeyword() + ".html");
    }

    public PageId getPageId() {
        return Frames.Static;
    }

    public Object getWidget() {
        return this;
    }

    @Override
    public void requestToNavigateAway(PageState place, final NavigationCallback callback) {
        callback.onDecided(NavigationError.NONE);
    }

    public String beforeWindowCloses() {
        return null;
    }

    public void shutdown() {

    }

    public boolean navigate(PageState place) {
        return false;
    }
}
