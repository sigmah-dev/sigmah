package org.sigmah.shared.command.result;

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
