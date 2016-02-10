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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sigmah.server.domain.AdminEntity;

/**
 * Binds the results of a {@link org.sigmah.server.dao.SiteTableDAO SiteTableDAO} query to a particular storage class.
 * TODO: Probably remove, there isn't a convincing case for another storage class outside of
 * {@link org.sigmah.shared.dto.SiteDTO}
 * 
 * @param <SiteT>
 */
public interface SiteProjectionBinder<SiteT> {

	SiteT newInstance(String[] properties, ResultSet rs) throws SQLException;

	void setAdminEntity(SiteT site, AdminEntity entity);

	void addIndicatorValue(SiteT site, int indicatorId, int aggregationMethod, double value);

	void setAttributeValue(SiteT site, int attributeId, boolean value);

}
