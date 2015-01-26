package org.sigmah.shared.dto.referential;

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
	 * Returns the given {@code categoryIcon} corresponding name.<br/>
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
	 * Returns the given {@code categoryIconText} corresponding {@code CategoryIcon} value.<br/>
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
