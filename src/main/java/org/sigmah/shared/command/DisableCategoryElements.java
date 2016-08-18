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
import org.sigmah.shared.dto.category.CategoryElementDTO;

/**
 * Enable or disable category elements.
 * 
 * @author Nikita Jibhkate (niksj1996@gmail.com)
 */
public class DisableCategoryElements implements Command<VoidResult> {
	//private static final long serialVersionUID = 3902096705358448566L;

	private List<CategoryElementDTO> categoryElements;
	private boolean disable;
	
	protected DisableCategoryElements() {
		// Serialization.
	}
	
	public DisableCategoryElements(List<CategoryElementDTO> categoryElements, boolean disable) {
		this.categoryElements = categoryElements;
		this.disable = disable;
	}

	public List<CategoryElementDTO> getCategoryElements() {
		return categoryElements;
	}

	public boolean isDisable() {
		return disable;
	}

}
