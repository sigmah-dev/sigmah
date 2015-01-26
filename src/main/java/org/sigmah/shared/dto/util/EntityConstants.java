package org.sigmah.shared.dto.util;

/**
 * <p>
 * Utility class referencing entities constants (table and column names, sizes, etc.).
 * </p>
 * <p>
 * <em>Thank you for maintaining entities alphabetical order in this class.</em>
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class EntityConstants {

	protected EntityConstants() {
		// This class only provides static constants.
	}

	// --------------------------------------------------------------------------------
	//
	// _COMMON.
	//
	// --------------------------------------------------------------------------------

	// Email.
	public static final int EMAIL_MAX_LENGTH = 60;
	public static final char EMAIL_AROBACE = '@';
	private static final String ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+";
	private static final String DOMAIN = ATOM + "(\\." + ATOM + ")*\\.[a-z]{2,4}";
	private static final String IP_DOMAIN = "\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\]";
	private static final String EMAIL_PATTERN = ATOM + "(\\." + ATOM + ")*" + EMAIL_AROBACE + "(" + DOMAIN + "|" + IP_DOMAIN + ")";
	public static final String EMAIL_REGULAR_EXPRESSION = "^" + EMAIL_PATTERN + "$";
	public static final String EMAILS_SEPARATOR = ",";
	public static final String EMAILS_REGULAR_EXPRESSION = "^(" + EMAIL_PATTERN + "(" + EMAILS_SEPARATOR + ")?)+$";

	public static final int NAME_MAX_LENGTH = 50;

	// --------------------------------------------------------------------------------
	//
	// ORG UNIT ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final int ORG_UNIT_NAME_MAX_LENGTH = 16;
	public static final int ORG_UNIT_FULL_NAME_MAX_LENGTH = 64;

	// --------------------------------------------------------------------------------
	//
	// USER DATABASE ENTITY.
	//
	// --------------------------------------------------------------------------------

	public static final int USER_DATABASE_FULL_NAME_MAX_LENGTH = 500;

}
