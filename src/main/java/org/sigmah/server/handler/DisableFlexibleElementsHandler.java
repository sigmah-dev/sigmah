package org.sigmah.server.handler;

import com.google.inject.persist.Transactional;
import java.util.Date;
import java.util.List;
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
			setDisabled(command.getFlexibleElements(), command.isDisable());
		}
		return null;
	}
	
	@Transactional
	protected void setDisabled(List<FlexibleElementDTO> elements, boolean disabled) {
		for (FlexibleElementDTO flexibleElementDTO : elements) {
			final FlexibleElement flexibleElement = em().find(FlexibleElement.class, flexibleElementDTO.getId());
			flexibleElement.setDisabledDate(disabled ? new Date() : null);
			em().merge(flexibleElement);

			LOGGER.debug("DisableFlexibleElementsHandler flexibleElement {} name {}.", flexibleElementDTO.getId(), flexibleElementDTO.getLabel());
		}
	}
}
