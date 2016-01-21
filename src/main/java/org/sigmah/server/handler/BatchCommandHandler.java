package org.sigmah.server.handler;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
