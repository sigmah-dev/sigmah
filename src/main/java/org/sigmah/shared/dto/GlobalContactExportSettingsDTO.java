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

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.util.ExportUtils;

public class GlobalContactExportSettingsDTO extends AbstractModelDataEntityDTO<Integer> {

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

	// contact model list
	public List<ContactModelDTO> getContactModelsDTO() {
		return get("contactModelsDTO");
	}

	public void setContactModelsDTO(List<ContactModelDTO> models) {
		set("contactModelsDTO", models);
	}

}
