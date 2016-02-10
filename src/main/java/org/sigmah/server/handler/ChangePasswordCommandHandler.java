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

import com.google.inject.persist.Transactional;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.User;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.security.impl.BCrypt;
import org.sigmah.shared.command.ChangePasswordCommand;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.FunctionalException;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ChangePasswordCommandHandler extends AbstractCommandHandler<ChangePasswordCommand, VoidResult> {

	@Override
	protected VoidResult execute(ChangePasswordCommand command, UserDispatch.UserExecutionContext context) throws CommandException {
		final User user = context.getUser();
		
		if(!command.getNewPassword().equals(command.getConfirmNewPassword())) {
			throw new FunctionalException(FunctionalException.ErrorCode.AUTHENTICATION_FAILURE);
		}
		
		if (!BCrypt.checkpw(command.getCurrentPassword(), user.getHashedPassword())) {
			throw new FunctionalException(FunctionalException.ErrorCode.AUTHENTICATION_FAILURE);
		}
		
		updatePassword(user, command.getNewPassword());
		
		return null;
	}
	
	@Transactional
	public void updatePassword(User user, String newPassword) {
		final String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
		
		user.setHashedPassword(hashedPassword);
		
		// Desactivate password change key.
		user.setChangePasswordKey(null);
		
		em().merge(user);
	}
}
