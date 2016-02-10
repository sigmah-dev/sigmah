package org.sigmah.server.report.model.generator;

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

import org.sigmah.server.domain.SiteData;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.sigmah.server.dao.util.SiteProjectionBinder;
import org.sigmah.server.dao.util.SiteTableColumn;
import org.sigmah.server.domain.AdminEntity;

public class SiteDataBinder implements SiteProjectionBinder<SiteData> {

	@Override
	public SiteData newInstance(String[] properties, ResultSet rs) throws SQLException {
        SiteTableColumn[] columns = SiteTableColumn.values();
        Object[] values = new Object[columns.length];

        for(int i=0;i!=values.length;++i) {
            values[i] = rs.getObject(columns[i].index());
        }

		return new SiteData(values);
	}

	
	@Override
	public void addIndicatorValue(SiteData site, int indicatorId,
			int aggregationMethod, double value) {
		
		site.indicatorValues.put(indicatorId, value);
	}


	@Override
	public void setAdminEntity(SiteData site, AdminEntity entity) {
		site.adminNames.put(entity.getLevel().getId(), entity.getName());
        site.adminEntities.put(entity.getLevel().getId(), entity);
	}

	public void setAttributeValue(SiteData site, int attributeId, boolean value) {
        if(value) {
            site.attributes.put(attributeId, value);
        }
	}

}
