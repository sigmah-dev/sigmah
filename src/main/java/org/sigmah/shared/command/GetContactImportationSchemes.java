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

import java.util.Set;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetContactImportationSchemes extends AbstractCommand<ListResult<ImportationSchemeDTO>> {

	private Integer schemaId;

	private Set<Integer> contactModelIds;

	private Boolean excludeExistent = false;

	public GetContactImportationSchemes() {
		// Serialization.
	}

	public GetContactImportationSchemes(Set<Integer> contactModelIds) {
		this.contactModelIds = contactModelIds;
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
	 * @return the contactModelIds
	 */
	public Set<Integer> getContactModelIds() {
		return contactModelIds;
	}

	/**
	 * @param contactModelIds
	 *          the contactModelIds to set
	 */
	public void setContactModelId(Set<Integer> contactModelIds) {
		this.contactModelIds = contactModelIds;
	}

}
