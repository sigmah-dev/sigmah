package org.sigmah.shared.dto.referential;

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
