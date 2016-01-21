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
