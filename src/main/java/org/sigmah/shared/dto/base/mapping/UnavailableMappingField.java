package org.sigmah.shared.dto.base.mapping;

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

import org.sigmah.shared.dto.base.DTO;

/**
 * Throws if the field isn't available in a given mapping mode.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class UnavailableMappingField extends RuntimeException {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = -8081248200259540757L;

	/**
	 * The current DTO class.
	 */
	private final Class<? extends DTO> dtoClass;

	/**
	 * The current mapping mode.
	 */
	private final IsMappingMode currentMappingMode;

	/**
	 * The unavailable mapping field.
	 */
	private final MappingField field;

	public UnavailableMappingField(final Class<? extends DTO> dtoClass, final IsMappingMode currentMappingMode, final MappingField field) {
		super("The field " + field + " is unavailable for the current mapping " + currentMappingMode.getMapId() + " in class " + dtoClass.getName() + ".");
		this.dtoClass = dtoClass;
		this.currentMappingMode = currentMappingMode;
		this.field = field;
	}

	public MappingField getField() {
		return field;
	}

	public Class<? extends DTO> getDTOClass() {
		return dtoClass;
	}

	public IsMappingMode getCurrentMappingMode() {
		return currentMappingMode;
	}

}
