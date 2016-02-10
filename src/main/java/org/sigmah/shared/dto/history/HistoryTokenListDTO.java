package org.sigmah.shared.dto.history;

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

import java.util.Date;
import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataDTO;

public class HistoryTokenListDTO extends AbstractModelDataDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 9137525003889062584L;

	// DTO attributes keys.
	public static final String DATE = "date";
	public static final String EMAIL = "email";
	public static final String FIRST_NAME = "firstName";
	public static final String NAME = "name";
	public static final String TOKENS = "tokens";

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(DATE, getDate());
		builder.append(EMAIL, getUserEmail());
		builder.append(FIRST_NAME, getUserFirstName());
		builder.append(NAME, getUserName());
	}

	// Token date.
	public Date getDate() {
		return get(DATE);
	}

	public void setDate(Date date) {
		set(DATE, date);
	}

	// User email.
	public String getUserEmail() {
		return get(EMAIL);
	}

	public void setUserEmail(String email) {
		set(EMAIL, email);
	}

	// User first name.
	public String getUserFirstName() {
		return get(FIRST_NAME);
	}

	public void setUserFirstName(String firstName) {
		set(FIRST_NAME, firstName);
	}

	// User name.
	public String getUserName() {
		return get(NAME);
	}

	public void setUserName(String name) {
		set(NAME, name);
	}

	// Tokens.
	public List<HistoryTokenDTO> getTokens() {
		return get(TOKENS);
	}

	public void setTokens(List<HistoryTokenDTO> tokens) {
		set(TOKENS, tokens);
	}
}
