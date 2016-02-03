package org.sigmah.shared.command.result;

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

import org.sigmah.shared.dto.ImportDetails;

/**
 * Result for command {@link org.sigmah.shared.command.GetImportInformation GetImportInformation}.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ImportInformationResult implements Result {

	private List<ImportDetails> entitiesToImport;

	public ImportInformationResult() {
		// Serialization.
	}

	public ImportInformationResult(List<ImportDetails> entitiesToImport) {
		this.entitiesToImport = entitiesToImport;
	}

	/**
	 * @return the entitiesToImport
	 */
	public List<ImportDetails> getEntitiesToImport() {
		return entitiesToImport;
	}

	/**
	 * @param entitiesToImport
	 *          the entitiesToImport to set
	 */
	public void setEntitiesToImport(List<ImportDetails> entitiesToImport) {
		this.entitiesToImport = entitiesToImport;
	}

}
