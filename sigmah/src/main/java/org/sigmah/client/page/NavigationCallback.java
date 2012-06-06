/*
 * All Sigmah code is released under the GNU General Public License v3 See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page;

import org.sigmah.client.event.NavigationEvent.NavigationError;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public interface NavigationCallback {

    public void onDecided(NavigationError navigationError);

}
