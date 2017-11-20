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
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetImportationSchemeModels extends AbstractCommand<ListResult<ImportationSchemeModelDTO>> {

	private Integer importationSchemeId;
	private Integer projectModelId;
	private Integer orgUnitModelId;
	private Integer contactModelId;

	public GetImportationSchemeModels() {
		// Serialization.
	}
	
	public GetImportationSchemeModels(IsModel model) {
		if(model instanceof ProjectModelDTO) {
			this.projectModelId = model.getId();
			
		} else if(model instanceof OrgUnitModelDTO) {
			this.orgUnitModelId = model.getId();
		} else if(model instanceof ContactModelDTO) {
			this.contactModelId = model.getId();
		}
	}

	/**
	 * @return the schemaId
	 */
	public Integer getImportationSchemeId() {
		return importationSchemeId;
	}

	/**
	 * @param importationSchemeId
	 *          the schemaId to set
	 */
	public void setImportationSchemeId(Integer importationSchemeId) {
		this.importationSchemeId = importationSchemeId;
	}

	/**
	 * @return the projectModelId
	 */
	public Integer getProjectModelId() {
		return projectModelId;
	}

	/**
	 * @param projectModelId
	 *          the projectModelId to set
	 */
	public void setProjectModelId(Integer projectModelId) {
		this.projectModelId = projectModelId;
	}

	public Integer getOrgUnitModelId() {
		return orgUnitModelId;
	}

	public void setOrgUnitModelId(Integer orgUnitModelId) {
		this.orgUnitModelId = orgUnitModelId;
	}

	public Integer getContactModelId() {
		return contactModelId;
	}

	public void setContactModelId(Integer contactModelId) {
		this.contactModelId = contactModelId;
	}

}
