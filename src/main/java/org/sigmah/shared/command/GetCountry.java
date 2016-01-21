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

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.country.CountryDTO;

/**
 * Retrieves a country by ID.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class GetCountry extends AbstractCommand<CountryDTO> {

	private int id;

	public GetCountry() {
		// Serialization.
	}

	public GetCountry(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
