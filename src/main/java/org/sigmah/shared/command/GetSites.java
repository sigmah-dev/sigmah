package org.sigmah.shared.command;

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
