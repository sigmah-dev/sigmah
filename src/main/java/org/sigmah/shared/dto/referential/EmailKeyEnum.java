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
