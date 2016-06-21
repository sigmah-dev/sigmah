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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DeleteGroups extends AbstractCommand<VoidResult> {

	private List<LayoutGroupDTO> flexElts;

	protected DeleteGroups() {
		// Serialization.
	}

	public DeleteGroups(List<LayoutGroupDTO> flexElts) {
		this.setGroups(flexElts);
	}

	public void setGroups(List<LayoutGroupDTO> flexElts) {
		this.flexElts = flexElts;
	}

	public List<LayoutGroupDTO> getGroups() {
		return flexElts;
	}

	public void addGroups(LayoutGroupDTO flexElt) {
		if (flexElts == null) {
			flexElts = new ArrayList<LayoutGroupDTO>();
		}
		this.flexElts.add(flexElt);
	}

	public void removeGroups(LayoutGroupDTO flexElt) {
		this.flexElts.remove(flexElt);
	}
}
