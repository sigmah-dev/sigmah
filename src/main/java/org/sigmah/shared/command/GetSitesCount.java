package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.SiteResult;
import org.sigmah.shared.util.Filter;

/**
 * A command to get a list of sites for a project by using a filter.
 * 
 * @author HUZHE
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetSitesCount extends AbstractCommand<SiteResult> {

	private Filter filter;

	public GetSitesCount() {
		// Serialization.
	}

	/**
	 * @param filter
	 */
	public GetSitesCount(Filter filter) {
		super();
		this.filter = filter;
	}

	/**
	 * @return the filter
	 */
	public Filter getFilter() {
		return filter;
	}

	/**
	 * @param filter
	 *          the filter to set
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}

}
