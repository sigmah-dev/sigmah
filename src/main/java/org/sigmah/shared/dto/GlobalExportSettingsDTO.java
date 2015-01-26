package org.sigmah.shared.dto;

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.util.ExportUtils;

/**
 * Global export settings DTO.
 * 
 * @author sherzod
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GlobalExportSettingsDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -6776274548842374318L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("exportFormat", getExportFormat());
		builder.append("defaultOrganizationExportFormat", getDefaultOrganizationExportFormat());
		builder.append("autoExportFrequency", getAutoExportFrequency());
		builder.append("autoDeleteFrequency", getAutoDeleteFrequency());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "GlobalExportSettings";
	}

	@Override
	public Integer getId() {
		return get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	public ExportUtils.ExportFormat getExportFormat() {
		return get("exportFormat");
	}

	public void setExportFormat(ExportUtils.ExportFormat exportFormat) {
		set("exportFormat", exportFormat);
	}

	public ExportUtils.ExportFormat getDefaultOrganizationExportFormat() {
		return get("defaultOrganizationExportFormat");
	}

	public void setDefaultOrganizationExportFormat(ExportUtils.ExportFormat defaultOrganizationExportFormat) {
		set("defaultOrganizationExportFormat", defaultOrganizationExportFormat);
	}

	public Integer getAutoExportFrequency() {
		return get("autoExportFrequency");
	}

	public void setAutoExportFrequency(Integer autoExportFrequency) {
		set("autoExportFrequency", autoExportFrequency);
	}

	public Integer getAutoDeleteFrequency() {
		return get("autoDeleteFrequency");
	}

	public void setAutoDeleteFrequency(Integer autoDeleteFrequency) {
		set("autoDeleteFrequency", autoDeleteFrequency);
	}

	// project model list
	public List<ProjectModelDTO> getProjectModelsDTO() {
		return get("projectModelsDTO");
	}

	public void setProjectModelsDTO(List<ProjectModelDTO> models) {
		set("projectModelsDTO", models);
	}

}
