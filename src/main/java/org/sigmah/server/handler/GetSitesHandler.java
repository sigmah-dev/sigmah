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

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.SortInfo;
import com.google.inject.Inject;
import org.sigmah.shared.command.GetSites;
import org.sigmah.shared.command.result.SiteResult;
import org.sigmah.shared.dto.AdminEntityDTO;
import org.sigmah.shared.dto.BoundingBoxDTO;
import org.sigmah.shared.dto.PartnerDTO;
import org.sigmah.shared.dto.SiteDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sigmah.server.dao.SiteTableDAO;
import org.sigmah.server.dao.util.SiteOrder;
import org.sigmah.server.dao.util.SiteProjectionBinder;
import org.sigmah.server.dao.util.SiteTableColumn;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.AdminEntity;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.dispatch.CommandException;


/**
 * Handler for {@link GetSites} command.
 * 
 * @author Alex Bertram
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 * @see org.sigmah.shared.command.GetSites
 */
public class GetSitesHandler extends AbstractCommandHandler<GetSites, SiteResult> {

	/**
	 * DAO used to retrieve data about the sites. 
	 */
	@Inject
    private SiteTableDAO siteDAO;

	@Override
	protected SiteResult execute(GetSites command, UserDispatch.UserExecutionContext context) throws CommandException {
        final List<SiteOrder> order = sortInfoToSortOrder(command);
        int offset = calculateOffset(command, context.getUser(), order);

        final List<SiteDTO> sites = siteDAO.query(
                context.getUser(),
                command.getFilter(),
                order,
                new ModelBinder(),
                SiteTableDAO.RETRIEVE_ALL,
                offset,
                command.getLimit());

        return new SiteResult(sites, offset, siteDAO.queryCount(context.getUser(), command.getFilter()));
    }

    private int calculateOffset(GetSites cmd, User user, List<SiteOrder> order) {
        int offset;
        if (cmd.getSeekToSiteId() != null && cmd.getSeekToSiteId() != 0 && cmd.getLimit() > 0) {
            int pageNum = siteDAO.queryPageNumber(
                    user,
                    cmd.getFilter(),
                    order,
                    cmd.getLimit(),
                    cmd.getSeekToSiteId());
            offset = pageNum * cmd.getLimit();

        } else {
            offset = cmd.getOffset();
        }
        return offset;
    }

    // TODO: ideally the client is just sending the SiteOrder object directly,
    // but we'll need to harmonize the field names first.
    private List<SiteOrder> sortInfoToSortOrder(GetSites cmd) {
        final List<SiteOrder> order = new ArrayList<SiteOrder>();
        if (cmd.getSortInfo().getSortDir() != SortDir.NONE) {
            String field = cmd.getSortInfo().getSortField();

            if (field.equals("date1")) {
                order.add( order(SiteTableColumn.date1, cmd.getSortInfo()));
            } else if (field.equals("date2")) {
                order.add(order(SiteTableColumn.date2, cmd.getSortInfo()));
            } else if (field.equals("locationName")) {
                order.add(order(SiteTableColumn.location_name, cmd.getSortInfo()));
            } else if (field.equals("partner")) {
                order.add(order(SiteTableColumn.partner_name, cmd.getSortInfo()));
            } else if (field.equals("locationAxe")) {
                order.add(order(SiteTableColumn.location_axe, cmd.getSortInfo()));
            } else {
                order.add(new SiteOrder(field, cmd.getSortInfo().getSortDir() == SortDir.DESC));
            }
        }
        return order;
    }

    protected SiteOrder order(SiteTableColumn column, SortInfo si) {
        if (si.getSortDir() == SortDir.ASC) {
            return SiteOrder.ascendingOn(column.property());
        } else {
            return SiteOrder.descendingOn(column.property());
        }
    }

    protected class ModelBinder implements SiteProjectionBinder<SiteDTO> {

        private final Map<Integer, AdminEntityDTO> adminEntities = new HashMap<Integer, AdminEntityDTO>();
        private final Map<Integer, PartnerDTO> partners = new HashMap<Integer, PartnerDTO>();


		@Override
        public SiteDTO newInstance(String[] properties, ResultSet rs) throws SQLException {
            SiteDTO model = new SiteDTO();
            model.setId( rs.getInt(SiteTableColumn.id.index()) );
            model.setActivityId( rs.getInt(SiteTableColumn.activity_id.index() ));
            model.setDatabaseId( rs.getInt(SiteTableColumn.database_id.index() ));
            model.setDate1(rs.getDate(SiteTableColumn.date1.index() ));
            model.setDate2(rs.getDate(SiteTableColumn.date2.index() ));
            model.setLocationName( rs.getString( SiteTableColumn.location_name.index() ));
            model.setLocationAxe( rs.getString( SiteTableColumn.location_axe.index() ));
            model.setX( rs.getDouble( SiteTableColumn.x.index()));
            model.setY( rs.getDouble( SiteTableColumn.y.index()));
            model.setComments( rs.getString( SiteTableColumn.comments.index() ));

            int partnerId = rs.getInt(SiteTableColumn.partner_id.index());
            PartnerDTO partner = partners.get(partnerId);
            if (partner == null) {
                partner = new PartnerDTO(
                        partnerId,
                        rs.getString(SiteTableColumn.partner_name.index()));
                partners.put(partnerId, partner);
            }

            model.setPartner(partner);
            return model;

        }

		@Override
        public void setAdminEntity(SiteDTO site, AdminEntity entity) {
            AdminEntityDTO model = adminEntities.get(entity.getId());
            if (model == null) {
                model = new AdminEntityDTO();
                model.setId(entity.getId());
                model.setName(entity.getName());
                model.setLevelId(entity.getLevel().getId());
                if(entity.getParent() != null) {
                    model.setParentId(entity.getParent().getId());
                }
                if(entity.getBounds() != null) {
                    model.setBounds(new BoundingBoxDTO(
                            entity.getBounds().getX1(),
                            entity.getBounds().getY1(),
                            entity.getBounds().getX2(),
                            entity.getBounds().getY2()
                    ));
                }
                adminEntities.put(entity.getId(), model);
            }
            site.setAdminEntity(entity.getLevel().getId(), model);
        }

		@Override
        public void setAttributeValue(SiteDTO site, int attributeId,
                                      boolean value) {

            site.setAttributeValue(attributeId, value);
        }

		@Override
        public void addIndicatorValue(SiteDTO site, int indicatorId,
                                      int aggregationMethod, double value) {

            site.setIndicatorValue(indicatorId, value);

        }
    }

}
