package org.sigmah.server.handler;

import java.util.ArrayList;
import org.sigmah.server.dispatch.FunctionalExceptions;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.Synchronize;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.FunctionalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SynchronizeHandler extends AbstractCommandHandler<Synchronize, ListResult<String>> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizeHandler.class);

    @Override
    protected ListResult<String> execute(Synchronize synchronize, UserDispatch.UserExecutionContext context) throws CommandException {
		final ArrayList<String> errors = new ArrayList<String>();
		
        for(final Command<?> command : synchronize.getCommands()) {
			try {
				if(command instanceof UpdateProject) {
					((UpdateProject)command).setComment("Synchronized from offline mode.");
				}
				context.execute(command);
				
			} catch(FunctionalException e) {
				errors.add(FunctionalExceptions.getMessage(context.getLanguage(), e));
			}
        }
		
		if(!errors.isEmpty()) {
			for(final String error : errors) {
				LOGGER.error(error);
			}
			
			// TODO: Send a mail to the current user.
		}
		
        return new ListResult<String>(errors);
    }
    
}
