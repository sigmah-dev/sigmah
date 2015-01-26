package org.sigmah.server.handler;

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
