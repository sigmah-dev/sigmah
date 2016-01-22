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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.result.SiteResult;
import org.sigmah.shared.dto.referential.DimensionType;
import org.sigmah.shared.util.Filter;

/**
 * Retrieves a list of sites based on the provided filter and limits.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetSites extends PagingGetCommand<SiteResult> {

	private Filter filter;

	private Integer seekToSiteId;

	public GetSites() {
		// Serialization.
		filter = new Filter();
	}
	
	public GetSites(Filter filter) {
		this.filter = filter;
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		assert filter != null : "Filter cannot be null! Use new Filter() to create an empty filter";
		this.filter = filter;
	}
	
	public Integer getSeekToSiteId() {
		return seekToSiteId;
	}

	public void setSeekToSiteId(Integer seekToSiteId) {
		this.seekToSiteId = seekToSiteId;
	}

	/**
	 * Make a copy of this command.
	 * 
	 * @return A new intance of GetSites with the same values.
	 */
	public GetSites createClone() {
		GetSites c = new GetSites();
		c.filter = new Filter(filter);
		c.setLimit(getLimit());
		c.setOffset(getOffset());
		c.setSortInfo(getSortInfo());

		return c;
	}

	public static GetSites byId(int siteId) {
		GetSites cmd = new GetSites();
		cmd.getFilter().addRestriction(DimensionType.Site, siteId);

		return cmd;
	}

	public static GetSites byActivity(int activityId) {
		GetSites cmd = new GetSites();
		cmd.getFilter().onActivity(activityId);

		return cmd;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("filter", filter);
		builder.append("seekToSiteId", seekToSiteId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		GetSites getSites = (GetSites) o;

		if (!filter.equals(getSites.filter))
			return false;
		if (seekToSiteId != null ? !seekToSiteId.equals(getSites.seekToSiteId) : getSites.seekToSiteId != null)
			return false;

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int result = filter.hashCode();
		result = 31 * result + (seekToSiteId != null ? seekToSiteId.hashCode() : 0);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOffset(int offset) {
		if (offset != getOffset()) {
			super.setOffset(offset);
			seekToSiteId = null;
		}
	}
}
