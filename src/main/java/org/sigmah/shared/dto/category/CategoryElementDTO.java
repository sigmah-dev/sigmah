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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * CategoryElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class CategoryElementDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 7879245182808843730L;
    
	private static final String STYLE_LABEL_SMALL = "label-small";


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return "category.CategoryElement";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("label", getLabel());
		builder.append("color", getColor());
		builder.append("iconHtml", getIconHtml());
	}

	// Element label
	public String getLabel() {
		return get("label");
	}

	public void setLabel(String label) {
		set("label", label);
	}

	// Reference to the parent category type
	public CategoryTypeDTO getParentCategoryDTO() {
		return get("parentCategoryDTO");
	}

	public void setParentCategoryDTO(CategoryTypeDTO parentCategoryDTO) {
		set("parentCategoryDTO", parentCategoryDTO);
	}

	// Color
	public String getColor() {
		return get("color");
	}

	public void setColor(String color) {
		set("color", color);
	}

	public void setIconHtml(String iconHtml) {
		set("iconHtml", iconHtml);
	}

	public String getIconHtml() {
		return get("iconHtml");
	}
    
  public boolean getisDisabled() {
		return get("isdisabled");
	}

	public void setisDisabled(boolean isdisabled) {
		set("isdisabled", isdisabled);
	}  

    public String renderDisabled(final String value) {

		// Renders direct HTML to improve performances.
		final StringBuilder builder = new StringBuilder();
		builder.append("<div class=\"").append(STYLE_LABEL_SMALL).append(" x-component\" style=\"text-decoration: line-through;\">");
		builder.append(value != null ? String.valueOf(value) : "").append("</div>");

		return builder.toString();
	}
    
    public String renderText(final String value) {

		// Renders direct HTML to improve performances.
		final StringBuilder builder = new StringBuilder();
		builder.append("<div class=\"").append(STYLE_LABEL_SMALL).append(" x-component\">");
		builder.append(value != null ? String.valueOf(value) : "").append("</div>");

		return builder.toString();
	}
}
