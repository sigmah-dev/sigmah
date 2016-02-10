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


import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;

/**
 * Deletes an importation scheme or a list of importation scheme variables.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr) v2.0
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DeleteImportationSchemes extends AbstractCommand<VoidResult> {

	private Integer schemaId;
	private List<Integer> variableIdsList;

	protected DeleteImportationSchemes() {
		// Serialization.
	}
	
	public DeleteImportationSchemes(Integer schemaId) {
		this.schemaId = schemaId;
	}
	
	public DeleteImportationSchemes(List<Integer> variableIdsList) {
		this.variableIdsList = variableIdsList;
	}

	public Integer getSchemaId() {
		return schemaId;
	}

	public List<Integer> getVariableIdsList() {
		return variableIdsList;
	}

}
