package org.sigmah.server.dao.util;

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

import org.sigmah.server.dao.SiteTableDAO;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * Names of columns that can be used to create a Criterion for {@link SiteTableDAO}.
 * 
 * @author Alex Bertram (akbertram@gmail.com)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum SiteTableColumn {

	id(1, EntityConstants.SITE_TABLE + '.' + EntityConstants.SITE_COLUMN_ID),
	activity_id(2, EntityConstants.ACTIVITY_TABLE + '.' + EntityConstants.ACTIVITY_COLUMN_ID),
	activity_name(3, EntityConstants.ACTIVITY_TABLE + '.' + EntityConstants.ACTIVITY_COLUMN_NAME),
	database_id(4, EntityConstants.USER_DATABASE_TABLE + '.' + EntityConstants.USER_DATABASE_COLUMN_ID),
	database_name(5, EntityConstants.USER_DATABASE_TABLE + '.' + EntityConstants.USER_DATABASE_COLUMN_NAME),
	date1(6, EntityConstants.SITE_TABLE + '.' + EntityConstants.SITE_COLUMN_DATE1),
	date2(7, EntityConstants.SITE_TABLE + '.' + EntityConstants.SITE_COLUMN_DATE2),
	status(8, EntityConstants.SITE_TABLE + '.' + EntityConstants.SITE_COLUMN_STATUS),
	partner_id(9, EntityConstants.ORG_UNIT_TABLE + '.' + EntityConstants.ORG_UNIT_COLUMN_ID),
	partner_name(10, EntityConstants.ORG_UNIT_TABLE + '.' + EntityConstants.ORG_UNIT_COLUMN_NAME),
	location_name(11, EntityConstants.LOCATION_TABLE + '.' + EntityConstants.LOCATION_COLUMN_NAME),
	location_axe(12, EntityConstants.LOCATION_TABLE + '.' + EntityConstants.LOCATION_COLUMN_AXE),
	location_type(13, EntityConstants.LOCATION_TYPE_TABLE + '.' + EntityConstants.LOCATION_TYPE_COLUMN_NAME),
	comments(14, EntityConstants.SITE_TABLE + '.' + EntityConstants.SITE_COLUMN_COMMENTS),
	x(15, EntityConstants.LOCATION_TABLE + '.' + EntityConstants.LOCATION_COLUMN_X),
	y(16, EntityConstants.LOCATION_TABLE + '.' + EntityConstants.LOCATION_COLUMN_Y);

	private final String property;
	private final int index;

	private SiteTableColumn(int index, String property) {
		this.index = index;
		this.property = property;
	}

	public int index() {
		return index;
	}

	public String alias() {
		return toString();
	}

	public String property() {
		return property;
	}
}
