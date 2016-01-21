package org.sigmah.shared.dto;

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

import java.util.Date;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.DTO;
import org.sigmah.shared.dto.value.FileDTO.LoadingScope;

/**
 * OrgUnit backup configuration DTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class BackupDTO implements DTO {

	// Input attributes.
	private Integer organizationId;
	private Integer orgUnitId;
	private LoadingScope loadingScope;

	// Output attributes.
	private String archiveFileName;
	private String orgUnitName;
	private Date creationDate;
	private boolean running;

	public BackupDTO() {
		// Serialization.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this);

		builder.append("organizationId", organizationId);
		builder.append("orgUnitId", orgUnitId);
		builder.append("loadingMode", loadingScope);

		builder.append("archiveFileName", archiveFileName);
		builder.append("creationDate", creationDate);
		builder.append("running", running);

		return builder.toString();
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(Integer organizationId) {
		this.organizationId = organizationId;
	}

	public Integer getOrgUnitId() {
		return orgUnitId;
	}

	public void setOrgUnitId(Integer orgUnitId) {
		this.orgUnitId = orgUnitId;
	}

	public LoadingScope getLoadingScope() {
		return loadingScope;
	}

	public void setLoadingScope(LoadingScope loadingScope) {
		this.loadingScope = loadingScope;
	}

	public String getArchiveFileName() {
		return archiveFileName;
	}

	public void setArchiveFileName(String archiveFileName) {
		this.archiveFileName = archiveFileName;
	}

	public String getOrgUnitName() {
		return orgUnitName;
	}

	public void setOrgUnitName(String orgUnitName) {
		this.orgUnitName = orgUnitName;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

}
