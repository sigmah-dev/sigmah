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
import org.sigmah.shared.command.base.Command;

import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Enable or disable flexible elements.
 * 
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DisableFlexibleElements implements Command<VoidResult> {
	private static final long serialVersionUID = 3902096705358448566L;

	private List<FlexibleElementDTO> flexibleElements;
	private boolean disable;
	
	protected DisableFlexibleElements() {
		// Serialization.
	}
	
	public DisableFlexibleElements(List<FlexibleElementDTO> flexibleElements, boolean disable) {
		this.flexibleElements = flexibleElements;
		this.disable = disable;
	}

	public List<FlexibleElementDTO> getFlexibleElements() {
		return flexibleElements;
	}

	public boolean isDisable() {
		return disable;
	}

}
