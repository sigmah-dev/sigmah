/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc.handler;

import com.google.inject.Inject;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetIndicatorDataSources;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.IndicatorDataSourceList;
import org.sigmah.shared.domain.Indicator;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.IndicatorDataSourceDTO;
import org.sigmah.shared.exception.CommandException;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;


public class GetIndicatorDataSourcesHandler implements CommandHandler<GetIndicatorDataSources> {

    private final EntityManager entityManager;
    private final Mapper mapper;

    @Inject
    public GetIndicatorDataSourcesHandler(EntityManager entityManager, Mapper mapper) {
        this.entityManager = entityManager;
        this.mapper = mapper;
    }

    @Override
    public CommandResult execute(GetIndicatorDataSources cmd, User user) throws CommandException {
        // TODO: verify authorization

        Indicator indicator = entityManager.find(Indicator.class, cmd.getIndicatorId());
        List<IndicatorDataSourceDTO> list = new ArrayList<IndicatorDataSourceDTO>();

        if(indicator.getDataSources() != null) {
            for(Indicator dataSource : indicator.getDataSources()) {
                list.add(mapper.map(dataSource, IndicatorDataSourceDTO.class));
            }
        }

        return new IndicatorDataSourceList(list);
    }
}
