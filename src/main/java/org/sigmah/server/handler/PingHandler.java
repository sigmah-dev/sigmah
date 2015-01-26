package org.sigmah.server.handler;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.Ping;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Handler for {@link Ping} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class PingHandler extends AbstractCommandHandler<Ping, VoidResult> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(Ping cmd, final UserExecutionContext context) throws CommandException {
		return new VoidResult();
	}

}
