package org.sigmah.shared.dto.base;

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

import java.io.Serializable;

/**
 * A data transfer object with a one-to-one relationship with a JPA @Entity.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface EntityDTO<K extends Serializable> extends DTO {

	/**
	 * Id property meta-name.
	 */
	final String ID = "id";

	/**
	 * Returns the corresponding DTO entity's id.
	 * 
	 * @return the corresponding DTO entity's id.
	 */
	K getId();

	/**
	 * Returns the corresponding DTO entity's JPA name starting from the "{@code server.domain}" package name.
	 * 
	 * @return the corresponding DTO entity's JPA name.
	 */
	String getEntityName();

}
