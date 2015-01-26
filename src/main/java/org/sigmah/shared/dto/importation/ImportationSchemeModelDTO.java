package org.sigmah.shared.dto.importation;

import java.util.List;

import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ImportationSchemeModelDTO extends BaseModelData implements EntityDTO {

	private static final long serialVersionUID = 8661322283680779114L;

	@Override
	public int getId() {
		if (get("id") != null) {
			return (Integer) get("id");
		} else {
			return -1;
		}
	}

	public void setId(int id) {
		set("id", id);
	}

	@Override
	public String getEntityName() {
		return "importation.ImportationSchemeModel";
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

	public VariableFlexibleElementDTO getIdKey() {
		for (VariableFlexibleElementDTO varfleDTO : getVariableFlexibleElementsDTO()) {
			if (varfleDTO.getIsKey()) {
				return varfleDTO;
			}
		}
		return null;
	}
}
