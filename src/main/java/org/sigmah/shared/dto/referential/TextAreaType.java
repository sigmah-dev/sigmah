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
import org.sigmah.shared.conf.PropertyName;

import com.google.gwt.core.client.GWT;

/**
 * TextArea type.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public enum TextAreaType implements Result {

	PARAGRAPH('P'),
	TEXT('T'),
	NUMBER('N'),
	DATE('D');

	private final char code;

	private TextAreaType(final char code) {
		this.code = code;
	}

	/**
	 * Returns the current text area type corresponding code character.
	 * 
	 * @return The current text area type corresponding code character.
	 */
	public char getCode() {
		return code;
	}

	/**
	 * <p>
	 * Returns the given {@code textAreaType} corresponding name.
	 * </p>
	 * <p>
	 * If this method is executed from server-side, it returns the given {@code textAreaType} enum name.
	 * </p>
	 * 
	 * @param textAreaType
	 *          The text area type.
	 * @return the given {@code textAreaType} corresponding name, or {@code null}.
	 */
	public static String getName(final TextAreaType textAreaType) {

		if (textAreaType == null) {
			return null;
		}

		if (!GWT.isClient()) {
			return textAreaType.name();
		}

		switch (textAreaType) {
			case PARAGRAPH:
				return I18N.CONSTANTS.adminFlexibleTextTypeP();
			case TEXT:
				return I18N.CONSTANTS.adminFlexibleTextTypeT();
			case NUMBER:
				return I18N.CONSTANTS.adminFlexibleTextTypeN();
			case DATE:
				return I18N.CONSTANTS.adminFlexibleTextTypeD();
			default:
				return PropertyName.error(textAreaType.name());
		}
	}

	/**
	 * Returns the given {@code type} corresponding {@link Character} code, or {@code null}.
	 * 
	 * @param type
	 *          The text area type.
	 * @return The given {@code type} corresponding {@link Character} code, or {@code null}.
	 */
	public static Character getCode(final TextAreaType type) {
		return type != null ? type.code : null;
	}

	/**
	 * Returns the given {@code code} corresponding {@link TextAreaType}.
	 * 
	 * @param code
	 *          The code character.
	 * @return The given {@code code} corresponding {@link TextAreaType}, or {@code null}.
	 */
	public static TextAreaType fromCode(final Character code) {

		if (code == null) {
			return null;
		}

		for (final TextAreaType type : values()) {
			if (type.code == code) {
				return type;
			}
		}

		return null;
	}

}
