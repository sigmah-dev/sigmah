package org.sigmah.shared.dto.importation;

import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VariableFlexibleElementDTO extends BaseModelData implements EntityDTO {

	private static final long serialVersionUID = 8473344169186271504L;

	@Override
	public int getId() {
		return (Integer) get("id");
	}

	public void setId(Integer id) {
		set("id", id);
	}

	@Override
	public String getEntityName() {
		return "importation.VariableFlexibleElement";
	}

	public Boolean getIsKey() {
		return get("isKey");
	}

	public void setIsKey(Boolean isKey) {
		set("isKey", isKey);
	}

	public VariableDTO getVariableDTO() {
		return get("variableDTO");
	}

	public void setVariableDTO(VariableDTO variableDTO) {
		set("variableDTO", variableDTO);
	}

	public FlexibleElementDTO getFlexibleElementDTO() {
		return get("flexibleElementDTO");
	}

	public void setFlexibleElementDTO(FlexibleElementDTO flexibleElementDTO) {
		set("flexibleElementDTO", flexibleElementDTO);
	}

	public ImportationSchemeModelDTO getImportationSchemeModelDTO() {
		return get("importationSchemeModelDTO");
	}

	public void setImportationSchemeModelDTO(ImportationSchemeModelDTO importationSchemeModelDTO) {
		set("importationSchemeModelDTO", importationSchemeModelDTO);
	}

}
