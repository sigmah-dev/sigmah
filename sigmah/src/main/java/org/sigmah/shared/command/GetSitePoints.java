/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command;

import org.sigmah.shared.command.result.SitePointList;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.report.model.DimensionType;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class GetSitePoints implements Command<SitePointList> {

    private Filter filter;

    private GetSitePoints() {

    }
    
    public GetSitePoints(Filter filter) {
    	this.filter = filter;
    }

    public GetSitePoints(int activityId) {
        this.filter = new Filter();
        filter.addRestriction(DimensionType.Activity, activityId);
    }

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

    
}


