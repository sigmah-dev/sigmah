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

import java.util.Map;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.util.ExportUtils;

public class UpdateGlobalContactExportSettingsCommand extends AbstractCommand<VoidResult> {

	private int organizationId;
	private Map<Integer, Boolean> fieldsMap;
	private ExportUtils.ExportFormat exportFormat;
	private ExportUtils.ExportFormat defaultOrganizationExportFormat;
	private Integer autoExportFrequency;
	private Integer autoDeleteFrequency;
	private boolean updateDefaultExportFormat;

	public UpdateGlobalContactExportSettingsCommand() {
		// Serialization.
	}

	public UpdateGlobalContactExportSettingsCommand(Map<Integer, Boolean> fieldsMap) {
		this.fieldsMap = fieldsMap;
	}

	public Map<Integer, Boolean> getFieldsMap() {
		return fieldsMap;
	}

	public void setFieldsMap(Map<Integer, Boolean> fieldsMap) {
		this.fieldsMap = fieldsMap;
	}

	public ExportUtils.ExportFormat getExportFormat() {
		return exportFormat;
	}

	public void setExportFormat(ExportUtils.ExportFormat exportFormat) {
		this.exportFormat = exportFormat;
	}

	public Integer getAutoExportFrequency() {
		return autoExportFrequency;
	}

	public void setAutoExportFrequency(Integer autoExportFrequency) {
		this.autoExportFrequency = autoExportFrequency;
	}

	public Integer getAutoDeleteFrequency() {
		return autoDeleteFrequency;
	}

	public void setAutoDeleteFrequency(Integer autoDeleteFrequency) {
		this.autoDeleteFrequency = autoDeleteFrequency;
	}

	public int getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}

	public boolean getUpdateDefaultExportFormat() {
		return updateDefaultExportFormat;
	}

	public void setUpdateDefaultExportFormat(boolean updateDefaultExportFormat) {
		this.updateDefaultExportFormat = updateDefaultExportFormat;
	}

	public ExportUtils.ExportFormat getDefaultOrganizationExportFormat() {
		return defaultOrganizationExportFormat;
	}

	public void setDefaultOrganizationExportFormat(ExportUtils.ExportFormat defaultOrganizationExportFormat) {
		this.defaultOrganizationExportFormat = defaultOrganizationExportFormat;
	}

}
