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

/**
 * VariableDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class VariableDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -3598029403371970959L;
	
	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "importation.Variable";

	// DTO attributes keys.
	public static final String NAME = "name";
	public static final String REFERENCE = "reference";
	public static final String IMPORTATION_SCHEME = "importationScheme";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(NAME, getName());
		builder.append(REFERENCE, getReference());
	}

	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
	}

	public String getReference() {
		return get(REFERENCE);
	}

	public void setReference(String reference) {
		set(REFERENCE, reference);
	}

	public ImportationSchemeDTO getImportationScheme() {
		return get(IMPORTATION_SCHEME);
	}

	public void setImportationScheme(ImportationSchemeDTO importationScheme) {
		set(IMPORTATION_SCHEME, importationScheme);
	}
}
