/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah.handler;

import java.util.ArrayList;
import java.util.Date;
import org.sigmah.shared.command.GetMonitoredPoints;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.MonitoredPointsResultList;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.exception.CommandException;

/**
 * Handler for the {@link GetMonitoredPoints} command. Use only local data.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetMonitoredPointsHandler implements CommandHandler<GetMonitoredPoints> {

    @Override
    public CommandResult execute(GetMonitoredPoints cmd, User user) throws CommandException {
        final ArrayList<MonitoredPointDTO> points = new ArrayList<MonitoredPointDTO>();

        final MonitoredPointDTO monitoredPointDTO = new MonitoredPointDTO();
        monitoredPointDTO.setId(1);
        monitoredPointDTO.setLabel("Bonjour Offline");
        monitoredPointDTO.setExpectedDate(new Date());

        points.add(monitoredPointDTO);

        final MonitoredPointsResultList resultList = new MonitoredPointsResultList(points);

        return resultList;
    }

}
