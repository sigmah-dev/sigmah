package org.sigmah.shared.dto.importation;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * VariableFlexibleElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class VariableFlexibleElementDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8473344169186271504L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "importation.VariableFlexibleElement";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("isKey", getIsKey());
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
