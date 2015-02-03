package org.sigmah.server.handler;

import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.Synchronize;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SynchronizeHandler extends AbstractCommandHandler<Synchronize, VoidResult>{

    @Override
    protected VoidResult execute(Synchronize synchronize, UserDispatch.UserExecutionContext context) throws CommandException {
        for(final Command<?> command : synchronize.getCommands()) {
			if(command instanceof UpdateProject) {
				((UpdateProject)command).setComment("Synchronized from offline mode.");
			}
            context.execute(command);
        }
        return null;
    }
    
}
