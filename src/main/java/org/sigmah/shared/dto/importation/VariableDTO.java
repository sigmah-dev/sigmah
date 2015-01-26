package org.sigmah.shared.dto.importation;

import org.sigmah.shared.dto.EntityDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VariableDTO extends BaseModelData implements EntityDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3598029403371970959L;

	@Override
	public int getId() {
		if (get("id") != null)
			return (Integer) get("id");
		else
			return -1;
	}

	public void setId(int id) {
		set("id", id);
	}

	@Override
	public String getEntityName() {
		return "importation.Variable";
	}

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	public String getReference() {
		return get("reference");
	}

	public void setReference(String reference) {
		set("reference", reference);
	}

	public ImportationSchemeDTO getImportationSchemeDTO() {
		return get("importationSchemeDTO");
	}

	public void setImportationSchemeDTO(ImportationSchemeDTO importationSchemeDTO) {
		set("importationSchemeDTO", importationSchemeDTO);
	}
}
