package org.sigmah.shared.command.result;

import java.util.Arrays;
import java.util.List;

import org.sigmah.shared.dto.SiteDTO;

/**
 * Result from the GetSites command.
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see org.sigmah.shared.command.GetSites
 */
public class SiteResult extends PagingResult<SiteDTO> {

	private int siteCount;

	public SiteResult(int siteCount) {
		this.siteCount = siteCount;
	}

	public SiteResult() {
		// Serialization.
	}

	public SiteResult(List<SiteDTO> data) {
		super(data);
	}

	public SiteResult(SiteDTO... sites) {
		super(Arrays.asList(sites));
	}

	public SiteResult(List<SiteDTO> data, int offset, int totalCount) {
		super(data, offset, totalCount);
	}

	/**
	 * @return the siteCount
	 */
	public int getSiteCount() {
		return siteCount;
	}

	/**
	 * @param siteCount
	 *          the siteCount to set
	 */
	public void setSiteCount(int siteCount) {
		this.siteCount = siteCount;
	}

}
