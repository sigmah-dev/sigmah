package org.sigmah.shared.dto;

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
