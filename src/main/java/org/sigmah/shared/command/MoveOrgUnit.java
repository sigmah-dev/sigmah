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
import org.sigmah.shared.command.result.VoidResult;

/**
 * Move a given {@code OrgUnitDTO} within its hierarchy.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class MoveOrgUnit extends AbstractCommand<VoidResult> {

	/**
	 * The org unit id to move.
	 */
	private Integer id;

	/**
	 * The new parent org unit id.
	 */
	private Integer parentId;

	public MoveOrgUnit() {
		// Serialization.
	}

	public MoveOrgUnit(final Integer id, final Integer parentId) {
		this.id = id;
		this.parentId = parentId;
	}

	public Integer getId() {
		return id;
	}

	public Integer getParentId() {
		return parentId;
	}

}
