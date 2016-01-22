package org.sigmah.shared.dto;

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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataDTO;

/**
 * <p>
 * Data transfer object for datasources of Indicators.
 * </p>
 * <p>
 * This is a projection of the {@link org.sigmah.server.domain.Indicator} and
 * {@link org.sigmah.server.domain.UserDatabase} entities.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class IndicatorDataSourceDTO extends AbstractModelDataDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1257058710718562119L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("indicatorId", getIndicatorId());
		builder.append("databaseId", getDatabaseId());
		builder.append("databaseName", getDatabaseName());
		builder.append("indicatorName", getIndicatorName());
		builder.append("indicatorCode", getIndicatorCode());
	}

	public int getIndicatorId() {
		return (Integer) get("indicatorId");
	}

	public void setIndicatorId(int id) {
		set("indicatorId", id);
	}

	public int getDatabaseId() {
		return (Integer) get("databaseId");
	}

	public void setDatabaseId(int id) {
		set("databaseId", id);
	}

	public String getDatabaseName() {
		return get("databaseName");
	}

	public void setDatabaseName(String name) {
		set("databaseName", name);
	}

	public String getIndicatorName() {
		return get("indicatorName");
	}

	public void setIndicatorName(String name) {
		set("indicatorName", name);
	}

	public String getIndicatorCode() {
		return get("indicatorCode");
	}

	public void setIndicatorCode(String code) {
		set("indicatorCode", code);
	}

}
