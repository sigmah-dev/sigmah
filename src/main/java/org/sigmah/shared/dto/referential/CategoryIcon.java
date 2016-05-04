package org.sigmah.shared.dto.referential;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.core.client.GWT;

/**
 * Category icons enumeration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum CategoryIcon implements Result {

	CIRCLE,
	CROSS,
	DIAMOND,
	SQUARE,
	STAR,
	TRIANGLE;

	/**
	 * Returns the given {@code categoryIcon} corresponding name.
	 * This method should be executed from client-side. If executed from server-side, it returns the enum constant name.
	 * 
	 * @param categoryIcon
	 *          The category icon.
	 * @return the given {@code categoryIcon} corresponding name, or {@code null}.
	 */
	public static String getName(final CategoryIcon categoryIcon) {

		if (categoryIcon == null) {
			return null;
		}

		if (!GWT.isClient()) {
			return categoryIcon.name();
		}

		switch (categoryIcon) {
			case CIRCLE:
				return I18N.CONSTANTS.adminCategoryCircle();

			case CROSS:
				return I18N.CONSTANTS.adminCategoryCross();

			case DIAMOND:
				return I18N.CONSTANTS.adminCategoryDiamond();

			case SQUARE:
				return I18N.CONSTANTS.adminCategorySquare();

			case STAR:
				return I18N.CONSTANTS.adminCategoryStar();

			case TRIANGLE:
				return I18N.CONSTANTS.adminCategoryTriangle();

			default:
				return categoryIcon.name();
		}
	}

	/**
	 * Returns the given {@code categoryIconText} corresponding {@code CategoryIcon} value.
	 * This method should be executed from client-side. If executed from server-side, it always returns {@code null}.
	 * 
	 * @param categoryIconText
	 *          The category icon text.
	 * @return the given {@code categoryIconText} corresponding {@code CategoryIcon} value, or {@code null}.
	 */
	public static CategoryIcon getIcon(final String categoryIconText) {

		if (!GWT.isClient()) {
			return null;
		}

		if (I18N.CONSTANTS.adminCategoryCircle().equals(categoryIconText)) {
			return CIRCLE;

		} else if (I18N.CONSTANTS.adminCategoryCross().equals(categoryIconText)) {
			return CROSS;

		} else if (I18N.CONSTANTS.adminCategoryDiamond().equals(categoryIconText)) {
			return DIAMOND;

		} else if (I18N.CONSTANTS.adminCategorySquare().equals(categoryIconText)) {
			return SQUARE;

		} else if (I18N.CONSTANTS.adminCategoryStar().equals(categoryIconText)) {
			return STAR;

		} else if (I18N.CONSTANTS.adminCategoryTriangle().equals(categoryIconText)) {
			return TRIANGLE;

		} else {
			return null;
		}
	}

	private CategoryIcon() {
	}
}
