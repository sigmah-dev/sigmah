package org.sigmah.server.handler;

import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.AutomatedImport;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Handler for {@link AutomatedImport}.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class AutomatedImportHandler extends AbstractCommandHandler<AutomatedImport, Result> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Result execute(AutomatedImport command, UserDispatch.UserExecutionContext context) throws CommandException {
		
		// TODO: Write the import code.
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
}
