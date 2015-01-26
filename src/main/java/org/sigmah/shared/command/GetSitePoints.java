package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.SitePointList;
import org.sigmah.shared.dto.referential.DimensionType;
import org.sigmah.shared.util.Filter;

/**
 * GetSitePoints Command.
 * 
 * @author Alex Bertram (akbertram@gmail.com)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetSitePoints extends AbstractCommand<SitePointList> {

	private Filter filter;

	protected GetSitePoints() {
		// Serialization.
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
