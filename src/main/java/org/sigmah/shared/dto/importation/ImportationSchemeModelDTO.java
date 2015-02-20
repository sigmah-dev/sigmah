package org.sigmah.shared.dto.importation;

import java.util.List;

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

	public List<VariableFlexibleElementDTO> getVariableFlexibleElementsDTO() {
		return get("variableFlexibleElementsDTO");
	}

	public void setVariableFlexibleElementsDTO(List<VariableFlexibleElementDTO> variableFlexibleElementsDTO) {
		set("variableFlexibleElementsDTO", variableFlexibleElementsDTO);
	}

}
