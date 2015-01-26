package org.sigmah.server.dao.util;

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
