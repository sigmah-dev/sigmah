package org.sigmah.shared.dto.referential;

/**
 * A key which is used by the mail service to replace strings before sending.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public enum EmailKeyEnum implements EmailKey {

	APPLICATION_LINK,
	USER_USERNAME,
	USER_LOGIN,
	USER_PASSWORD(false),
	CONTENT,
	
	INVITING_USERNAME,
	INVITING_EMAIL,
	CHANGE_PASS_KEY,
	RESET_PASSWORD_LINK,
	
	ERROR_LIST,
	FILE_NAME,
	;

	private final boolean isSafe;

	private final String key;

	private EmailKeyEnum() {
		this(true, null);
	}

	private EmailKeyEnum(boolean isSafe) {
		this(isSafe, null);
	}

	private EmailKeyEnum(String key) {
		this(true, key);
	}

	private EmailKeyEnum(boolean isSafe, String key) {
		this.isSafe = isSafe;
		this.key = key;
	}

	@Override
	public String getKey() {
		return key != null ? key : name();
	}

	@Override
	public boolean isSafe() {
		return isSafe;
	}

}
