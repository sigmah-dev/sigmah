package org.sigmah.server.handler;

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

import com.google.inject.Inject;
import org.sigmah.server.domain.SiteData;
import org.sigmah.shared.command.GetSitePoints;
import org.sigmah.shared.command.result.SitePointList;
import org.sigmah.shared.dto.BoundingBoxDTO;
import org.sigmah.shared.dto.SitePointDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.sigmah.server.dao.SiteTableDAO;
import org.sigmah.server.dao.util.SiteOrder;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.report.model.generator.SiteDataBinder;
import org.sigmah.shared.dispatch.CommandException;


/**
 * @author Alex Bertram (akbertram@gmail.com)
 * @see org.sigmah.shared.command.GetSitePoints
 */
public class GetSitePointsHandler extends AbstractCommandHandler<GetSitePoints, SitePointList> {

    private final SiteTableDAO dao;

    @Inject
    public GetSitePointsHandler(SiteTableDAO dao) {
        this.dao = dao;
    }

	@Override
	protected SitePointList execute(GetSitePoints command, UserDispatch.UserExecutionContext context) throws CommandException {

        // query for the sites
        List<SiteData> sites = dao.query(context.getUser(),
                command.getFilter(),
                Collections.<SiteOrder>emptyList(),
                new SiteDataBinder(),
                SiteTableDAO.RETRIEVE_NONE, 0, -1);

        BoundingBoxDTO bounds = new BoundingBoxDTO();

        int withoutCoordinates = 0;
        List<SitePointDTO> points = new ArrayList<SitePointDTO>(sites.size());
        for (SiteData site : sites) {
            if (site.hasLatLong()) {
                points.add(new SitePointDTO(site.getId(), site.getLocationName(), site.getLongitude(), site.getLatitude()));
                bounds.grow(site.getLongitude(), site.getLatitude());
            } else {
            	withoutCoordinates++;
            }
        }
        return new SitePointList(bounds, points, withoutCoordinates);
    }
}
