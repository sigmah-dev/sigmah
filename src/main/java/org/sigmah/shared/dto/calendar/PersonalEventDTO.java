package org.sigmah.shared.dto.calendar;

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

import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * This DTO is currently not used.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PersonalEventDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -3126801656737893590L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "calendar.PersonalEvent";

	public PersonalEventDTO() {
		// Serialization.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

}
