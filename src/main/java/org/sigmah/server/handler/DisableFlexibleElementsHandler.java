package org.sigmah.server.handler;

import org.sigmah.shared.command.DisableFlexibleElements;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for the command {@link DisableFlexibleElements}.
 * 
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DisableFlexibleElementsHandler extends AbstractCommandHandler<DisableFlexibleElements, VoidResult> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DisableFlexibleElementsHandler.class);

	@Override
	protected VoidResult execute(DisableFlexibleElements command, UserDispatch.UserExecutionContext context) throws CommandException {
		if (command.getFlexibleElements() != null) {
			for (FlexibleElementDTO flexibleElementDTO : command.getFlexibleElements()) {
				final FlexibleElement flexibleElement = em().find(FlexibleElement.class, flexibleElementDTO.getId());
				flexibleElement.setDisabled(command.isDisable());
				em().merge(flexibleElement);
				
				LOGGER.debug("DisableFlexibleElementsHandler flexibleElement {} name {}.", flexibleElementDTO.getId(), flexibleElementDTO.getLabel());
			}
		}
		return null;
	}
}
