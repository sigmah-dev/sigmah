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
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DeleteCategories extends AbstractCommand<VoidResult> {

	private List<CategoryTypeDTO> categories;
	private List<CategoryElementDTO> categoryElements;

	protected DeleteCategories() {
		// Serialization.
	}

	public DeleteCategories(List<CategoryTypeDTO> categories, List<CategoryElementDTO> categoryElements) {
		this.setCategoryTypes(categories);
		this.setCategoryElements(categoryElements);
	}

	public void setCategoryTypes(List<CategoryTypeDTO> categories) {
		this.categories = categories;
	}

	public List<CategoryTypeDTO> getCategoryTypes() {
		return categories;
	}

	public void addCategoryType(CategoryTypeDTO category) {
		if (categories == null) {
			categories = new ArrayList<CategoryTypeDTO>();
		}
		this.categories.add(category);
	}

	public void removeCategoryType(CategoryTypeDTO category) {
		this.categories.remove(category);
	}

	public void setCategoryElements(List<CategoryElementDTO> categoryElements) {
		this.categoryElements = categoryElements;
	}

	public List<CategoryElementDTO> getCategoryElements() {
		return categoryElements;
	}

	public void addCategoryElement(CategoryElementDTO categoryElement) {
		if (categoryElements == null) {
			categoryElements = new ArrayList<CategoryElementDTO>();
		}
		this.categoryElements.add(categoryElement);
	}

	public void removeCategoryElement(CategoryElementDTO category) {
		this.categoryElements.remove(category);
	}
}
