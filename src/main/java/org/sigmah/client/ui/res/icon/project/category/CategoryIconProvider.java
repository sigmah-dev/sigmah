package org.sigmah.client.ui.res.icon.project.category;

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


import org.sigmah.shared.dto.category.CategoryElementDTO;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * Provides a icon for each category element.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class CategoryIconProvider {

	private CategoryIconProvider() {
		// Only provides static methods.
	}

	/**
	 * Build an image for the given category element.
	 * 
	 * @param element
	 *          The category element.
	 * @return The image.
	 */
	public static Image getIcon(CategoryElementDTO element) {
		return getIcon(element, true);
	}

	/**
	 * Build an image for the given category element.
	 * 
	 * @param element
	 *          The category element.
	 * @param tooltip
	 *          If a tooltip text must be added.
	 * @return The image.
	 */
	public static Image getIcon(CategoryElementDTO element, boolean tooltip) {

		if (element == null || element.getParentCategoryDTO() == null || element.getParentCategoryDTO().getIcon() == null || element.getColor() == null) {
			return null;
		}

		final AbstractImagePrototype prototype;

		switch (element.getParentCategoryDTO().getIcon()) {
			case CIRCLE:
				prototype = CategoryImageBundle.ICONS.circle();
				break;
			case SQUARE:
				prototype = CategoryImageBundle.ICONS.square();
				break;
			case TRIANGLE:
				prototype = CategoryImageBundle.ICONS.triangle();
				break;
			case CROSS:
				prototype = CategoryImageBundle.ICONS.cross();
				break;
			case DIAMOND:
				prototype = CategoryImageBundle.ICONS.diamond();
				break;
			case STAR:
				prototype = CategoryImageBundle.ICONS.star();
				break;
			default:
				return null;
		}

		final Image img = prototype.createImage();
		img.getElement().getStyle().setBackgroundColor("#" + element.getColor());

		if (tooltip) {
			img.setTitle(element.getParentCategoryDTO().getLabel() + " (" + element.getLabel() + ')');
		}

		return img;
	}

	/**
	 * Build an image for the given category element and return its HTML code.
	 * 
	 * @param element
	 *          The category element.
	 * @param tooltip
	 *          If a tooltip text must be added.
	 * @return The image HTML code.
	 */
	public static String getIconHtml(CategoryElementDTO element, boolean tooltip) {
		final Image img = getIcon(element, tooltip);
		return img == null ? "" : img.getElement().getString();
	}

	public static native String getComboboxIconTemplate()
	/*-{
		return [
				'<tpl for=".">',
				'<table cellspacing="0" cellpadding="0" width="100%" class="x-combo-list-item">\
    <tr>\
        <td width="16px"><tpl if="values.categoryElement != null">{[values.categoryElement.iconHtml]}</tpl></td>\
        <td style="padding-left: 5px;">{[values.label]}</td>\
    </tr>\
    </table>',
				'</tpl>' ].join("");
	}-*/;

}
