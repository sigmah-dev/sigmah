package org.sigmah.shared.command;

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
