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
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.UserDatabaseDTO;

/**
 * Retrieves the list of project models available to the user.
 * 
 * @author nrebiai (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetUserDatabase extends AbstractCommand<ListResult<UserDatabaseDTO>> {

	/**
	 * The type of model of the models for the current user organization (set to <code>null</code> to ignore this filter).
	 */
	public GetUserDatabase() {
		// Serialization.
	}
}
