/**
 * 
 */
package org.sigmah.shared.command;

import org.sigmah.shared.command.result.SiteResult;
import org.sigmah.shared.dao.Filter;

/**
 * 
 * A command to get a list of sites for a project by using a filter.
 * 
 * @author HUZHE
 *
 */
public class GetSitesCount implements Command<SiteResult> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Filter filter;
	
	

	public GetSitesCount() {
		
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
	 * @param filter the filter to set
	 */
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	
	
	
	
}
