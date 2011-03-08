package org.sigmah.shared.command.handler;

import org.sigmah.shared.command.GetSchema;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;

public class GetIndicatorsHandler  implements CommandHandler<GetSchema> {

	@Override
	public CommandResult execute(GetSchema cmd, User user)
			throws CommandException {
		// TODO Auto-generated method stub
		return null;
	}
}
