package org.sigmah.shared.dto.importation;

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

import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * ImportationSchemeModelDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ImportationSchemeModelDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8661322283680779114L;

	public static final String ENTITY_NAME = "importation.ImportationSchemeModel";
	public static final String IMPORTATION_SCHEME = "importationSchemeDTO";
	public static final String PROJECT_MODEL = "projectModelDTO";
	public static final String ORGUNIT_MODEL = "orgUnitModelDTO";
	public static final String CONTACT_MODEL = "contactModelDTO";
	public static final String VARIABLES = "variableFlexibleElementsDTO";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public VariableFlexibleElementDTO getIdKey() {
		for (VariableFlexibleElementDTO varfleDTO : getVariableFlexibleElementsDTO()) {
			if (varfleDTO.getIsKey()) {
				return varfleDTO;
			}
		}
		return null;
	}

	public ImportationSchemeDTO getImportationSchemeDTO() {
		return get("importationSchemeDTO");
	}

	public void setImportationSchemeDTO(ImportationSchemeDTO importationSchemeDTO) {
		set("importationSchemeDTO", importationSchemeDTO);
	}

	public ProjectModelDTO getProjectModelDTO() {
		return get("projectModelDTO");
	}

	public void setProjectModelDTO(ProjectModelDTO projectModelDTO) {
		set("projectModelDTO", projectModelDTO);
	}

	public OrgUnitModelDTO getOrgUnitModelDTO() {
		return get("orgUnitModelDTO");
	}

	public void setOrgUnitModelDTO(OrgUnitModelDTO orgUnitModelDTO) {
		set("orgUnitModelDTO", orgUnitModelDTO);
	}

	public ContactModelDTO getContactModelDTO() {
		return get("contactModelDTO");
	}

	public void setContactModelDTO(ContactModelDTO contactModelDTO) {
		set("contactModelDTO", contactModelDTO);
	}

	public List<VariableFlexibleElementDTO> getVariableFlexibleElementsDTO() {
		return get("variableFlexibleElementsDTO");
	}

	public void setVariableFlexibleElementsDTO(List<VariableFlexibleElementDTO> variableFlexibleElementsDTO) {
		set("variableFlexibleElementsDTO", variableFlexibleElementsDTO);
	}

}
