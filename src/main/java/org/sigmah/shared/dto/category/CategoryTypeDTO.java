package org.sigmah.shared.dto.category;

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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.referential.CategoryIcon;

/**
 * CategoryTypeDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CategoryTypeDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4190439829705158136L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "category.CategoryType";

	// DTO attributes keys.
	public static final String LABEL = "label";
	public static final String ICON = "icon";
	public static final String CATEGORY_ELEMENTS = "categoryElementsDTO";

	public CategoryTypeDTO() {
		// Serialization.
	}

	/**
	 * Initializes a new {@code CategoryTypeDTO} with the given {@code label}.
	 * 
	 * @param label
	 *          The category type label.
	 */
	public CategoryTypeDTO(final String label) {
		setLabel(label);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(LABEL, getLabel());
		builder.append(ICON, getIcon());
	}

	// Type label
	public String getLabel() {
		return get(LABEL);
	}

	public void setLabel(String label) {
		set(LABEL, label);
	}

	// Icon name
	public CategoryIcon getIcon() {
		return get(ICON);
	}

	public void setIcon(CategoryIcon icon) {
		set(ICON, icon);
	}

	// Category elements list
	public List<CategoryElementDTO> getCategoryElementsDTO() {
		return get(CATEGORY_ELEMENTS);
	}

	public void setCategoryElementsDTO(List<CategoryElementDTO> categoryElementsDTO) {
		set(CATEGORY_ELEMENTS, categoryElementsDTO);
	}

}
