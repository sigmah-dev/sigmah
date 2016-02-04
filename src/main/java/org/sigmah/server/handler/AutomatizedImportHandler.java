package org.sigmah.server.handler;

import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.AutomatizedImport;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Handler for {@link AutomatizedImport}.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class AutomatizedImportHandler extends AbstractCommandHandler<AutomatizedImport, Result> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Result execute(AutomatizedImport command, UserDispatch.UserExecutionContext context) throws CommandException {
		
		// TODO: Write the import code.
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
}
