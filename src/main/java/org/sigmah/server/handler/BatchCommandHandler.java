package org.sigmah.server.handler;

import java.util.ArrayList;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Handler for {@link BatchCommand} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class BatchCommandHandler extends AbstractCommandHandler<BatchCommand, ListResult<Result>> {

	@Override
	protected ListResult<Result> execute(BatchCommand commands, UserDispatch.UserExecutionContext context) throws CommandException {
		final ArrayList<Result> results = new ArrayList<Result>();
		
		for(final Command command : commands.getCommands()) {
			results.add(context.execute(command));
		}
		
		return new ListResult(results);
	}
	
}
