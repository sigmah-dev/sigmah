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
 * Remove the given importation scheme model or some of its variables.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DeleteImportationSchemeModels extends AbstractCommand<VoidResult> {

	private List<Integer> importationSchemeIds;
	private List<Integer> variableFlexibleElemementIds;

	protected DeleteImportationSchemeModels() {
		// Serialization.
	}

	public DeleteImportationSchemeModels(List<Integer> importationSchemeIds, List<Integer> variableFlexibleElemementIds) {
		this.importationSchemeIds = importationSchemeIds;
		this.variableFlexibleElemementIds = variableFlexibleElemementIds;
	}

	public List<Integer> getImportationSchemeIds() {
		return importationSchemeIds;
	}

	public void setImportationSchemeIds(List<Integer> importationSchemeIdsList) {
		this.importationSchemeIds = importationSchemeIdsList;
	}

	public List<Integer> getVariableFlexibleElemementIds() {
		return variableFlexibleElemementIds;
	}

	public void setVariableFlexibleElemementIds(List<Integer> variableFlexibleElemementIdsList) {
		this.variableFlexibleElemementIds = variableFlexibleElemementIdsList;
	}

}
