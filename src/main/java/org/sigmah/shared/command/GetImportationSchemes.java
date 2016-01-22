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
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetImportationSchemes extends AbstractCommand<ListResult<ImportationSchemeDTO>> {

	private Integer schemaId;

	private Integer projectModelId;

	private Integer orgUnitModelId;

	private Boolean excludeExistent = false;

	public GetImportationSchemes() {
		// Serialization.
	}

	public Integer getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(Integer schemaId) {
		this.schemaId = schemaId;
	}

	/**
	 * @return the excludeExistent
	 */
	public Boolean getExcludeExistent() {
		return excludeExistent;
	}

	/**
	 * @param excludeExistent
	 *          the excludeExistent to set
	 */
	public void setExcludeExistent(Boolean excludeExistent) {
		this.excludeExistent = excludeExistent;
	}

	/**
	 * @return the modelId
	 */
	public Integer getProjectModelId() {
		return projectModelId;
	}

	/**
	 * @param modelId
	 *          the modelId to set
	 */
	public void setProjectModelId(Integer modelId) {
		this.projectModelId = modelId;
	}

	/**
	 * @return the orgUnitModelId
	 */
	public Integer getOrgUnitModelId() {
		return orgUnitModelId;
	}

	/**
	 * @param orgUnitModelId
	 *          the orgUnitModelId to set
	 */
	public void setOrgUnitModelId(Integer orgUnitModelId) {
		this.orgUnitModelId = orgUnitModelId;
	}

}
