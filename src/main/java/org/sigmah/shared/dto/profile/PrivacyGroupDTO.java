package org.sigmah.shared.dto.profile;

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

import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;

/**
 * DTO mapping class for entity profile.PrivacyGroup.
 * 
 * @author nrebiai
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PrivacyGroupDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8951877538079370046L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "profile.PrivacyGroup";

	// DTO 'base' attributes keys.
	public static final String TITLE = "title";
	public static final String CODE = "code";
	public static final String UPDATED = "updated";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(CODE, getCode());
		builder.append(TITLE, getTitle());
		builder.append(UPDATED, isUpdated());
	}

	// Code.
	public Integer getCode() {
		return get(CODE);
	}

	public void setCode(Integer code) {
		set(CODE, code);
	}

	// Title.
	public String getTitle() {
		return get(TITLE);
	}

	public void setTitle(String title) {
		set(TITLE, title);
	}

	// Updated.
	public boolean isUpdated() {
		return ClientUtils.isTrue(get(UPDATED));
	}

	public void setUpdated(boolean updated) {
		set(UPDATED, updated);
	}

}
